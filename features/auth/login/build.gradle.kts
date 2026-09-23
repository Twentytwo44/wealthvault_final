plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
//    alias(libs.plugins.composeCompiler)
//    alias(libs.plugins.composeMultiplatform)
//    alias(libs.plugins.wealth.vault.test)


}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.wealthvault.login"

    }

    // The GoogleSignIn CocoaPod is linked by the iOS app through Xcode's
    // generated Pods xcconfig. Kotlin/Native test binaries are linked by
    // Gradle directly, so they need the same synthetic pod framework search
    // paths explicitly. Keep this scoped to the simulator test binary; the
    // production app continues to receive these paths from CocoaPods.
    val googleAuthPodFrameworkRoot = rootProject.file(
        "data/auth/build/cocoapods/synthetic/ios/build/Debug-iphonesimulator",
    )
    iosSimulatorArm64 {
        binaries {
            configureEach {
                if (name == "debugTest") {
                    listOf(
                        "GoogleSignIn",
                        "AppAuth",
                        "GTMAppAuth",
                        "GTMSessionFetcher",
                        "AppCheckCore",
                        "GoogleUtilities",
                        "PromisesObjC",
                    )
                        .forEach { framework ->
                            val frameworkPath = googleAuthPodFrameworkRoot.resolve(framework).path
                            linkerOpts("-F$frameworkPath")
                            linkerOpts("-rpath", frameworkPath)
                        }
                }
            }
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
                implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")






                implementation(project(":base:core"))
                implementation(project(":domain:auth"))
                implementation(project(":domain:profile"))
                implementation(project(":domain:notification"))

                implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
                implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.0-rc01")

                // 🌟 เช็คว่าตัวสะกดถูกต้องเป๊ะๆ
                implementation("io.github.onseok:peekaboo-image-picker:0.5.2")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")





            }
        }
        commonTest {
            dependencies {
                dependencies {
                    // 1. ตัวหลักสำหรับรัน Test ใน Kotlin
                    implementation(kotlin("test"))

                    // 2. สำหรับทดสอบ Coroutines (พวก suspend fun และ Flow)
                    // สำคัญมากสำหรับการใช้ runTest และคำสั่ง .first() ใน Flow
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

                }
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
