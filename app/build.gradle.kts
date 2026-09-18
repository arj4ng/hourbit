plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    // enables @Serializable classes/objects
    alias(libs.plugins.kotlin.serialization)

    // enables code generation used by Room
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.arj4ng.hourbit"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.arj4ng.hourbit"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // adds Jetpack Compose Navigation
    // used for NavHost, NavController and moving between screens
    implementation(libs.androidx.navigation.compose)

    // serialization runtime used by my type safe @Serializable navigation routes
    implementation(libs.kotlinx.serialization.json)

    // room runtime gives the app access to the local database API.
    implementation(libs.androidx.room.runtime)

    // kotlin extensions for Room, used for coroutines and Flow
    implementation(libs.androidx.room.ktx)

    // room compiler processes @Entity, @Dao and @Database annotations
    // ksp() means this dependency is used for code generation, not bundled into the app
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}