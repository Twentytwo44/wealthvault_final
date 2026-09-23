plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
    alias(libs.plugins.kotlin.serialization)
    kotlin("native.cocoapods")
}

// Auth transport sources are owned by this bounded-context module. The
// original functional/api tree remains uncompiled as a compatibility archive.
kotlin {
    androidLibrary {
        namespace = "com.wealthvault.data.auth"
    }

    cocoapods {
        version = "1.0"
        ios.deploymentTarget = "15.0"
        pod("GoogleSignIn") {
            version = "9.1.0"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.koin.core)
                implementation(project(":domain:profile"))
                implementation(project(":base:core"))
                implementation(project(":base:config"))
                implementation(project(":domain:auth"))
                implementation(project(":domain:notification"))
                implementation(project(":base:security"))
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
                implementation("androidx.credentials:credentials:1.3.0")
                implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
                implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
            }
        }

        iosMain {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:${libs.versions.ktor.get()}")
            }
        }
    }
}
