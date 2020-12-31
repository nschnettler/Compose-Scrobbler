plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(24)
    }
}

dependencies {
    implementation(AndroidX.core.ktx)

    // Testing
    testImplementation(Testing.junit4)
//    androidTestImplementation(KotlinX.coroutines.test)
//    androidTestImplementation(AndroidX.test.ext.junit)
    testImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
}