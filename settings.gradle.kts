rootProject.name = "Wealthvault_final"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")

include(":functional:api:auth-api")
include(":functional:data-store")
include(":functional:setting-app")
include(":functional:api:google-auth")
include(":functional:api:user-api")


include(":features:auth:login")
include(":features:auth:register")



include(":base:core")
include(":base:config")
//include(":build-logic")
include(":features:profile")
include(":navigation")

