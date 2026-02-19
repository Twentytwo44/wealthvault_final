plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
//    alias(libs.plugins.composeCompiler)
//    alias(libs.plugins.composeMultiplatform)
//    alias(libs.plugins.wealth.vault.test)
    kotlin("native.cocoapods")


}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.wealthvault.login"

    }
    cocoapods {
        version = "1.0"
        ios.deploymentTarget = "14.1"

        pod("GoogleSignIn") {
            version = "7.0.0"
        }
        framework {
            baseName = "GoogleAuth"
        }
    }


    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                // Add KMP dependencies here

                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                val voyagerVersion = "1.0.0"
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")

                implementation("androidx.datastore:datastore-preferences-core:1.1.1")

                implementation(libs.coroutines)




            }
        }
        androidMain {
            dependencies {
                implementation("androidx.credentials:credentials:1.3.0")
                implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
                implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
            }
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


}

//
//val voyagerVersion = "1.0.0"
//implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
//implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
//implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
//implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
//implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")

