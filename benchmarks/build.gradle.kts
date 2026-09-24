plugins {
    alias(libs.plugins.androidTest)
    alias(libs.plugins.baselineProfile)
}

android {
    namespace = "com.wealthvault.benchmarks"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Keep the Macrobenchmark JSON on the runner so CI can convert measured
        // results into the repository's canonical performance properties. The
        // exporter is intentionally enabled only for the benchmark test APK.
        testInstrumentationRunnerArguments["androidx.benchmark.output.enable"] = "true"
        testInstrumentationRunnerArguments["androidx.benchmark.additionalTestOutputDir"] =
            "/sdcard/Download/wealthvault-benchmark"
        // Local/CI validation uses a fixed emulator. Keep the benchmark
        // runnable there, while the performance gate still records that
        // emulator measurements are provisional until a physical runner is
        // available.
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
    }

    targetProjectPath = ":androidApp"

    // Macrobenchmark must run in its own instrumentation process. Without
    // self-instrumenting, the runner is hosted by the target application and
    // the benchmark's intentional force-stop also kills the test process.
    experimentalProperties["android.experimental.self-instrumenting"] = true

    buildTypes {
        create("benchmark") {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            matchingFallbacks += listOf("release")
            // The benchmark APK is installed on the measurement device. Keep
            // production signing out of source/CI secrets and use the
            // standard debug key for this test-only artifact.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(libs.benchmark.macro)
    implementation(libs.uiautomator)
    implementation(libs.androidx.testExt.junit)
    implementation(libs.profile.installer)
    implementation(libs.startup.runtime)
    // benchmark-macro 1.4.1 still declares lifecycle 2.3.1. The app uses
    // the current lifecycle runtime through Compose Multiplatform; pin the
    // test APK to the same AndroidX binary so Lifecycle.Event's ABI cannot
    // be split between the instrumentation and target class loaders.
    implementation("androidx.lifecycle:lifecycle-runtime:2.9.4")
    implementation("androidx.lifecycle:lifecycle-common:2.9.4")
    compileOnly(libs.error.prone.annotations)
}
