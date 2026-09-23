plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.kotlin.serialization)
}

/*
 * Bounded-context data module for portfolio transports. The original
 * functional/api trees remain uncompiled source-compatible archives, while
 * these copies are owned and built by the bounded context.
 */
kotlin {
    androidLibrary {
        namespace = "com.wealthvault.data.portfolio"
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
                implementation(project(":base:security"))
                implementation(project(":domain:portfolio"))
                implementation(project(":domain:social"))
                implementation(project(":domain:profile"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.cio)
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
                implementation("io.ktor:ktor-client-mock:${libs.versions.ktor.get()}")
            }
        }
    }
}
