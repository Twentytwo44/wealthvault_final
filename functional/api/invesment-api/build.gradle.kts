plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
}

kotlin {
    plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidKotlinMultiplatformLibrary)
        alias(libs.plugins.androidLint)
    }

    kotlin {

        // Target declarations - add or remove as needed below. These define
        // which platforms this KMP module supports.
        // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
        androidLibrary {
            namespace = "com.example.account_api"
            compileSdk = 36
            minSdk = 31

//        withHostTestBuilder {
//        }
//
//        withDeviceTestBuilder {
//            sourceSetTreeName = "test"
//        }.configure {
//            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        }
        }

        // For iOS targets, this is also where you should
        // configure native binary output. For more information, see:
        // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

        // A step-by-step guide on how to include this library in an XCode
        // project can be found here:
        // https://developer.android.com/kotlin/multiplatform/migrate
        val xcfName = "invesment-apiKit"

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

        // Source set declarations.
        // Declaring a target automatically creates a source set with the same name. By default, the
        // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
        // common to share sources between related targets.
        // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
        sourceSets {
            commonMain {

                dependencies {
                    implementation(libs.kotlin.stdlib)
                    // Add KMP dependencies here
                    implementation(libs.ktor.client.core)
                    implementation(libs.ktor.client.content.negotiation)
                    implementation(libs.ktor.serialization.json)
                    implementation(libs.ktor.client.auth)
                    implementation(libs.ktor.client.cio)


                    implementation(libs.koin.core)
                    implementation(libs.koin.compose)
                    implementation(libs.ktorfit.lib)

                    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:2.3.4")


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
                    // Add Android-specific dependencies here. Note that this source set depends on
                    // commonMain by default and will correctly pull the Android artifacts of any KMP
                    // dependencies declared in commonMain.
                }
            }


            iosMain {
                dependencies {
                    // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                    // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                    // part of KMP’s default source set hierarchy. Note that this source set depends
                    // on common by default and will correctly pull the iOS artifacts of any
                    // KMP dependencies declared in commonMain.
                    implementation("io.ktor:ktor-client-darwin:${libs.versions.ktor.get()}")
                }

            }
        }

    }

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.example.invesment_api"
        compileSdk = 36
        minSdk = 31

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "invesment-apiKit"

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

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                // Add KMP dependencies here
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }

}
