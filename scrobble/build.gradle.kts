plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
    }
}

dependencies {
    implementation(project(":common"))
    api(project(":repo"))

    implementation(Kotlin.stdlib.jdk8)
    implementation(KotlinX.coroutines.core)

    // Dagger
    implementation("androidx.hilt","hilt-lifecycle-viewmodel","1.0.0-alpha01")
    kapt("androidx.hilt","hilt-compiler","1.0.0-alpha01")
    implementation("com.google.dagger","hilt-android","2.28-alpha")
    kapt("com.google.dagger","hilt-android-compiler","2.28-alpha")
    implementation(AndroidX.work.runtimeKtx)
}