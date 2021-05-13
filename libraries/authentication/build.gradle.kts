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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Libraries
    implementation(project(":libraries:core"))
    implementation(project(":libraries:persistence"))
    implementation(project(":libraries:network:common"))
    implementation(project(":libraries:network:lastfm"))
    implementation(project(":libraries:network:spotify"))

    // Android X
    implementation(AndroidX.room.ktx)

    // Network & Serialization
    implementation(Square.Retrofit2.retrofit)
    implementation(Square.Moshi.kotlinCodegen)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Repository
    implementation("com.dropbox.mobile.store", "store4", "_")

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)
}