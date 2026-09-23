plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
}

// Platform-backed authentication capabilities belong to the core security
// boundary. Active sources are owned by this module; the original functional
// trees remain uncompiled compatibility archives.
kotlin {
    androidLibrary {
        namespace = "com.wealthvault.core.security"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.dataStore)
                implementation(libs.dataStore.preferences)
                implementation(libs.coroutines)
                implementation(libs.compose.runtime)
                implementation(libs.koin.compose)
                implementation(project(":base:core"))
                implementation(project(":domain:auth"))
                implementation(project(":domain:profile"))
                implementation("com.squareup.okio:okio:3.9.0")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
                implementation(libs.dataStore)
                implementation(libs.dataStore.preferences)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation("com.linecorp.linesdk:linesdk:5.9.1")
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.messaging)
            }
        }

        iosMain {
        }
    }
}
