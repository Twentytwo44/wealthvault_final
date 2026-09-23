plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // CocoaPods owns the app framework integration. The auth data module owns
    // the GoogleSignIn cinterop; declaring the same pod here would link its
    // generated symbols twice when data:auth is exported below.
    kotlin("native.cocoapods")

}

kotlin {
    // The production iOS app declares this pod through the generated podspec
    // and the Podfile below; Gradle owns only the framework metadata here.
    cocoapods {
        version = "1.0"
        summary = "WealthVault shared Kotlin Multiplatform application"
        homepage = "https://github.com/Twentytwo44/wealthvault_final"
        ios.deploymentTarget = "15.0"
        // Let CocoaPods own the iOS app integration. The generated podspec
        // contributes its Gradle syncFramework script phase to iosApp rather
        // than invoking an incompatible embed-and-sign task from Xcode.
        podfile = project.file("../iosApp/Podfile")
        // CocoaPods owns the Apple framework integration. Keeping the
        // framework declaration inside this block prevents Kotlin 2.3 from
        // registering the incompatible direct embed-and-sign task.
        framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.wealthvault.composeapp")
            export(project(":data:auth"))
            export(project(":base:security"))
            export(project(":features:auth:login"))
        }
    }

    androidLibrary {
        namespace = "com.wealthvault.composeapp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    iosArm64()
    iosSimulatorArm64()

    // Shared Kotlin tests link the exported auth implementation directly.
    // CocoaPods supplies these frameworks to the app workspace, but Gradle's
    // native test linker does not inherit the workspace xcconfig, so mirror
    // the synthetic pod framework search paths for the debug test binary.
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
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(project(":base:core"))
            implementation(project(":base:database"))
            implementation(project(":domain:auth"))
            implementation(libs.compose.uiTooling)

        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            val voyagerVersion = "1.0.0"
            implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")


            implementation(project(":base:core"))
            implementation(project(":base:database"))
            api(project(":features:auth:login"))
            implementation(project(":features:dashboard"))
            implementation(project(":features:notification"))
            implementation(project(":features:manage:financialList"))
            implementation(project(":features:social"))
            implementation(project(":features:profile"))
            implementation(project(":main"))


            // These projects are exported by the CocoaPods framework above;
            // they must be API dependencies of the corresponding source set.
            api(project(":data:auth"))
            api(project(":base:security"))
            implementation(project(":domain:profile"))
            implementation(project(":data:dashboard"))
            implementation(project(":data:notification"))
            implementation(project(":data:profile"))
            implementation(project(":data:portfolio"))
            implementation(project(":data:social"))

            implementation(project(":base:network"))








        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.coroutines.test)
        }
    }
}

compose {
    resources {
        // เพื่อให้โมดูลหลักรู้จักคลาส Res จากโมดูลอื่น
        publicResClass = true
    }
}
