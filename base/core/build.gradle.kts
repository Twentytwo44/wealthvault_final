plugins {
    alias(libs.plugins.wealth.vault.lib)
    alias(libs.plugins.wealth.vault.compose)
    // ...

    // 🌟 เปลี่ยนเป็นบรรทัดนี้ครับ
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
    // หมายเหตุ: version ให้ดูจากไฟล์ libs.versions.toml หรือใช้เลขเดียวกับ kotlin version หลักในโปรเจกต์ครับ
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.wealthvault.core"

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
    sourceSets {
        commonMain {
            resources.srcDir("src/commonMain/composeResources")
            dependencies {
                val voyagerVersion = "1.0.0"
                implementation("cafe.adriel.voyager:voyager-navigator:${voyagerVersion}")
                implementation("cafe.adriel.voyager:voyager-tab-navigator:${voyagerVersion}")
                implementation("cafe.adriel.voyager:voyager-transitions:${voyagerVersion}")
                implementation("cafe.adriel.voyager:voyager-navigator:${voyagerVersion}")
                implementation("cafe.adriel.voyager:voyager-screenmodel:${voyagerVersion}")
                implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
                implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.0-rc01")
            }
        }
    }

}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "com.wealthvault.core.generated.resources"
    }
}
