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
        targetSdkVersion(30)
        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        excludes += "/META-INF/AL2.0"
        excludes += "/META-INF/LGPL2.1"
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
    implementation(Google.dagger.hilt.android)
    kapt(AndroidX.hilt.compiler)
    kapt(Google.dagger.hilt.android.compiler)

    api("com.dropbox.mobile.store", "store4", "_")
    api("com.github.tfcporciuncula", "flow-preferences", "_")

    androidTestImplementation(Testing.junit4)
    androidTestImplementation(KotlinX.coroutines.test)
    androidTestImplementation(AndroidX.test.ext.junitKtx)
    androidTestImplementation(AndroidX.archCore.testing)
    androidTestImplementation(AndroidX.test.rules)
    androidTestImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
    androidTestImplementation(Square.moshi.kotlinReflect)
    androidTestImplementation("app.cash.turbine:turbine:0.2.0")
}