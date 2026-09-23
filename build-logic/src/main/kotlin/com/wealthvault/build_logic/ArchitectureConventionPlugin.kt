package com.wealthvault.build_logic

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.util.regex.Pattern

class ArchitectureConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        check(project == project.rootProject) {
            "Architecture verification must be registered on the root project"
        }

        project.tasks.register("verifyArchitecture", VerifyArchitectureTask::class.java) {
            rootDirectory.set(project.layout.projectDirectory)
            sourceInputs.from(project.fileTree(project.projectDir) {
                include("**/src/**/*.kt", "**/src/**/*.kts")
                exclude(
                    "**/build/**",
                    "**/test/**",
                    "**/androidTest/**",
                    "**/commonTest/**",
                    "**/androidHostTest/**",
                    "**/androidDeviceTest/**",
                )
            })
            featureBuildInputs.from(project.fileTree(project.projectDir.resolve("features")) {
                include("**/build.gradle.kts")
                exclude("**/build/**")
            })
            projectBuildInputs.from(project.fileTree(project.projectDir) {
                include("**/build.gradle.kts")
                exclude("**/build/**", "build-logic/**")
            })
            baselineFile.set(project.layout.projectDirectory.file(".architecture-baseline"))
            reportFile.set(project.layout.buildDirectory.file("reports/architecture/violations.txt"))
            strict.set(
                project.providers.gradleProperty("strictArchitecture")
                    .map(String::toBoolean)
                    .orElse(false),
            )
        }
    }
}

@DisableCachingByDefault(because = "The task scans source files and emits a migration report.")
abstract class VerifyArchitectureTask : DefaultTask() {
    /** Only source files are inputs; generated build outputs are deliberately excluded. */
    @get:Internal
    abstract val rootDirectory: DirectoryProperty

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceInputs: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val featureBuildInputs: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val projectBuildInputs: ConfigurableFileCollection

    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val baselineFile: RegularFileProperty

    @get:OutputFile
    abstract val reportFile: RegularFileProperty

    @get:Input
    abstract val strict: Property<Boolean>

    @TaskAction
    fun verify() {
        val root = rootDirectory.get().asFile
        val featureSources = sourceInputs.files.filter { it.path.contains("${File.separator}features${File.separator}") }
        // A Gradle edge is not the only way one feature can become coupled to
        // another: a source file can also import a public type from a sibling
        // feature when both happen to be on the app classpath. Keep the
        // package groups explicit so imports within one bounded feature (for
        // example login + password recovery) remain valid while sibling
        // feature imports are rejected by the same zero-based ratchet.
        val featurePackageGroups = linkedMapOf(
            "${File.separator}features${File.separator}auth${File.separator}login${File.separator}" to setOf(
                "com.wealthvault.login.",
                "com.wealthvault.forgetpassword.",
                "com.wealthvault.introduction.",
                "com.wealthvault.splashscreen.",
                "com.wealthvault.register.",
            ),
            "${File.separator}features${File.separator}dashboard${File.separator}" to setOf(
                "com.wealthvault.dashboard.",
            ),
            "${File.separator}features${File.separator}notification${File.separator}" to setOf(
                "com.wealthvault.notification.",
            ),
            "${File.separator}features${File.separator}profile${File.separator}" to setOf(
                "com.wealthvault.profile.",
            ),
            "${File.separator}features${File.separator}social${File.separator}" to setOf(
                "com.wealthvault.social.",
            ),
            "${File.separator}features${File.separator}manage${File.separator}financialList${File.separator}" to setOf(
                "com.wealthvault.financiallist.",
            ),
        )
        val allFeaturePackageRoots = featurePackageGroups.values.flatten().toSet()
        val productionSources = sourceInputs.files.filterNot { it.path.contains("${File.separator}build-logic${File.separator}") }
        // Presentation gates apply to feature modules and to the compatibility
        // bundle while it is being retired. Data adapters remain the only
        // allowed consumers of transport DTOs.
        val compatibilitySources = productionSources.filter { file ->
            file.path.contains("${File.separator}base${File.separator}financial-common${File.separator}")
        }
        val compositionRootSources = productionSources.filter { file ->
            file.path.contains("${File.separator}composeApp${File.separator}") ||
                file.path.contains("${File.separator}androidApp${File.separator}")
        }
        val presentationSources = (featureSources + compatibilitySources).filter { file ->
            file.path.contains("${File.separator}ui${File.separator}") ||
                file.path.contains("${File.separator}usecase${File.separator}") ||
                file.name.endsWith("ViewModel.kt") ||
                file.name.endsWith("ScreenModel.kt")
        }
        val domainSources = productionSources.filter {
            it.path.contains("${File.separator}domain${File.separator}")
        }
        val featureBuildFiles = featureBuildInputs.files
        val domainBuildFiles = projectBuildInputs.files.filter {
            it.path.contains("${File.separator}domain${File.separator}")
        }
        val featureLayerBuildFiles = projectBuildInputs.files.filter {
            it.path.contains("${File.separator}features${File.separator}")
        }
        val dataBuildFiles = projectBuildInputs.files.filter {
            it.path.contains("${File.separator}data${File.separator}")
        }
        // Legacy transport sources are compiled by bounded-context data
        // modules during the staged migration. Scan them with the data layer
        // as well, so a compatibility adapter cannot import presentation or
        // app code while it is being retired.
        val dataSources = productionSources.filter { file ->
            file.path.contains("${File.separator}data${File.separator}") ||
                file.path.contains("${File.separator}functional${File.separator}api${File.separator}") ||
                file.path.contains("${File.separator}functional${File.separator}data-store${File.separator}") ||
                file.path.contains("${File.separator}functional${File.separator}notification${File.separator}")
        }
        val activeDataTransportSources = productionSources.filter { file ->
            // API interfaces/implementations and Koin modules are public by
            // design inside data. The DTO boundary is the model directory;
            // scan only those files so the ratchet enforces internal wire
            // models without mistaking the transport facade for a DTO leak.
            file.path.contains("${File.separator}data${File.separator}") &&
                file.path.contains("${File.separator}model${File.separator}") &&
                (
                    file.path.contains("${File.separator}transport${File.separator}") ||
                        file.path.contains("_api${File.separator}") ||
                        file.path.contains("-api${File.separator}")
                    )
        }
        val legacyApiModelSources = productionSources.filter { file ->
            file.path.contains("${File.separator}functional${File.separator}api${File.separator}") &&
                file.path.contains("${File.separator}model${File.separator}") &&
                !file.path.contains(
                    "${File.separator}functional${File.separator}api${File.separator}line-auth${File.separator}",
                )
        }
        val nonDataBoundarySources = featureSources + domainSources + compatibilitySources + compositionRootSources

        val apiModelImportPattern = Pattern.compile(
            "(?m)^\\s*import .*com\\.wealthvault\\.(?:[A-Za-z0-9_]+_api|`[^`]+-api`)\\.model\\.|" +
                "com\\.wealthvault\\.data\\.(?:auth|notification|portfolio|social)\\..*\\.transport\\.model\\.",
        )
        val apiImportOutsideDataPattern = Pattern.compile(
            "(?m)^\\s*import .*com\\.wealthvault\\.(?:[A-Za-z0-9_]+_api|`[^`]+-api`)\\.|" +
                "com\\.wealthvault\\.data\\.(?:auth|notification|portfolio|social)\\..*\\.(?:transport|wire)\\.",
        )
        val legacyTransportImportPattern = Pattern.compile(
            "(?m)^\\s*import\\s+com\\.wealthvault(?:_final)?\\.(?:[A-Za-z0-9_]+_api|`[^`]+-api`|setup_api|google_auth)(?:\\.|$)|" +
                "^\\s*import\\s+com\\.wealthvault\\.data\\.(?:auth|notification|portfolio|social)\\..*\\.(?:transport|wire)(?:\\.|$)",
        )
        val legacyModelImportPattern = Pattern.compile(
            "(?m)^\\s*import .*com\\.wealthvault_final\\..*\\.model\\.",
        )
        val metrics = linkedMapOf(
            "feature_to_feature_dependencies" to countMatches(
                featureBuildFiles,
                Pattern.compile("project\\(\\\":features:"),
            ),
            "feature_to_feature_imports" to countFeatureToFeatureImports(
                featureSources = featureSources,
                featurePackageGroups = featurePackageGroups,
                allFeaturePackageRoots = allFeaturePackageRoots,
            ),
            "feature_to_data_dependencies" to countMatches(
                featureBuildFiles,
                Pattern.compile("project\\(\\\":data:"),
            ),
            // Presentation modules may use domain contracts and core UI
            // primitives, but must not link legacy storage/transport
            // implementations.  Keep the rule explicit so a future feature
            // cannot reintroduce DataStore, network, or compatibility edges
            // while the staged migration is still in progress.
            "feature_to_infrastructure_dependencies" to countMatches(
                featureBuildFiles,
                Pattern.compile("project\\(\\\":(?:functional:|base:(?:config|database|network)):"),
            ),
            // Compatibility presentation bundles are assembled at the
            // composition root while their remaining consumers migrate. A
            // feature must not reintroduce that dependency after it has been
            // removed from the active graph.
            "feature_to_compatibility_dependencies" to countMatches(
                featureBuildFiles,
                Pattern.compile("project\\(\\\":base:financial-common\\\"")
            ),
            "feature_to_app_dependencies" to countMatches(
                featureLayerBuildFiles,
                Pattern.compile("project\\(\\\":(?:androidApp|composeApp|main):"),
            ),
            "domain_to_forbidden_dependencies" to countMatches(
                domainBuildFiles,
                Pattern.compile("project\\(\\\":(?:features|functional|androidApp|composeApp):"),
            ),
            "domain_to_domain_dependencies" to countMatches(
                domainBuildFiles,
                Pattern.compile("project\\(\\\":domain:"),
            ),
            "data_to_feature_dependencies" to countMatches(
                dataBuildFiles,
                Pattern.compile("project\\(\\\":features:"),
            ),
            "data_to_data_dependencies" to countMatches(
                dataBuildFiles,
                Pattern.compile("project\\(\\\":data:"),
            ),
            "data_to_app_dependencies" to countMatches(
                dataBuildFiles,
                Pattern.compile("project\\(\\\":(?:androidApp|composeApp|main):"),
            ),
            "compatibility_to_data_dependencies" to countMatches(
                projectBuildInputs.filter { file ->
                    file.path.contains("${File.separator}base${File.separator}financial-common${File.separator}")
                },
                Pattern.compile("project\\(\\\":data:"),
            ),
            // Legacy source trees are preserved for rollback, but no active
            // Gradle source set may compile directly from those archives.
            "legacy_source_root_edges" to countMatches(
                projectBuildInputs,
                Pattern.compile(
                    "(?s)kotlin\\.srcDirs\\([^)]*functional/(?:api|data-store|notification)/src",
                ),
            ),
            "feature_source_root_edges" to countMatches(
                featureLayerBuildFiles,
                Pattern.compile("(?s)kotlin\\.srcDirs?\\([^)]*features/[^)]*/src"),
            ),
            "data_presentation_imports" to countMatches(
                dataSources,
                Pattern.compile(
                    "(?m)^\\s*import .*com\\.wealthvault(?:_final)?\\.(?:features|composeApp|androidApp|main)\\.",
                ),
            ),
            "public_legacy_api_model_declarations" to countMatches(
                legacyApiModelSources,
                Pattern.compile(
                    "(?m)^\\s*(?!(?:internal|private|protected)\\b)(?:(?:data|sealed|enum|value|annotation)\\s+)?(?:class|interface|object)\\s+[A-Za-z0-9_`]+",
                ),
            ),
            "public_data_transport_declarations" to countMatches(
                activeDataTransportSources,
                Pattern.compile(
                    "(?m)^\\s*(?:(?:public)\\s+)?(?!(?:internal|private|protected)\\b)" +
                        "(?:(?:data|sealed|enum|value|annotation|open|abstract)\\s+)*" +
                        "(?:class|interface|object|typealias)\\s+[A-Za-z0-9_`]+",
                ),
            ),
            "feature_api_model_imports" to countMatches(
                featureSources,
                apiModelImportPattern,
            ),
            "presentation_api_model_imports" to countMatches(
                presentationSources,
                apiModelImportPattern,
            ),
            "presentation_legacy_model_imports" to countMatches(
                presentationSources,
                legacyModelImportPattern,
            ),
            "presentation_repository_impl_imports" to countMatches(
                presentationSources,
                Pattern.compile("(?m)^\\s*import .*RepositoryImpl(?:\\s|$)"),
            ),
            "presentation_data_package_imports" to countMatches(
                presentationSources,
                Pattern.compile("(?m)^\\s*import .*\\.data\\."),
            ),
            "compatibility_api_model_imports" to countMatches(
                compatibilitySources,
                apiModelImportPattern,
            ),
            "composition_root_legacy_transport_imports" to countMatches(
                compositionRootSources,
                legacyTransportImportPattern,
            ),
            "composition_root_session_storage_imports" to countMatches(
                compositionRootSources,
                Pattern.compile("(?m)^\\s*import .*com\\.wealthvault\\.data_store\\."),
            ),
            "domain_api_model_imports" to countMatches(
                domainSources,
                apiModelImportPattern,
            ),
            "api_qualified_references_outside_data" to countMatches(
                nonDataBoundarySources,
                Pattern.compile(
                    "com\\.wealthvault(?:_final)?\\.[A-Za-z0-9_`-]*(?:_api|api)\\.|" +
                        "com\\.wealthvault\\.data\\.(?:auth|notification|portfolio|social)\\..*\\.(?:transport|wire)\\.",
                ),
            ),
            "domain_infrastructure_imports" to countMatches(
                domainSources,
                Pattern.compile(
                    "(?m)^\\s*import .*com\\.wealthvault\\.(?:data_store|network|database|security)\\.",
                ),
            ),
            "domain_transport_serialization_imports" to countMatches(
                domainSources,
                Pattern.compile("(?m)^\\s*import kotlinx\\.serialization(?:\\.|$)"),
            ),
            "domain_transport_serialization_dependencies" to countMatches(
                domainBuildFiles,
                Pattern.compile("kotlin(?:x)?\\.serialization|ktor\\.serialization"),
            ),
            // Monetary values and financial rates are represented by the
            // fixed-point domain contracts (Money/FixedDecimal).  Keep the
            // real-estate area compatibility fields out of this rule: area
            // is a physical measurement, not a monetary amount.
            "domain_floating_point_financial_fields" to countMatches(
                domainSources,
                Pattern.compile(
                    "(?im)^\\s*(?:val|var)\\s+" +
                        "(?:amount|principal|coverageAmount|costPerPrice|totalAssets|" +
                        "totalLiabilities|quantity|interestRate|purchasePrice|sellingPrice|" +
                        "unitPrice|totalValue|currentValue|cashFlow|income|expense|" +
                        "premium|balance|fee|tax)\\s*:\\s*(?:Double|Float)(?:\\?)?\\b",
                ),
            ),
            "presentation_session_storage_imports" to countMatches(
                presentationSources,
                Pattern.compile(
                    "(?m)^\\s*import .*com\\.wealthvault\\.(?:data_store|security)\\.",
                ),
            ),
            "feature_ktorfit_imports" to countMatches(
                featureSources,
                Pattern.compile("(?m)^\\s*import .*ktorfit"),
            ),
            "feature_transport_serialization_imports" to countMatches(
                featureSources,
                Pattern.compile(
                    "(?m)^\\s*import (?:io\\.ktor\\.|kotlinx\\.serialization\\.json\\.)",
                ),
            ),
            "feature_transport_serialization_dependencies" to countMatches(
                featureBuildFiles,
                Pattern.compile("kotlinx\\.serialization|ktor\\.serialization|ktorfit|datastore"),
            ),
            "presentation_ktorfit_imports" to countMatches(
                presentationSources + domainSources,
                Pattern.compile("(?m)^\\s*import .*ktorfit"),
            ),
            "presentation_session_implementation_imports" to countMatches(
                presentationSources,
                Pattern.compile(
                    "(?m)^\\s*import .*com\\.wealthvault\\.data_store\\.(?:TokenStore|SecureStorage|AndroidSecureStorage|IosSecureStorage)",
                ),
            ),
            "presentation_websocket_implementation_imports" to countMatches(
                presentationSources,
                Pattern.compile("(?m)^\\s*import .*com\\.wealthvault\\.websocket_api\\."),
            ),
            "api_imports_outside_data" to countMatches(
                (featureSources + compatibilitySources).filter { file ->
                    !file.path.contains("${File.separator}data${File.separator}")
                },
                apiImportOutsideDataPattern,
            ),
            "feature_infrastructure_imports" to countMatches(
                featureSources,
                Pattern.compile(
                    "(?m)^\\s*import .*com\\.wealthvault\\.(?:data_store|config|network)\\.",
                ),
            ),
            "lifecycle_resume_observers" to countMatches(
                featureSources,
                Pattern.compile("Lifecycle\\.Event\\.ON_RESUME"),
            ),
            "production_println_calls" to countMatches(
                productionSources,
                Pattern.compile("(?m)(^|[^A-Za-z0-9_])println\\("),
            ),
            "production_print_calls" to countMatches(
                productionSources,
                Pattern.compile("(?m)(^|[^A-Za-z0-9_])print\\("),
            ),
            "legacy_namespace_references" to countMatches(
                productionSources,
                Pattern.compile("(?m)^\\s*(?:package|import) .*wealthvault_final"),
            ),
            "screen_model_udf_gaps" to countFilesWithoutUdfContract(
                productionSources.filter { file ->
                    file.name.endsWith("ScreenModel.kt") ||
                        Pattern.compile(
                            "(?m)^\\s*class\\s+[A-Za-z0-9_`]+[^\\n]*ScreenModel",
                        ).matcher(stripComments(file.readText())).find()
                },
            ),
            // Route files should own navigation and lifecycle wiring only.
            // Large content belongs in dedicated composables/components so a
            // screen can be reviewed and tested without a monolithic route.
            "large_screen_route_files" to countLargeScreenRouteFiles(featureSources),
        )


        val baseline = if (baselineFile.isPresent && baselineFile.get().asFile.isFile) {
            baselineFile.get().asFile.readLines()
                .filter { it.contains('=') && !it.trimStart().startsWith("#") }
                .associate { line ->
                    val (key, value) = line.split('=', limit = 2)
                    key.trim() to value.trim().toInt()
                }
        } else {
            emptyMap()
        }

        // Keep the bounded-context graph reviewable. This is a hard budget,
        // not a ratcheted violation count, so adding a module requires an
        // intentional architecture decision rather than silently expanding
        // the build graph.
        val activeGradleModuleCount = root.resolve("settings.gradle.kts")
            .takeIf { it.isFile }
            ?.readLines()
            ?.count { line ->
                val trimmed = line.trim()
                trimmed.startsWith("include(") && !trimmed.startsWith("//")
            }
            ?: 0
        val maxGradleModules = 35

        val regressions = metrics.filter { (key, value) -> value > (baseline[key] ?: 0) }
        val report = reportFile.get().asFile
        report.parentFile.mkdirs()
        report.writeText(
            buildString {
                appendLine("Architecture verification")
                appendLine("strict=${strict.get()}")
                appendLine("active_gradle_modules=$activeGradleModuleCount budget=$maxGradleModules")
                metrics.forEach { (key, value) ->
                    appendLine("$key=$value baseline=${baseline[key] ?: "missing"}")
                }
            },
        )

        metrics.forEach { (key, value) ->
            logger.lifecycle("architecture: $key=$value baseline=${baseline[key] ?: "missing"}")
        }

        if (strict.get() && regressions.isNotEmpty()) {
            throw org.gradle.api.GradleException(
                "Architecture regressions detected: ${regressions.keys.joinToString()}. " +
                "See ${report.relativeTo(root)}.",
            )
        }
        if (activeGradleModuleCount > maxGradleModules) {
            throw org.gradle.api.GradleException(
                "Gradle module budget exceeded: $activeGradleModuleCount > $maxGradleModules. " +
                    "Consolidate a bounded context before adding another module.",
            )
        }
    }

    private fun countMatches(files: Iterable<File>, pattern: Pattern): Int = files.sumOf { file ->
        pattern.matcher(stripComments(file.readText())).results().count().toInt()
    }

    private fun countFeatureToFeatureImports(
        featureSources: Iterable<File>,
        featurePackageGroups: Map<String, Set<String>>,
        allFeaturePackageRoots: Set<String>,
    ): Int = featureSources.sumOf { file ->
        val ownPackageRoots = featurePackageGroups.entries
            .firstOrNull { (pathMarker, _) -> file.path.contains(pathMarker) }
            ?.value
            .orEmpty()
        val importedRoots = allFeaturePackageRoots - ownPackageRoots
        if (importedRoots.isEmpty()) {
            0
        } else {
            val source = stripComments(file.readText())
            importedRoots.sumOf { root ->
                Pattern.compile("(?m)^\\s*import\\s+${Pattern.quote(root)}")
                    .matcher(source)
                    .results()
                    .count()
                    .toInt()
            }
        }
    }

    private fun countFilesWithoutPattern(files: Iterable<File>, pattern: Pattern): Int = files.count { file ->
        !pattern.matcher(stripComments(file.readText())).find()
    }

    private fun countLargeScreenRouteFiles(files: Iterable<File>): Int = files.count { file ->
        if (!file.name.endsWith("Screen.kt")) return@count false
        val source = stripComments(file.readText())
        val declaresScreen = Regex("\\bclass\\s+[A-Za-z0-9_`]+Screen\\b").containsMatchIn(source)
        declaresScreen && file.readLines().size > SCREEN_ROUTE_LINE_BUDGET
    }

    /**
     * ScreenModels are the presentation boundary, so checking only for a
     * state type would allow a model to mutate state directly without an
     * action/effect contract. Keep the check source-based and deliberately
     * small enough to work during the staged migration.
     */
    private fun countFilesWithoutUdfContract(files: Iterable<File>): Int = files.count { file ->
        val source = stripComments(file.readText())
        val hasState = source.contains("UiState<") && source.contains("StateFlow<")
        val hasAction = Regex("\\b[A-Za-z0-9_]*UiAction\\b|\\b(FormAction|SummaryAction)\\b").containsMatchIn(source)
        val hasEffect = Regex("\\b[A-Za-z0-9_]*UiEffect\\b|\\bFormEffect\\b|\\beffects\\b").containsMatchIn(source)
        !(hasState && hasAction && hasEffect)
    }

    private fun stripComments(source: String): String = source
        .replace(Regex("(?s)/\\*.*?\\*/"), "")
        .replace(Regex("(?m)//.*$"), "")

    private companion object {
        const val SCREEN_ROUTE_LINE_BUDGET = 300
    }
}
