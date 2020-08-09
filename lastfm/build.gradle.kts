plugins {
    id("com.android.library")
    kotlin("android")
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
    api(project(":common"))

    api(Square.retrofit2.retrofit)
    implementation(Square.retrofit2.converter.moshi)
    implementation(Square.retrofit2.converter.scalars)
    implementation(Square.okHttp3.okHttp)
    api(Square.okHttp3.loggingInterceptor)

    implementation("com.squareup.moshi", "moshi-kotlin", "_")
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")
}