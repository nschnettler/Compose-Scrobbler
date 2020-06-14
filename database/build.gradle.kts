plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)
}

dependencies {
    implementation(Kotlin.stdlib.jdk8)
    implementation(AndroidX.room.ktx)
    api(AndroidX.room.runtime)
    kapt(AndroidX.room.compiler)
}