plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta03"
    }
}

dependencies {
    // Libraries
    implementation(project(":libraries:core"))
    implementation(project(":libraries:model"))
    implementation(project(":libraries:image"))
    implementation(project(":libraries:compose"))
    implementation(project(":libraries:persistence"))
    implementation(project(":libraries:network:lastfm"))
    implementation(project(":libraries:network:common"))
    implementation(project(":libraries:authentication"))

    // Compose
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.extended)
    implementation("androidx.compose.ui", "ui-tooling", "_")
    implementation("com.google.accompanist", "accompanist-insets", "_")
    implementation("com.google.accompanist", "accompanist-coil", "_")

    // AndroidX
    implementation(AndroidX.room.common)
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.browser)

    // Network & Serialization
    implementation(Square.Retrofit2.retrofit)
    implementation(Square.Moshi.kotlinCodegen)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Repository
    implementation("com.dropbox.mobile.store", "store4", "_")

    // Dagger
    implementation(AndroidX.hilt.lifecycleViewModel)
    kapt(AndroidX.hilt.compiler)
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)

    // Other
    implementation(JakeWharton.timber)
    coreLibraryDesugaring("com.android.tools", "desugar_jdk_libs", "_")
}