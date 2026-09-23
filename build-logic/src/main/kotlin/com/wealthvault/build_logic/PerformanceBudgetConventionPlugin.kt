package com.wealthvault.build_logic

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Properties

class PerformanceBudgetConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        check(project == project.rootProject) {
            "Performance budget verification must be registered on the root project"
        }

        project.tasks.register("verifyPerformanceBudgets", VerifyPerformanceBudgetsTask::class.java) {
            rootDirectory.set(project.layout.projectDirectory)
            budgetFile.set(project.layout.projectDirectory.file("performance/performance-budgets.properties"))
            val configuredMetrics = project.providers.gradleProperty("performanceMetrics").orNull
            if (configuredMetrics.isNullOrBlank()) {
                metricsPath.set(project.layout.buildDirectory.file("performance/metrics.properties").map { it.asFile.absolutePath })
            } else {
                // Keep the provider configuration-cache friendly. Relative
                // paths are resolved from rootDirectory during task execution.
                metricsPath.set(configuredMetrics)
            }
            baselineMetricsPath.set(
                project.providers.gradleProperty("performanceBaselineMetrics")
                    .orElse(""),
            )
            confirmationMetricsPath.set(
                project.providers.gradleProperty("performanceConfirmationMetrics")
                    .orElse(""),
            )
            strict.set(
                project.providers.gradleProperty("strictPerformance")
                    .map(String::toBoolean)
                    .orElse(false),
            )
        }

        project.tasks.register("collectPerformanceMetrics", CollectPerformanceMetricsTask::class.java) {
            rootDirectory.set(project.layout.projectDirectory)
            budgetFile.set(project.layout.projectDirectory.file("performance/performance-budgets.properties"))
            inputDirectoryPath.set(
                project.providers.gradleProperty("performanceInputs")
                    .orElse(project.layout.buildDirectory.dir("performance/inputs").map { it.asFile.absolutePath }),
            )
            outputFile.set(project.layout.buildDirectory.file("performance/metrics.properties"))
            // Exporters run outside Gradle and may create this directory later.
            // Always inspect it when explicitly requested instead of letting a
            // missing optional directory fail Gradle's input validation first.
            outputs.upToDateWhen { false }
        }
    }
}

/**
 * Combines measured exports from Android Macrobenchmark, iOS signposts and
 * build artifacts into the single properties file consumed by the strict
 * performance gate. It deliberately fails when inputs or budget keys are
 * missing; it never fills a metric with a guessed or zero value.
 */
abstract class CollectPerformanceMetricsTask : DefaultTask() {
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
        val required = budget.keys.filterNot {
            it in setOf("schema_version", "minimum_sample_count", "regression_tolerance_percent")
        }.toSet()
        val inputRoot = inputDirectoryPath.orNull?.let(::resolveInputDirectory)
        val files = if (inputRoot?.isDirectory == true) {
            inputRoot.walkTopDown()
                .filter { file -> file.isFile && file.extension == "properties" }
                .sortedBy(File::getName)
                .toList()
        } else {
            emptyList()
        }
        if (files.isEmpty()) {
            throw GradleException(
                "No performance exporter properties found in ${inputRoot?.path ?: "<missing input directory>"}. " +
                    "Export five-run Android/iOS measurements there before collecting metrics.",
            )
        }

        val merged = linkedMapOf<String, String>()
        files.forEach { file ->
            load(file).forEach { (key, value) ->
                if (value.trim().startsWith("<")) {
                    throw GradleException(
                        "Unmeasured performance metric '$key' found in ${file.path}: $value",
                    )
                }
                val previous = merged[key]
                if (previous != null && previous != value) {
                    throw GradleException(
                        "Conflicting performance metric '$key': $previous vs $value (${file.path})",
                    )
                }
                merged[key] = value
            }
        }

        val sampleCount = merged["sample_count"]?.toIntOrNull()
            ?: throw GradleException("Performance exporter properties must define sample_count")
        if (sampleCount <= 0) {
            throw GradleException("Performance exporter sample_count must be positive")
        }
        val minimumSamples = budget["minimum_sample_count"]?.toIntOrNull()
            ?: throw GradleException("performance budget must define minimum_sample_count")
        if (sampleCount < minimumSamples) {
            throw GradleException(
                "Performance exports contain $sampleCount samples; at least $minimumSamples are required",
            )
        }
        val missing = required.filterNot(merged::containsKey)
        if (missing.isNotEmpty()) {
            throw GradleException(
                "Performance exporter properties are missing required metrics: ${missing.sorted().joinToString()}",
            )
        }
        val invalidNumbers = required.filter { metric ->
            val value = merged[metric]?.toDoubleOrNull()
            value == null || !value.isFinite() || value < 0.0
        }
        if (invalidNumbers.isNotEmpty()) {
            throw GradleException(
                "Performance exporter properties contain invalid values: ${invalidNumbers.sorted().joinToString()}",
            )
        }
        merged.putIfAbsent("schema_version", budget["schema_version"] ?: "1")

        val output = outputFile.get().asFile
        output.parentFile.mkdirs()
        output.printWriter().use { writer ->
            writer.println("# Generated from measured exporter properties; do not edit.")
            merged.toSortedMap().forEach { (key, value) -> writer.println("$key=$value") }
        }
        logger.lifecycle("performance: collected ${merged.size} metrics from ${files.size} exporter files")
    }

    private fun load(file: File): Map<String, String> {
        return Properties().also { properties ->
            file.inputStream().use(properties::load)
        }.entries.associate { (key, value) -> key.toString() to value.toString() }
    }

    private fun resolveInputDirectory(path: String): File {
        val configured = File(path)
        return if (configured.isAbsolute) configured else rootDirectory.get().asFile.resolve(path)
    }

}

abstract class VerifyPerformanceBudgetsTask : DefaultTask() {
    @get:Internal
    abstract val rootDirectory: org.gradle.api.file.DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val budgetFile: RegularFileProperty

    @get:Input
    abstract val metricsPath: Property<String>

    /** Optional main-branch metrics used for the ten-percent regression ratchet. */
    @get:Input
    @get:Optional
    abstract val baselineMetricsPath: Property<String>

    /** Optional second run used to confirm a regression before failing CI. */
    @get:Input
    @get:Optional
    abstract val confirmationMetricsPath: Property<String>

    @get:Input
    abstract val strict: Property<Boolean>

    @TaskAction
    fun verify() {
        val budgets = load(budgetFile.get().asFile)
        requireValue(budgets, "schema_version")
        val requiredSamples = budgets["minimum_sample_count"]?.toIntOrNull()
            ?: throw GradleException("performance budget must define minimum_sample_count")
        if (requiredSamples < 5) {
            throw GradleException("minimum_sample_count must be at least 5")
        }

        val metricsPath = resolvePath(metricsPath.get())
        if (!metricsPath.isFile) {
            val message = "No measured performance metrics found at ${metricsPath.path}. " +
                "Run Macrobenchmark/XCTest and pass -PperformanceMetrics=<file>."
            if (strict.get()) throw GradleException(message)
            logger.lifecycle("performance: pending ($message)")
            return
        }

        val metrics = load(metricsPath)
        val sampleCount = metrics["sample_count"]?.toIntOrNull()
            ?: throw GradleException("performance metrics must define sample_count")
        if (sampleCount <= 0) {
            throw GradleException("performance metrics sample_count must be positive")
        }
        if (sampleCount < requiredSamples) {
            throw GradleException(
                "performance metrics contain $sampleCount samples; at least $requiredSamples are required",
            )
        }

        val ignoredKeys = setOf(
            "schema_version",
            "minimum_sample_count",
            "regression_tolerance_percent",
        )
        val failures = mutableListOf<String>()
        budgets.filterKeys { it !in ignoredKeys }.forEach { (metric, budgetValue) ->
            val budget = budgetValue.toDoubleOrNull()
                ?: throw GradleException("Invalid budget for $metric: $budgetValue")
            val actualValue = metrics[metric]?.toDoubleOrNull()
            if (actualValue == null) {
                failures += "$metric is missing from ${metricsPath.path}"
            } else if (!actualValue.isFinite() || actualValue < 0.0) {
                failures += "$metric=$actualValue is not a finite non-negative value"
            } else if (actualValue > budget) {
                failures += "$metric=$actualValue exceeds budget $budget"
            } else {
                logger.lifecycle("performance: $metric=$actualValue budget=$budget pass")
            }
        }

        verifyRegressionRatchet(
            budgets = budgets,
            metrics = metrics,
            failures = failures,
        )

        if (failures.isNotEmpty()) {
            throw GradleException(
                "Performance budget failures:\n${failures.joinToString("\n") { "- $it" }}",
            )
        }
    }

    private fun verifyRegressionRatchet(
        budgets: Map<String, String>,
        metrics: Map<String, String>,
        failures: MutableList<String>,
    ) {
        val baselinePath = baselineMetricsPath.orNull?.trim().orEmpty()
        if (baselinePath.isBlank()) {
            logger.lifecycle(
                "performance: baseline comparison pending; pass " +
                    "-PperformanceBaselineMetrics=<main metrics file> to enable the regression ratchet",
            )
            return
        }

        val baselineFile = resolvePath(baselinePath)
        if (!baselineFile.isFile) {
            throw GradleException("Missing performance baseline metrics file: $baselinePath")
        }
        val baseline = load(baselineFile)
        val tolerance = budgets["regression_tolerance_percent"]?.toDoubleOrNull()
            ?: throw GradleException("performance budget must define regression_tolerance_percent")
        if (tolerance < 0.0) {
            throw GradleException("regression_tolerance_percent must not be negative")
        }

        val candidates = budgets.keys
            .filter { it !in setOf("schema_version", "minimum_sample_count", "regression_tolerance_percent") }
            .mapNotNull { metric ->
                val current = metrics[metric]?.toDoubleOrNull() ?: return@mapNotNull null
                val previous = baseline[metric]?.toDoubleOrNull() ?: return@mapNotNull null
                // Zero is commonly used in the checked-in example file and is
                // not a meaningful baseline for a relative comparison.
                if (previous <= 0.0) return@mapNotNull null
                val limit = previous * (1.0 + tolerance / 100.0)
                metric.takeIf { current > limit }?.let {
                    Regression(metric, current, previous, limit)
                }
            }

        if (candidates.isEmpty()) {
            logger.lifecycle("performance: baseline regression ratchet pass")
            return
        }

        val confirmationPath = confirmationMetricsPath.orNull?.trim().orEmpty()
        if (confirmationPath.isBlank()) {
            val message = "Performance regression suspected (> $tolerance%): " +
                candidates.joinToString { "${it.metric}=${it.current} baseline=${it.baseline}" } +
                ". Run once more and pass -PperformanceConfirmationMetrics=<file>."
            if (strict.get()) throw GradleException(message)
            logger.warn("performance: $message")
            return
        }

        val confirmationFile = resolvePath(confirmationPath)
        if (!confirmationFile.isFile) {
            throw GradleException("Missing performance confirmation metrics file: $confirmationPath")
        }
        val confirmation = load(confirmationFile)
        val confirmed = candidates.filter { candidate ->
            val value = confirmation[candidate.metric]?.toDoubleOrNull()
            value != null && value > candidate.limit
        }
        if (confirmed.isNotEmpty()) {
            failures += confirmed.joinToString("; ") { candidate ->
                "${candidate.metric} confirmed regression=${confirmation[candidate.metric]} " +
                    "baseline=${candidate.baseline} limit=${candidate.limit}"
            }
        } else {
            logger.lifecycle("performance: suspected regression cleared by confirmation run")
        }
    }

    private data class Regression(
        val metric: String,
        val current: Double,
        val baseline: Double,
        val limit: Double,
    )

    private fun load(file: java.io.File): Map<String, String> {
        if (!file.isFile) throw GradleException("Missing performance properties file: ${file.path}")
        return Properties().also { properties ->
            file.inputStream().use(properties::load)
        }.entries.associate { (key, value) -> key.toString() to value.toString() }
    }

    private fun resolvePath(path: String): java.io.File {
        val file = java.io.File(path)
        return if (file.isAbsolute) file else rootDirectory.get().asFile.resolve(path)
    }

    private fun requireValue(values: Map<String, String>, key: String) {
        if (values[key].isNullOrBlank()) {
            throw GradleException("performance budget must define $key")
        }
    }
}
