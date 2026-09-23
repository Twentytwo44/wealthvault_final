
plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
}

kotlin {

    androidLibrary {
        namespace = "com.wealthvault.financiallist"

    }

    sourceSets {
        commonMain {
            dependencies {
                // Add KMP dependencies here
                implementation(libs.compose.components.resources)
                implementation(libs.compose.ui)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3) // เรียกใช้ Material3 ได้แล้ว


                val voyagerVersion = "1.0.0"
                implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")

                implementation(project(":base:core"))
                implementation(project(":domain:portfolio"))
                implementation(project(":domain:profile"))
                implementation(project(":domain:social"))

                implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
                implementation(libs.compose.material)
                implementation(libs.compose.runtime)
                implementation("org.jetbrains.compose.material:material-icons-extended:1.6.11")
            }
        }
        commonTest {
            dependencies {
                dependencies {

                }
            }
        }
    }

}

configurations.all {
    resolutionStrategy.force("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
}
