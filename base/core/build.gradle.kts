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

        }
    }

}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "com.wealthvault.core.generated.resources"
    }
}
