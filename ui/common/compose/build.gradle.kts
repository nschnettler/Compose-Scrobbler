plugins {
    id("com.android.library")
    kotlin("android")
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta03"
    }
}

dependencies {
    // Modules
    implementation(project(":common"))
    implementation(project(":repo"))
    implementation(project(":ui:common:util"))
    implementation(project(":ui:common:resources"))
    implementation(project(":network:lastfm")) // TODO: This should not be here

    // Retrofit TODO: This should not be here
    implementation(Square.Retrofit2.retrofit)

    // ViewModel
    implementation(AndroidX.lifecycle.viewModelKtx)

    // Compose
    api(AndroidX.compose.runtime)
    api(AndroidX.compose.foundation)
    api(AndroidX.compose.material)
    api(AndroidX.compose.material.icons.extended)
    api("androidx.compose.ui:ui-tooling:_")
    api("androidx.constraintlayout:constraintlayout-compose:_")
    implementation("androidx.navigation:navigation-compose:_")
    api(AndroidX.compose.runtime.liveData)

    // Other
    implementation(AndroidX.paletteKtx)
    implementation(AndroidX.core.ktx)

    // Coil & Insets
    api("com.google.accompanist", "accompanist-coil", "_")
    api("com.google.accompanist", "accompanist-insets", "_")
}