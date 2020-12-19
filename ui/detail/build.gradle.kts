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
        kotlinCompilerVersion = "1.4.21"
        kotlinCompilerExtensionVersion = "1.0.0-alpha09"
    }
}

dependencies {
    implementation(project(":ui:common:compose"))
    implementation(project(":ui:common:resources"))
    implementation(project(":ui:common:util"))
    implementation(project(":repo"))

    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.browser)

    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.extended)
    implementation("dev.chrisbanes.accompanist", "accompanist-coil", "_")
    implementation("dev.chrisbanes.accompanist", "accompanist-insets", "_")

    implementation(AndroidX.hilt.lifecycleViewModel)
    kapt(AndroidX.hilt.compiler)
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)
}