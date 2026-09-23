rootProject.name = "WealthVault"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

include(":composeApp")
include(":androidApp")
include(":benchmarks")
include(":data:auth")
include(":data:dashboard")
include(":data:notification")
include(":data:profile")
include(":data:social")
include(":data:portfolio")
include(":base:network")
include(":base:security")








include(":features:auth:login")

include(":features:dashboard")
include(":features:notification")


include(":base:core")
include(":base:config")
include(":base:database")
include(":domain:auth")
include(":domain:profile")
include(":domain:social")
include(":domain:portfolio")
include(":domain:notification")
//include(":build-logic")
include(":features:profile")
include(":main")

include(":features:manage:financialList")

include(":features:social")
