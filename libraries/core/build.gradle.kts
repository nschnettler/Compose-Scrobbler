plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    val lastfmKey: String? by project
    val lastFmSecret: String? by project

    buildTypes {
        buildTypes.forEach {
            it.buildConfigField("String", "LASTFM_API_KEY", lastfmKey ?: "\"\"")
            it.buildConfigField("String", "LASTFM_SECRET", lastFmSecret ?: "\"\"")
        }
    }
}

dependencies {
    // Modules
    implementation(project(":libraries:model"))

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