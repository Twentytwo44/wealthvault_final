plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.kotlin.serialization)
}

/* Profile transport, mapping, cache, and repository implementations. */
kotlin {
    androidLibrary {
        namespace = "com.wealthvault.data.profile"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.koin.core)
                implementation(project(":base:core"))
                implementation(project(":base:config"))
                implementation(project(":domain:profile"))
            }
        }

        iosMain {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:${libs.versions.ktor.get()}")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
                implementation(libs.ktor.client.content.negotiation)
                implementation("io.ktor:ktor-client-mock:${libs.versions.ktor.get()}")
            }
        }
    }
}
