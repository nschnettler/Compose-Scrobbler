plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // OkHttp
    implementation(Square.OkHttp3.okHttp)

    // Interceptor
    implementation(Square.OkHttp3.loggingInterceptor)
    debugImplementation("com.github.chuckerteam.chucker", "library", "_")
    releaseImplementation("com.github.chuckerteam.chucker", "library-no-op", "_")

    // Moshi
    implementation(Square.Moshi.kotlinCodegen)
    implementation(Square.Retrofit2.converter.moshi)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Hilt
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)
}