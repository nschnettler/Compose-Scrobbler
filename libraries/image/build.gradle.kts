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
    // Libraries
    implementation(project(":libraries:core"))
    implementation(project(":libraries:model"))
    implementation(project(":libraries:persistence"))
    implementation(project(":libraries:network:common"))
    implementation(project(":libraries:network:spotify"))

    // Android X
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.hilt.work)
    implementation(AndroidX.room.ktx)

    // Retrofit
    implementation(Square.Retrofit2.retrofit)

    // Moshi
    implementation(Square.Moshi.kotlinCodegen)
    implementation(Square.Retrofit2.converter.moshi)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)

    // Other
    implementation(JakeWharton.timber)
}