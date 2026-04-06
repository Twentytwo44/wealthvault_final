import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("com.google.gms.google-services")

}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export(project(":functional:api:line-auth"))
            export(project(":features:auth:login"))
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(project(":base:core"))
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.messaging)
            implementation("com.google.firebase:firebase-common-ktx")

        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            val voyagerVersion = "1.0.0"
            implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")


            implementation(project(":functional:data-store"))
            implementation(project(":navigation"))
            implementation(project(":base:core"))
            api(project(":features:auth:login"))
            implementation(project(":features:auth:login"))
            implementation(project(":features:auth:register"))
            implementation(project(":features:dashboard"))
            implementation(project(":features:notification"))
            implementation(project(":features:manage:financialList"))
            implementation(project(":features:social"))
            implementation(project(":features:profile"))

            implementation(project(":functional:api:account-api"))
            implementation(project(":functional:api:auth-api"))
            implementation(project(":functional:api:building-api"))
            implementation(project(":functional:api:cash-api"))

            implementation(project(":functional:api:google-auth"))
            implementation(project(":functional:api:insurance-api"))
            implementation(project(":functional:api:investment-api"))
            implementation(project(":functional:api:land-api"))
            implementation(project(":functional:api:liability-api"))
            implementation(project(":functional:api:user-api"))
            api(project(":functional:api:line-auth"))
            implementation(project(":functional:notification"))
            implementation(project(":functional:api:setup-api"))
            implementation(project(":functional:api:group-api"))
            implementation(project(":functional:api:insurance-api"))



            implementation(project(":features:manage:form"))

            implementation(project(":features:auth:register"))
            implementation(project(":features:dashboard"))
            implementation(project(":features:notification"))
            implementation(project(":features:manage:financialList"))
            implementation(project(":features:social"))
            implementation(project(":features:profile"))




//            implementation(project(":features:auth:introduction"))







        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.wealthvault.wealthvault_final"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.wealthvault.wealthvault_final"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.1")
}

compose {
    resources {
        // เพื่อให้โมดูลหลักรู้จักคลาส Res จากโมดูลอื่น
        publicResClass = true
    }
}

