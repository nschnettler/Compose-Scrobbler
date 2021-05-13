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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar")))) // TODO: Should be impl

    // Libraries
    implementation(project(":libraries:model"))

    // AndroidX
    implementation(AndroidX.room.common)
    implementation(AndroidX.dataStore.preferences)

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)
}