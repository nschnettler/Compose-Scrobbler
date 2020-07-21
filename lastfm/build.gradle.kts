plugins {
    id ("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(29)
}

dependencies {
    api(project(":common"))

    api(Square.retrofit2.retrofit)
    implementation(Square.retrofit2.converter.moshi)
    implementation(Square.retrofit2.converter.scalars)
    implementation(Square.okHttp3.okHttp)
    api(Square.okHttp3.loggingInterceptor)

    implementation("com.squareup.moshi","moshi-kotlin","_")
    implementation("com.serjltt.moshi","moshi-lazy-adapters","_")
}