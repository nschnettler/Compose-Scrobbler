plugins {
    id("com.android.library")
    id("kotlin-android")
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
    // Modules
    implementation(project(":libraries:model"))
    implementation(project(":libraries:network:common"))
    implementation(project(":libraries:network:lastfm"))
    implementation(project(":libraries:persistence"))

    // Network & Serialization
    implementation(Square.Retrofit2.retrofit)
    implementation(Square.moshi.kotlinCodegen)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Repository
    implementation("com.dropbox.mobile.store", "store4", "_")

    // AndroidX
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.hilt.work)
    implementation(AndroidX.room.common)

    // Dagger
    implementation(AndroidX.hilt.lifecycleViewModel)
    kapt(AndroidX.hilt.compiler)
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)

    // Other
    implementation(JakeWharton.timber)
}