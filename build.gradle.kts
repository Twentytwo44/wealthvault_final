plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidTest) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.kover) apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
id("com.wealthvault.build_logic.architecture")
id("com.wealthvault.build_logic.performance")
id("com.wealthvault.build_logic.coverage")
}

// Kover is applied by the KMP library convention to every migrated module.
// This root task gives CI one stable entry point while keeping reports scoped
// to the Android host tests that Kover can instrument.
val koverAndroidReports = tasks.register("koverXmlReportAndroidAll") {
    group = "verification"
    description = "Generates Android Kover XML reports for all migrated modules."
}

// Keep the schema upgrade path executable from the repository root.  SQLDelight
// already validates each generated database; exposing one stable task lets CI
// verify that production-version migrations remain valid without depending on
// a module-specific task name.
val verifyDatabaseMigrations = tasks.register("verifyDatabaseMigrations") {
    group = "verification"
    description = "Verifies SQLDelight schema declarations and migrations."
    dependsOn(":base:database:verifySqlDelightMigration")
}

// This is the fast, usability-first acceptance gate.  It deliberately stops
// at deterministic compilation/host checks and the optimized release artifact;
// connected UI smoke tests and runtime benchmarks remain runner-specific jobs.
val verifyUsability = tasks.register("verifyUsability") {
    group = "verification"
    description = "Verifies architecture, migrations, Android tests, device-test contracts, and release packaging."
    dependsOn(
        ":verifyArchitecture",
        ":verifyDatabaseMigrations",
        ":androidApp:compileDebugKotlin",
        ":androidApp:compileDebugAndroidTestKotlin",
        ":androidApp:verifyReleaseApkSize",
    )
}

// iOS has its own runner/toolchain, so keep its shared-test gate explicit
// instead of making the Android usability task unexpectedly require Xcode.
val verifyUsabilityIos = tasks.register("verifyUsabilityIos") {
    group = "verification"
    description = "Verifies the iOS simulator framework and all active shared simulator tests."
    dependsOn(":composeApp:linkPodDebugFrameworkIosSimulatorArm64")
}

gradle.projectsEvaluated {
    koverAndroidReports.configure {
        dependsOn(
            subprojects.flatMap { project ->
                project.tasks.matching { it.name == "koverXmlReportAndroid" }.toList()
            },
        )
    }

    verifyUsability.configure {
        dependsOn(
            subprojects.flatMap { project ->
                project.tasks.matching {
                    it.name == "testAndroidHostTest" || it.name == "compileAndroidDeviceTest"
                }.toList().map { task -> task.path }
            },
        )
    }

    verifyUsabilityIos.configure {
        dependsOn(
            subprojects.flatMap { project ->
                project.tasks.matching { it.name == "iosSimulatorArm64Test" }
                    .toList()
                    .map { task -> task.path }
            },
        )
    }
}
