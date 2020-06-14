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

    implementation("com.jakewharton.retrofit","retrofit2-kotlin-coroutines-adapter","0.9.2")
    implementation("com.squareup.moshi","moshi-kotlin","1.9.2")
    implementation("com.serjltt.moshi","moshi-lazy-adapters","2.2")
}