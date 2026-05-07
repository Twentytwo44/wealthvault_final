plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.wealthvault.websocket_api"
        compileSdk = 36
        minSdk = 24
    }

    val xcfName = "websocket-apiKit"

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
                implementation(libs.kotlin.stdlib)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.websockets)

                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.ktorfit.lib)

                implementation(project(":base:core"))
                implementation(project(":base:config"))
                implementation(project(":functional:data-store"))
                implementation(project(":functional:api:auth-api"))
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${libs.versions.coroutines.get()}")
                implementation("io.ktor:ktor-client-mock:${libs.versions.ktor.get()}")
            }
        }

        androidMain {
            dependencies {
            }
        }

        iosMain {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:${libs.versions.ktor.get()}")
            }
        }
    }
}
