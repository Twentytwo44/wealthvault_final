import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.baselineProfile)
}

// Firebase configuration is local-only; enable the Google Services plugin when
// the app module has a configuration file without making CI builds depend on it.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

android {
    namespace = "com.wealthvault.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.wealthvault.wealthvault_final"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val releaseStoreFilePath = providers.gradleProperty("releaseStoreFile")
        .orElse(providers.environmentVariable("RELEASE_STORE_FILE"))
        .orNull
    val releaseStorePassword = providers.gradleProperty("releaseStorePassword")
        .orElse(providers.environmentVariable("RELEASE_STORE_PASSWORD"))
        .orNull
    val releaseKeyAlias = providers.gradleProperty("releaseKeyAlias")
        .orElse(providers.environmentVariable("RELEASE_KEY_ALIAS"))
        .orNull
    val releaseKeyPassword = providers.gradleProperty("releaseKeyPassword")
        .orElse(providers.environmentVariable("RELEASE_KEY_PASSWORD"))
        .orNull
    val hasReleaseSigning = listOf(
        releaseStoreFilePath,
        releaseStorePassword,
        releaseKeyAlias,
        releaseKeyPassword,
    ).all { !it.isNullOrBlank() }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStoreFilePath!!)
                storePassword = releaseStorePassword!!
                keyAlias = releaseKeyAlias!!
                keyPassword = releaseKeyPassword!!
            }
        }
    }

    buildTypes {
        // Macrobenchmark must install a non-debuggable, signed target APK.
        // Release remains unsigned unless CI supplies production signing
        // material; the benchmark variant uses the local debug key only on
        // the measurement device and is never a release artifact.
        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
        }

        getByName("release") {
            // Release artifacts must use the same optimized path that CI measures.
            // Signing remains opt-in so local and CI builds can still produce an
            // unsigned artifact without requiring private signing material.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Keep the existing launcher/theme assets in one place while the app shell is split out.
    sourceSets["main"].res.directories.add(file("../composeApp/src/androidMain/res").path)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(project(":composeApp"))
    // The platform launcher owns only process-level logging; all data,
    // security, and feature graph assembly remains inside composeApp.
    implementation(project(":base:core"))
    baselineProfile(project(":benchmarks"))

    androidTestImplementation(libs.androidx.testExt.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.uiautomator)
    androidTestImplementation(libs.koin.core)
    androidTestImplementation(project(":domain:auth"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.uiToolingPreview)
    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.profile.installer)
}

tasks.register("verifyReleaseApkSize") {
    group = "verification"
    description = "Builds release and fails when the unsigned APK exceeds the 35 MB budget."
    dependsOn("assembleRelease")

    val releaseOutput = layout.buildDirectory.dir("outputs/apk/release")
    val releaseIntermediates = layout.buildDirectory.dir("intermediates")
    val artifactMetrics = layout.buildDirectory.file("performance/artifacts.properties")
    val backupRules = layout.projectDirectory.file("src/main/res/xml/backup_rules.xml")
    val extractionRules = layout.projectDirectory.file("src/main/res/xml/data_extraction_rules.xml")
    doLast {
        val releaseApk = releaseOutput.get().asFile
            .listFiles()
            ?.filter { it.isFile && it.extension == "apk" }
            ?.singleOrNull()
            ?: throw GradleException("Expected exactly one release APK in ${releaseOutput.get().asFile}")
        val sizeBytes = releaseApk.length()
        val budgetBytes = 35_000_000L
        logger.lifecycle("Release APK: ${releaseApk.name} = $sizeBytes bytes")
        val artifactMetricsFile = artifactMetrics.get().asFile
        artifactMetricsFile.parentFile.mkdirs()
        artifactMetricsFile.writeText(
            "schema_version=1\n" +
                "android_release_apk_bytes=$sizeBytes\n",
        )
        if (sizeBytes > budgetBytes) {
            throw GradleException("Release APK exceeds 35 MB budget: $sizeBytes > $budgetBytes bytes")
        }

        val releaseManifests = releaseIntermediates.get().asFile
            .walkTopDown()
            .filter { it.isFile && it.path.contains("/release/") && it.name == "AndroidManifest.xml" }
            .toList()
        val cleartextEnabledInRelease = releaseManifests.any { manifest ->
            manifest.readText().contains("android:usesCleartextTraffic=\"true\"")
        }
        if (cleartextEnabledInRelease) {
            throw GradleException("Release manifest enables cleartext traffic; only debug may use HTTP")
        }

        val backupRulesFile = backupRules.asFile
        val extractionRulesFile = extractionRules.asFile
        check(backupRulesFile.isFile && extractionRulesFile.isFile) {
            "Release must ship explicit Android backup exclusion rules for session credentials"
        }
        val requiredExcludedPaths = listOf(
            "wealthvault_secure_session.xml",
            "default.preferences_pb",
        )
        requiredExcludedPaths.forEach { path ->
            check(backupRulesFile.readText().contains(path) && extractionRulesFile.readText().contains(path)) {
                "Android backup rules must exclude session path: $path"
            }
        }
    }
}
