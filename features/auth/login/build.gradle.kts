plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
//    alias(libs.plugins.composeCompiler)
//    alias(libs.plugins.composeMultiplatform)
//    alias(libs.plugins.wealth.vault.test)


}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.wealthvault.login"

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
                    // 1. ตัวหลักสำหรับรัน Test ใน Kotlin
                    implementation(kotlin("test"))

                    // 2. สำหรับทดสอบ Coroutines (พวก suspend fun และ Flow)
                    // สำคัญมากสำหรับการใช้ runTest และคำสั่ง .first() ใน Flow
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

                    // 3. หากคุณใช้ Ktor สำหรับ LoginApi (ถ้าใช้ Retrofit จะอยู่อีกส่วน)
                    implementation("io.ktor:ktor-client-mock:3.4.0")

                    // 4. (ทางเลือก) หากต้องการทำ Mocking
                    implementation("io.mockative:mockative:2.1.0")
                    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:2.7.2")


                    implementation(project(":functional:api:auth-api"))
                    implementation(project(":functional:data-store"))
                }
            }
        }
    }

        // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate








    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html


}

//
//val voyagerVersion = "1.0.0"
//implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
//implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
//implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
//implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
//implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")

