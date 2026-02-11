package com.wealthvault.build_logic.plugin

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.wealthvault.build_logic.extension.versionCatalogLibrary
import com.wealthvault.build_logic.extension.versionCatalogPlugin
import com.wealthvault.build_logic.extension.versionCatalogVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
           with(pluginManager) {
               apply(versionCatalogPlugin("kotlinMultiplatform"))
               apply(versionCatalogPlugin("androidKotlinMultiplatformLibrary"))
               apply(versionCatalogPlugin("androidLint"))
           }

            extensions.configure<KotlinMultiplatformExtension> {
                extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
                    compileSdk = versionCatalogVersion("android-compileSdk").toInt()
                    minSdk = versionCatalogVersion("android-minSdk").toInt()

                    withHostTest {

                    }

                    withDeviceTestBuilder {
                        sourceSetTreeName = "test"
                    }.configure {
                        instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    }
                }

                val xcfName = "configKit"

                iosX64 {
                    binaries.framework {
                        baseName = xcfName
                    }
                }

                iosArm64 {
                    binaries.framework {
                        baseName = xcfName
                    }
                }

                iosSimulatorArm64 {
                    binaries.framework {
                        baseName = xcfName
                    }
                }

                sourceSets {
                    commonMain {
                        dependencies {
                            implementation(versionCatalogLibrary("kotlin-stdlib"))
                            implementation(versionCatalogLibrary("coroutines"))
                            implementation(versionCatalogLibrary("koin-core"))
                            implementation(versionCatalogLibrary("koin-compose"))

                        }
                    }

                    androidMain {
                        dependencies {
                            implementation(versionCatalogLibrary("coroutines-android"))
                        }
                    }
                }
            }

        }
    }
}
