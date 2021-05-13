plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(24)
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta03"
    }
}

dependencies {
    // Modules
    implementation(project(":libraries:core"))
    implementation(project(":libraries:model"))
    implementation(project(":libraries:compose"))
    implementation(project(":libraries:network:common"))
    implementation(project(":libraries:network:lastfm"))
    implementation(project(":libraries:persistence"))
    implementation(project(":libraries:submission"))

    // Compose
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.compose.runtime.liveData)
    implementation(Google.accompanist.insets)

    // AndroidX
    implementation(AndroidX.room.common)
    implementation(AndroidX.lifecycle.viewModelKtx)

    // Network & Serialization
    implementation(Square.Retrofit2.retrofit)
    implementation(Square.Moshi.kotlinCodegen)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Repository
    implementation("com.dropbox.mobile.store", "store4", "_")

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)
}