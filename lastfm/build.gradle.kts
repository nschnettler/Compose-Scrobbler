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
    api(project(":common"))

    api(Square.retrofit2.retrofit)
    api(Square.retrofit2.converter.moshi)
    implementation(Square.retrofit2.converter.scalars)
    implementation(Square.okHttp3.okHttp)
    api(Square.okHttp3.loggingInterceptor)
    debugApi("com.github.chuckerteam.chucker:library:3.3.0")
    releaseApi("com.github.chuckerteam.chucker:library-no-op:3.3.0")

    kapt(Square.moshi.kotlinCodegen)
    implementation(Square.moshi.kotlinReflect)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    implementation(Google.dagger.hilt.android)
    kapt(AndroidX.hilt.compiler)
    kapt(Google.dagger.hilt.android.compiler)

    implementation(KotlinX.coroutines.android)
}