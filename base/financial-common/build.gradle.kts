plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.wealthvault.financial.common"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            val voyagerVersion = "1.0.0"
            implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")

            implementation(project(":base:core"))
            implementation(project(":domain:portfolio"))
            implementation(project(":domain:profile"))
            implementation(project(":domain:social"))
            implementation(libs.compose.material)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.components.resources)
            implementation("org.jetbrains.compose.material:material-icons-extended:1.6.11")
            implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha06")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
        }

        androidMain.dependencies {
            implementation("androidx.activity:activity-compose:1.9.0")
        }
    }
}
