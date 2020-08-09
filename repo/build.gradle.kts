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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(project(":database"))
    api(project(":lastfm"))
    implementation(project(":common"))

    implementation(KotlinX.coroutines.core)
    implementation(Square.okHttp3.okHttp)
    implementation(AndroidX.work.runtimeKtx)

    implementation("org.jetbrains.kotlin", "kotlin-stdlib", "1.3.72")
    implementation("androidx.hilt", "hilt-lifecycle-viewmodel", "_")
    kapt("androidx.hilt", "hilt-compiler", "_")
    implementation("com.google.dagger", "hilt-android", "_")
    kapt("com.google.dagger", "hilt-android-compiler", "_")
    implementation("androidx.hilt", "hilt-work", "_")
    api("com.dropbox.mobile.store", "store4", "_")
    api("com.github.tfcporciuncula", "flow-preferences", "_")
}