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

    val spotifyAuth: String? by project

    buildTypes {
        buildTypes.forEach {
            it.buildConfigField("String", "SPOTIFY_AUTH", spotifyAuth ?: "\"\"")
        }
    }
}

dependencies {
    // Modules
    implementation(project(":libraries:network:common"))

    // Retrofit
    implementation(Square.Retrofit2.retrofit)

    // Moshi
    implementation(Square.Moshi.kotlinCodegen)
    implementation(Square.Retrofit2.converter.moshi)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Interceptor
    implementation(Square.OkHttp3.loggingInterceptor)
    debugImplementation("com.github.chuckerteam.chucker", "library", "_")
    releaseImplementation("com.github.chuckerteam.chucker", "library-no-op", "_")

    // Hilt
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)

    // KotlinX
    implementation(KotlinX.coroutines.core)
}