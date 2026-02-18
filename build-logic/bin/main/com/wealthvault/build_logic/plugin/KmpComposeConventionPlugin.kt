package com.wealthvault.build_logic.plugin

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.wealthvault.build_logic.extension.versionCatalogLibrary
import com.wealthvault.build_logic.extension.versionCatalogPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpComposeConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(versionCatalogPlugin("composeCompiler"))
                apply(versionCatalogPlugin("composeMultiplatform"))
            }
            extensions.configure<KotlinMultiplatformExtension> {
                extensions.configure<KotlinMultiplatformAndroidLibraryExtension>{
                    androidResources { enable = true }
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

//                            implementation(versionCatalogLibrary("navigation-compose"))

                            implementation(versionCatalogLibrary("koin-compose"))
//                            implementation(versionCatalogLibrary("koin-compose-viewmodel"))
//                            implementation(versionCatalogLibrary("koin-compose-navigation"))
                        }
                    }
                }
            }
        }
    }
}
