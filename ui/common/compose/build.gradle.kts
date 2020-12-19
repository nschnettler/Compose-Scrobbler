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
        kotlinCompilerVersion = "1.4.21"
        kotlinCompilerExtensionVersion = "1.0.0-alpha09"
    }
}

dependencies {
    implementation(project(":repo"))
    implementation(project(":ui:common:util"))
    implementation(project(":ui:common:resources"))

    implementation(AndroidX.compose.runtime)
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.extended)

    implementation(AndroidX.paletteKtx)
    implementation(AndroidX.core.ktx)

    implementation("dev.chrisbanes.accompanist", "accompanist-coil", "_")
}