plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
}

kotlin {

    androidLibrary {
        namespace = "com.example.register"

    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                // Add KMP dependencies here

                implementation(libs.koin.core)
                implementation(libs.koin.compose)
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
