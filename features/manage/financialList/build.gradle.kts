
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
                implementation(project(":functional:api:account-api"))
                implementation(project(":functional:api:cash-api"))
                implementation(project(":functional:api:investment-api"))
                implementation(project(":functional:api:insurance-api"))
                implementation(project(":functional:api:building-api"))
                implementation(project(":functional:api:land-api"))
                implementation(project(":functional:api:user-api"))
                implementation(project(":functional:api:group-api"))



                implementation(project(":functional:api:liability-api"))
                implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
                implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.0-rc01")
                implementation("org.jetbrains.kotlinx:atomicfu:0.23.2")
                implementation(project(":features:manage:form"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
                implementation(project(":navigation-point"))


                implementation(libs.compose.material)
                implementation(libs.compose.material3)

                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation("org.jetbrains.compose.material:material-icons-extended:1.6.11")
                implementation(libs.compose.components.resources)
                implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha06")

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
