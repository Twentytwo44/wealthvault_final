plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
}

kotlin {

    androidLibrary {
        namespace = "com.wealthvault.profile"

    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlin.stdlib)
                // Add KMP dependencies here
                implementation(libs.compose.components.resources)
                implementation(libs.compose.ui)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3) // เรียกใช้ Material3 ได้แล้ว


                val voyagerVersion = "1.0.0"
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")

                implementation(project(":functional:api:auth-api"))
                implementation(project(":functional:data-store"))
                implementation(project(":base:core"))
                implementation(project(":functional:api:user-api"))
                implementation(project(":functional:api:notification-api"))
                implementation(project(":navigation-point"))




                implementation("androidx.datastore:datastore-preferences-core:1.1.1")

                implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
                implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.0-rc01")

                // 🌟 เช็คว่าตัวสะกดถูกต้องเป๊ะๆ
                implementation("io.github.onseok:peekaboo-image-picker:0.5.2")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

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
