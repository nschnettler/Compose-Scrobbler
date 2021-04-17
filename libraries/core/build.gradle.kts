plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(24)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Modules
    implementation(project(":libraries:model"))
    implementation(project(":libraries:network:lastfm"))

    // AndroidX
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.lifecycle.viewModelKtx)

    // KotlinX
    implementation(KotlinX.coroutines.core)

    // Network & Serialization
    compileOnly(Square.Retrofit2.retrofit)

    // Store
    compileOnly("com.dropbox.mobile.store", "store4", "_")

    // Other
    implementation(JakeWharton.timber)

    // Testing
    testImplementation(Testing.junit4)
    testImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
}