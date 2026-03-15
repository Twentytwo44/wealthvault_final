rootProject.name = "Wealthvault_final"
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

include(":functional:api:auth-api")
include(":functional:data-store")
include(":functional:setting-app")
include(":functional:api:google-auth")
include(":functional:api:user-api")
include(":functional:api:account-api")
include(":functional:api:cash-api")
include(":functional:api:insurance-api")
include(":functional:api:investment-api")
include(":functional:api:building-api")
include(":functional:api:land-api")
include(":functional:api:liability-api")
include(":functional:api:line-auth")

include(":functional:notification")








include(":features:auth:login")
include(":features:auth:register")

include(":features:dashboard")



include(":base:core")
include(":base:config")
//include(":build-logic")
include(":features:profile")
include(":navigation")

include(":features:manage:financial-asset")
include(":features:manage:financial-obligations")
