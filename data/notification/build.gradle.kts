plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.kotlin.serialization)
}

// Notification repositories and their transport adapters are compiled as a
// bounded-context data module. The original functional/api tree remains an
// uncompiled compatibility archive; push-service sources live in base:security.
kotlin {
    androidLibrary {
        namespace = "com.wealthvault.data.notification"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.koin.core)
                implementation(project(":base:core"))
                implementation(project(":base:config"))
                implementation(project(":base:security"))
                implementation(project(":domain:notification"))
                implementation(project(":domain:social"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
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
