plugins {
    id ("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(29)
}

dependencies {
    api(project(":common"))

    api("com.squareup.retrofit2:retrofit:2.8.1")
    implementation("com.squareup.retrofit2:converter-moshi:2.8.1")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation("com.squareup.retrofit2:converter-scalars:2.8.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.9.2")
    implementation("com.serjltt.moshi:moshi-lazy-adapters:2.2")
    implementation("com.squareup.okhttp3:okhttp:4.4.0")
    api("com.squareup.okhttp3:logging-interceptor:4.7.2")
}