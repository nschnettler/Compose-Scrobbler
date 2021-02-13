plugins {
    id("com.android.library")
    kotlin("android")
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-alpha11"
    }
}

dependencies {
    // Modules
    implementation(project(":common"))
    implementation(project(":ui:common:compose"))
    implementation(project(":ui:common:resources"))
    implementation(project(":ui:common:util"))
    implementation(project(":repo"))
    implementation(project(":network:lastfm")) // TODO: This should not be here

    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.browser)

    implementation(AndroidX.hilt.lifecycleViewModel)
    kapt(AndroidX.hilt.compiler)
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)
}