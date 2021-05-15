plugins {
    id("com.android.library")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    implementation(AndroidX.appCompat)
}