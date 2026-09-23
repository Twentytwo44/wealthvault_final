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
               apply(versionCatalogPlugin("kover"))
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

                // Library modules are consumed by the application framework and do not
                // publish standalone iOS frameworks.  In particular, adding a direct
                // `binaries.framework` here creates `embedAndSignAppleFrameworkForXcode`
                // for every library.  That task is incompatible with CocoaPods-backed
                // modules (for example data:auth and features:profile).  The composition
                // root owns the single framework declaration instead.
                iosX64()
                iosArm64()
                iosSimulatorArm64()

                sourceSets {
                    commonMain {
                        dependencies {
                            implementation(versionCatalogLibrary("kotlin-stdlib"))
                            implementation(versionCatalogLibrary("coroutines"))
                            implementation(versionCatalogLibrary("koin-core"))
                            implementation(versionCatalogLibrary("koin-compose"))

                        }
                    }

                    commonTest {
                        dependencies {
                            implementation(versionCatalogLibrary("kotlin-test"))
                            implementation(versionCatalogLibrary("coroutines-test"))
                        }
                    }

                    getByName("androidHostTest") {
                        dependencies {
                            implementation(versionCatalogLibrary("kotlin-test"))
                        }
                    }

                    getByName("androidDeviceTest") {
                        dependencies {
                            // Device tests inherit commonTest sources. Keep
                            // kotlin.test available there as well as on the
                            // host and native test compilations.
                            implementation(versionCatalogLibrary("kotlin-test"))
                            implementation(versionCatalogLibrary("androidx-runner"))
                            implementation(versionCatalogLibrary("androidx-core"))
                            implementation(versionCatalogLibrary("androidx-testExt-junit"))
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
