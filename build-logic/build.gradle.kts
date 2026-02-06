plugins {
    `kotlin-dsl` // สำคัญมาก: เพื่อให้เขียน Gradle ด้วย Kotlin ในโฟลเดอร์นี้ได้
}

group = "com.wealthvault.buildlogic"

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.build.gradle)
    implementation(libs.mokkery.gradle)
}

gradlePlugin {
    plugins {
        register("kmpLibraryPlugin") {
            id = "me.orbitalno11.kmptemplate.library"
            implementationClass = "com.wealthvault.buildlogic.plugin.KmpLibraryConventionPlugin"
        }

        register("kmpComposePlugin") {
            id = "me.orbitalno11.kmptemplate.compose"
            implementationClass = "com.wealthvault.buildlogic.plugin.KmpComposeConventionPlugin"
        }
//
//        register("kmpTestPlugin") {
//            id = "me.orbitalno11.kmptemplate.test"
//            implementationClass = "me.orbitalno11.buildLogic.plugin.KmpTestConventionPlugin"
//        }
//
//        register("kmpApplicationPlugin") {
//            id = "me.orbitalno11.kmptemplate.application"
//            implementationClass = "me.orbitalno11.buildLogic.plugin.KmpApplicationConventionPlugin"
//        }
    }
}