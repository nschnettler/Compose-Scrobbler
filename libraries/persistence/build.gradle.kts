plugins {
    id("com.android.library")
    id("kotlin-android")
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
    implementation("androidx.datastore", "datastore-preferences", "_")
}