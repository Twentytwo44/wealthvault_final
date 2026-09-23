plugins {
    alias(libs.plugins.wealth.vault.lib)
}

kotlin {
    androidLibrary {
        namespace = "com.wealthvault.domain.portfolio"
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
