package com.wealthvault.build_logic

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Properties

/** Registers a strict, report-driven coverage gate without inventing coverage data. */
class CoverageConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        check(project == project.rootProject) {
            "Coverage verification must be registered on the root project"
        }

        project.tasks.register("verifyCoverage", VerifyCoverageTask::class.java) {
            rootDirectory.set(project.layout.projectDirectory)
            budgetFile.set(project.layout.projectDirectory.file("performance/coverage-budgets.properties"))
            val configuredMetrics = project.providers.gradleProperty("coverageMetrics").orNull
            if (configuredMetrics.isNullOrBlank()) {
                metricsPath.set(
                    project.layout.buildDirectory
                        .file("coverage/metrics.properties")
                        .map { it.asFile.absolutePath },
                )
            } else {
                metricsPath.set(configuredMetrics)
            }
            strict.set(
                project.providers.gradleProperty("strictCoverage")
                    .map(String::toBoolean)
                    .orElse(false),
            )
        }

        val collectCoverageMetrics = project.tasks.register("collectCoverageMetrics", CollectCoverageMetricsTask::class.java) {
            rootDirectory.set(project.layout.projectDirectory)
            budgetFile.set(project.layout.projectDirectory.file("performance/coverage-budgets.properties"))
            inputDirectoryPath.set(
                project.providers.gradleProperty("coverageInputs")
                    .orElse(project.layout.buildDirectory.dir("coverage/inputs").map { it.asFile.absolutePath }),
            )
            outputFile.set(project.layout.buildDirectory.file("coverage/metrics.properties"))
            // Kover/XCTest exporters are external to Gradle and can create the
            // directory after configuration. Keep the failure in the task
            // action so it explains which measured inputs are missing.
            outputs.upToDateWhen { false }
        }
        // Keep a combined `collectCoverageMetrics verifyCoverage` invocation
        // deterministic without changing the pending behavior of verify alone.
        project.tasks.named("verifyCoverage") {
            mustRunAfter(collectCoverageMetrics)
        }
    }
}

/**
 * Merges line/branch coverage exports from Android and iOS without inventing
 * a combined percentage. Exporters must provide the canonical metric keys
 * from coverage-budgets.properties; conflicting values are rejected.
 */
abstract class CollectCoverageMetricsTask : DefaultTask() {
    @get:Internal
    abstract val rootDirectory: org.gradle.api.file.DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val budgetFile: RegularFileProperty

    @get:Input
    abstract val inputDirectoryPath: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun collect() {
        val budget = load(budgetFile.get().asFile)
        val required = budget.keys.filterNot { it == "schema_version" }.toSet()
        val inputRoot = inputDirectoryPath.orNull?.let(::resolvePath)
        val files = if (inputRoot?.isDirectory == true) {
            inputRoot.walkTopDown()
                .filter { file -> file.isFile && file.extension == "properties" }
                .sortedBy { it.name }
                .toList()
        } else {
            emptyList()
        }
        if (files.isEmpty()) {
            throw GradleException(
                "No coverage exporter properties found in ${inputRoot?.path ?: "<missing input directory>"}. " +
                    "Export Android Kover and iOS XCTest coverage there before collecting metrics.",
            )
        }

        val merged = linkedMapOf<String, String>()
        files.forEach { file ->
            load(file).forEach { (key, value) ->
                if (value.trim().startsWith("<")) {
                    throw GradleException(
                        "Unmeasured coverage metric '$key' found in ${file.path}: $value",
                    )
                }
                val previous = merged[key]
                if (previous != null && previous != value) {
                    throw GradleException(
                        "Conflicting coverage metric '$key': $previous vs $value (${file.path})",
                    )
                }
                merged[key] = value
            }
        }

        val invalid = merged.filterKeys { it in required }.filterValues {
            it.toDoubleOrNull()?.let { value -> value !in 0.0..100.0 } ?: true
        }.keys
        if (invalid.isNotEmpty()) {
            throw GradleException("Coverage metrics must be percentages between 0 and 100: ${invalid.sorted().joinToString()}")
        }
        val missing = required.filterNot(merged::containsKey)
        if (missing.isNotEmpty()) {
            throw GradleException(
                "Coverage exporter properties are missing required metrics: ${missing.sorted().joinToString()}",
            )
        }
        merged.putIfAbsent("schema_version", budget["schema_version"] ?: "1")

        val output = outputFile.get().asFile
        output.parentFile.mkdirs()
        output.printWriter().use { writer ->
            writer.println("# Generated from measured exporter properties; do not edit.")
            merged.toSortedMap().forEach { (key, value) -> writer.println("$key=$value") }
        }
        logger.lifecycle("coverage: collected ${merged.size} metrics from ${files.size} exporter files")
    }

    private fun load(file: File): Map<String, String> {
        return Properties().also { properties ->
            file.inputStream().use(properties::load)
        }.entries.associate { (key, value) -> key.toString() to value.toString() }
    }

    private fun resolvePath(path: String): File {
        val file = File(path)
        return if (file.isAbsolute) file else rootDirectory.get().asFile.resolve(path)
    }
}

abstract class VerifyCoverageTask : DefaultTask() {
    @get:Internal
    abstract val rootDirectory: DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val budgetFile: RegularFileProperty

    @get:Input
    abstract val metricsPath: Property<String>

    @get:Input
    abstract val strict: Property<Boolean>

    @TaskAction
    fun verify() {
        val budgets = load(budgetFile.get().asFile)
        requireValue(budgets, "schema_version")
        val metricsFile = resolvePath(metricsPath.get())
        if (!metricsFile.isFile) {
            val message = "No coverage report found at ${metricsFile.path}. " +
                "Run the Android host/Kover and iOS XCTest exporters, then pass " +
                "-PcoverageMetrics=<file>."
            if (strict.get()) throw GradleException(message)
            logger.lifecycle("coverage: pending ($message)")
            return
        }

        val metrics = load(metricsFile)
        val failures = mutableListOf<String>()
        budgets
            .filterKeys { it != "schema_version" }
            .forEach { (metric, minimumText) ->
                val minimum = minimumText.toDoubleOrNull()
                    ?: throw GradleException("Invalid coverage budget for $metric: $minimumText")
                requirePercent(metric, minimum)
                val actual = metrics[metric]?.toDoubleOrNull()
                if (actual == null) {
                    failures += "$metric is missing from ${metricsFile.path}"
                } else {
                    requirePercent(metric, actual)
                    if (actual < minimum) {
                        failures += "$metric=$actual is below minimum $minimum"
                    } else {
                        logger.lifecycle("coverage: $metric=$actual minimum=$minimum pass")
                    }
                }
            }

        if (failures.isNotEmpty()) {
            throw GradleException("Coverage gate failures:\n${failures.joinToString("\n") { "- $it" }}")
        }
    }

    private fun resolvePath(path: String): File {
        val file = File(path)
        return if (file.isAbsolute) file else rootDirectory.get().asFile.resolve(path)
    }

    private fun load(file: File): Map<String, String> {
        if (!file.isFile) throw GradleException("Missing coverage properties file: ${file.path}")
        return Properties().also { properties ->
            file.inputStream().use(properties::load)
        }.entries.associate { (key, value) -> key.toString() to value.toString() }
    }

    private fun requireValue(values: Map<String, String>, key: String) {
        if (values[key].isNullOrBlank()) throw GradleException("Coverage budget must define $key")
    }

    private fun requirePercent(metric: String, value: Double) {
        if (value !in 0.0..100.0) {
            throw GradleException("Coverage value for $metric must be between 0 and 100: $value")
        }
    }
}
