plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(29)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(project(":database"))
    api(project(":lastfm"))
    implementation(project(":common"))

    implementation(KotlinX.coroutines.core)
    implementation(Square.okHttp3.okHttp)

    implementation("org.jetbrains.kotlin","kotlin-stdlib","1.3.72")
    implementation("com.google.dagger","hilt-android","2.28-alpha")
    kapt("com.google.dagger","hilt-android-compiler","2.28-alpha")
    api("com.dropbox.mobile.store","store4","4.0.0-alpha06")
}