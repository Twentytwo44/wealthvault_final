plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
}

kotlin {

    androidLibrary {
        namespace = "com.wealthvault.dashboard"

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

                implementation("androidx.datastore:datastore-preferences-core:1.1.1")
                implementation(project(":functional:api:user-api"))

                implementation(project(":features:manage:form"))
                implementation(project(":features:notification"))
                implementation(project(":navigation-point"))






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
