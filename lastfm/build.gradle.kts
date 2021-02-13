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
        targetSdkVersion(30)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Modules
    implementation(project(":common"))
    implementation(project(":network:common"))

    // Retrofit & OkHttp
    api(Square.retrofit2.retrofit)
    api(Square.retrofit2.converter.moshi)
    implementation(Square.retrofit2.converter.scalars)
    implementation(Square.okHttp3.okHttp)
    implementation(Square.okHttp3.loggingInterceptor)

    // Interceptor
    debugApi("com.github.chuckerteam.chucker:library:3.3.0")
    releaseApi("com.github.chuckerteam.chucker:library-no-op:3.3.0")

    // Msohi
    kapt(Square.moshi.kotlinCodegen)
    implementation(Square.moshi.kotlinReflect)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(AndroidX.hilt.compiler)
    kapt(Google.dagger.hilt.android.compiler)

    // Coroutines
    implementation(KotlinX.coroutines.android)
}