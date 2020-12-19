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
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(project(":ui:common:compose"))
    implementation(project(":ui:common:resources"))
    implementation(project(":repo"))

    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.extended)
    implementation("dev.chrisbanes.accompanist", "accompanist-coil", "_")
    implementation("dev.chrisbanes.accompanist", "accompanist-insets", "_")
}