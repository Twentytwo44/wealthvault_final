plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.serialization)




}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.wealthvault.notificationservice"
        compileSdk = 36
        minSdk = 24


    }

    val xcfName = "notificationKit"

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

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate



    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            // Repository and transport code is compiled by data:notification;
            // this module keeps only the platform push-service implementation.
            kotlin.exclude("com/wealthvault/data/notification/**")
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



                implementation(project(":base:core"))
                implementation(project(":base:config"))
                implementation(project(":base:security"))
                implementation(project(":domain:notification"))
                implementation(project(":domain:social"))
                implementation(project(":domain:auth"))

                // Add KMP dependencies here
            }
        }

        commonTest {
            kotlin.exclude("com/wealthvault/data/notification/**")
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
            }
        }

        androidMain {
            dependencies {

                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.messaging)

            }
        }

        iosMain { }

    }

}
