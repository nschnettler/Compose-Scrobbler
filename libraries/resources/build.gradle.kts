plugins {
    id("com.android.library")
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    implementation(AndroidX.appCompat)
}