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
        kotlinCompilerExtensionVersion = "1.0.0-alpha11"
    }
}

dependencies {
    implementation(project(":repo"))
    implementation(project(":ui:common:util"))
    implementation(project(":ui:common:resources"))

    implementation(AndroidX.lifecycle.viewModelKtx)

    api(AndroidX.compose.runtime)
    api(AndroidX.compose.foundation)
    api(AndroidX.compose.material)
    api(AndroidX.compose.material.icons.extended)
    implementation("androidx.navigation:navigation-compose:_")

    implementation(AndroidX.paletteKtx)
    implementation(AndroidX.core.ktx)

    api("dev.chrisbanes.accompanist", "accompanist-coil", "_")
    api("dev.chrisbanes.accompanist", "accompanist-insets", "_")
}