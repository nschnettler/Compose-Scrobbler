import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 24
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.ui)
    }
}

dependencies {
    // Modules
    implementation(project(":libraries:core"))
    implementation(project(":libraries:compose"))
    implementation(project(":libraries:network:lastfm"))

    // Compose
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(Google.Accompanist.insets)

    // AndroidX
    implementation(AndroidX.lifecycle.viewModelKtx)

    // KotlinX
    implementation(KotlinX.coroutines.core)

    // Network & Serialization
    implementation(Square.Retrofit2.retrofit)
    kapt(Square.Moshi.kotlinCodegen)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Repository
    implementation("com.dropbox.mobile.store", "store4", "_")

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)

    // Other
    implementation(JakeWharton.timber)
}