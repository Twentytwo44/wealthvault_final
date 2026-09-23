plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.kotlin.serialization)
}

// The network implementation is owned by this module. The original setup-api
// tree remains an uncompiled source-compatible archive.
kotlin {
    androidLibrary {
        namespace = "com.wealthvault.core.network"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.log)
                implementation(libs.koin.core)
                implementation(project(":base:core"))
                implementation(project(":base:config"))
                implementation(project(":domain:auth"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
                implementation("io.ktor:ktor-client-mock:${libs.versions.ktor.get()}")
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
    }
}
