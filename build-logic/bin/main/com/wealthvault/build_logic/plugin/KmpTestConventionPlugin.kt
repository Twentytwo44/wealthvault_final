package com.wealthvault.build_logic.plugin

import com.wealthvault.build_logic.extension.versionCatalogLibrary
import com.wealthvault.build_logic.extension.versionCatalogPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpTestConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(versionCatalogPlugin("mokkery"))
            }

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets {
                    commonTest {
                        dependencies {
                            implementation(versionCatalogLibrary("kotlin-test"))
                            implementation(versionCatalogLibrary("coroutines-test"))
                            implementation(versionCatalogLibrary("turbine"))
                        }
                    }

                    getByName("androidDeviceTest") {
                        dependencies {
                            implementation(versionCatalogLibrary("androidx-runner"))
                            implementation(versionCatalogLibrary("androidx-core"))
                            implementation(versionCatalogLibrary("androidx-testExt-junit"))
                        }
                    }
                }
            }
        }
    }
}
