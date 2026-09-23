plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.kotlin.serialization)
}

/*
 * Social bounded-context data implementation.
 *
 * Group/share endpoint sources are owned by this data module. The original
 * functional/api tree remains an uncompiled compatibility archive. Social
 * feature code consumes only domain contracts.
 */
kotlin {
    androidLibrary {
        namespace = "com.wealthvault.data.social"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.serialization.json)
                implementation(libs.koin.core)
                implementation(project(":base:core"))
                implementation(project(":base:config"))
                implementation(project(":domain:portfolio"))
                implementation(project(":domain:profile"))
                implementation(project(":domain:social"))
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
