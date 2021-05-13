import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.library")
    kotlin("android")
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
        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.ui)
    }
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar")))) // TODO: Should be impl

    // Libraries
    implementation(project(":libraries:compose")) // Only needed for MediaCardSize enum
    implementation(project(":libraries:persistence"))

    // Compose
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.extended)

    // Android X
    implementation(AndroidX.dataStore.preferences)
}