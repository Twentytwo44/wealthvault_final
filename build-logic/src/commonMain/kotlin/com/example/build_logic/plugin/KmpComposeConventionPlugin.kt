package com.example.plugin


class KmpComposeConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<KotlinMultiplatformExtension> {
                with(pluginManager) {
                    apply(versionCatalogPlugin("composeCompiler"))
                    apply(versionCatalogPlugin("composeMultiplatform"))
                }

                sourceSets {
                    androidMain {
                        dependencies {
                            implementation(versionCatalogLibrary("androidx-activity-compose"))
                            implementation(versionCatalogLibrary("compose-uiToolingPreview"))
                        }
                    }

                    commonMain {
                        dependencies {
                            implementation(versionCatalogLibrary("compose-runtime"))
                            implementation(versionCatalogLibrary("compose-foundation"))
                            implementation(versionCatalogLibrary("compose-material3"))
                            implementation(versionCatalogLibrary("compose-ui"))
                            implementation(versionCatalogLibrary("compose-components-resources"))
                            implementation(versionCatalogLibrary("compose-uiToolingPreview"))

                            implementation(versionCatalogLibrary("androidx-lifecycle-viewmodelCompose"))
                            implementation(versionCatalogLibrary("androidx-lifecycle-runtimeCompose"))

                            implementation(versionCatalogLibrary("navigation-compose"))

                            implementation(versionCatalogLibrary("koin-compose"))
                            implementation(versionCatalogLibrary("koin-compose-viewmodel"))
                            implementation(versionCatalogLibrary("koin-compose-navigation"))
                        }
                    }
                }
            }
        }
    }
}