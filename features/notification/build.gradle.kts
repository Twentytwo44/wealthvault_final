plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    androidLibrary {
        namespace = "com.wealthvault.notification"
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
                // ลบบรรทัดที่ซ้ำออกไป 1 อัน (voyager-navigator)
                implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")

                implementation(project(":functional:api:auth-api"))
                implementation(project(":functional:api:notification-api"))
                implementation(project(":functional:api:user-api"))

                implementation(project(":functional:data-store"))
                implementation(project(":base:core"))

                // 🌟 IMPORT MODULE SOCIAL (เพิ่มบรรทัดนี้เพื่อให้เรียก AddFriendScreen ได้)
                // ปล. เช็กชื่อ path ":social" อีกทีนะครับว่าโปรเจกต์จริงตั้งชื่อโฟลเดอร์ไว้ว่าอะไร
                // เช่น อาจจะเป็น implementation(project(":feature:social"))
                implementation(project(":features:social"))

                implementation("androidx.datastore:datastore-preferences-core:1.1.1")
                implementation(libs.ktor.serialization.json)
            }
        }
        commonTest {
            // ลบ dependencies ที่ซ้อนกันออก
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}