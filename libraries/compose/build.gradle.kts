plugins {
    id("com.android.library")
    id("kotlin-android")
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
    implementation(project(":libraries:core"))
    implementation(project(":libraries:model"))
    implementation(project(":ui:common:resources"))

    // AndroidX
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.paletteKtx)

    // Compose
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.extended)
    implementation("androidx.compose.ui", "ui-tooling", "_")
    implementation("androidx.navigation", "navigation-compose", "_")
    implementation("androidx.constraintlayout", "constraintlayout-compose", "_")
    implementation("com.google.accompanist", "accompanist-coil", "_")
    implementation("com.google.accompanist", "accompanist-insets", "_")
    implementation("com.google.accompanist", "accompanist-pager", "_")
    implementation("com.google.accompanist", "accompanist-flowlayout", "_")

    // Other
    implementation(JakeWharton.timber)
}