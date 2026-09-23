plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("WealthVaultDatabase") {
            packageName.set("com.wealthvault.database")
        }
    }
}

kotlin {
    androidLibrary {
        namespace = "com.wealthvault.database"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.sqldelight.runtime)
            implementation(project(":base:core"))
        }
        androidMain.dependencies {
            api(libs.sqldelight.android.driver)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}

// The Android KMP library plugin currently exposes the Android target through
// a variant-specific configuration instead of the legacy `androidMain` one.
// Keep the driver on both configurations while the project finishes its AGP 9
// migration; this prevents the platform actual from compiling without the
// driver classes.
dependencies {
    add("androidMainApi", libs.sqldelight.android.driver)
}
