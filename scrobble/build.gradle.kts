plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Modules
    implementation(project(":libraries:persistence"))
    implementation(project(":libraries:network:lastfm")) // TODO: This should not be here. Errors need to be mapped

    implementation(project(":common"))
    implementation(project(":repo"))

    implementation(KotlinX.coroutines.core)
    implementation(AndroidX.work.runtimeKtx)
    implementation(Google.dagger.hilt.android)
    implementation(AndroidX.lifecycle.service)
    implementation(AndroidX.lifecycle.runtimeKtx)
    kapt(AndroidX.hilt.compiler)
    kapt(Google.dagger.hilt.android.compiler)
}