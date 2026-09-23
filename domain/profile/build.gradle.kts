plugins {
    alias(libs.plugins.wealth.vault.lib)
}

kotlin {
    androidLibrary {
        namespace = "com.wealthvault.domain.profile"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":base:core"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
