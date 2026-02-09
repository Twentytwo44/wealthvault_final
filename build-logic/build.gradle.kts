plugins {
    `kotlin-dsl` // สำคัญมาก: เพื่อให้เขียน Gradle ด้วย Kotlin ในโฟลเดอร์นี้ได้
}

group = "com.wealthvault.build_logic"

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.build.gradle)
    implementation(libs.mokkery.gradle)
}

gradlePlugin {
    plugins {
        register("kmpLibraryPlugin") {
            id = "com.wealthvault.build_logic.library"
            implementationClass = "com.wealthvault.build_logic.plugin.KmpLibraryConventionPlugin"
        }

        register("kmpComposePlugin") {
            id = "com.wealthvault.build_logic.compose"
            implementationClass = "com.wealthvault.build_logic.plugin.KmpComposeConventionPlugin"
        }

        register("kmpTestPlugin") {
            id = "com.wealthvault.build_logic.test"
            implementationClass = "com.wealthvault.build_logic.plugin.KmpTestConventionPlugin"
        }

        register("kmpApplicationPlugin") {
            id = "com.wealthvault.build_logic.application"
            implementationClass = "com.wealthvault.build_logic.plugin.KmpApplicationConventionPlugin"
        }
    }
}
