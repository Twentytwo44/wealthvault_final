package com.wealthvault.build_logic.plugin

import com.android.build.api.dsl.ApplicationExtension
import com.wealthvault.build_logic.extension.versionCatalogPlugin
import com.wealthvault.build_logic.extension.versionCatalogVersion
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpApplicationConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(versionCatalogPlugin("kotlinMultiplatform"))
                apply(versionCatalogPlugin("androidApplication"))
                apply(versionCatalogPlugin("kmp-template-compose"))
            }

            extensions.configure<KotlinMultiplatformExtension> {
                androidTarget {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_17)
                    }
                }

                listOf(
                    iosX64(),
                    iosArm64(),
                    iosSimulatorArm64()
                ).forEach { iosTarget ->
                    iosTarget.binaries.framework {
                        baseName = "ComposeApp"
                        isStatic = true
                    }
                }
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = versionCatalogVersion("android-compileSdk").toInt()

                defaultConfig {
                    minSdk = versionCatalogVersion("android-minSdk").toInt()
                    targetSdk = versionCatalogVersion("android-targetSdk").toInt()
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }
        }
    }
}
