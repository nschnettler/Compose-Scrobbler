plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
    }

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
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.hilt.work)
    implementation(Google.dagger.hilt.android.core)
    kapt(AndroidX.hilt.compiler)
    kapt(Google.dagger.hilt.android.compiler)

    api("com.dropbox.mobile.store", "store4", "_")
    api("com.github.tfcporciuncula", "flow-preferences", "_")
}