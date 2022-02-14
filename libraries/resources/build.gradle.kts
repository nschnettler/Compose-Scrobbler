plugins {
    id("com.android.library")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    implementation(AndroidX.appCompat)
}