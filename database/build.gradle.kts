plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")
    api("androidx.room:room-runtime:2.2.5")
    implementation("androidx.room:room-ktx:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")
}