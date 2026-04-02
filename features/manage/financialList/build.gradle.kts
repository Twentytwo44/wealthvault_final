
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
                implementation(project(":functional:api:account-api"))
                implementation(project(":functional:api:cash-api"))
                implementation(project(":functional:api:investment-api"))
                implementation(project(":functional:api:insurance-api"))
                implementation(project(":functional:api:building-api"))
                implementation(project(":functional:api:land-api"))
                implementation(project(":functional:api:liability-api"))
                implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
                implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.0-rc01")
                implementation("org.jetbrains.kotlinx:atomicfu:0.23.2")
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
