plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }

    testOptions.unitTests {
        isIncludeAndroidResources = true
        isReturnDefaultValues = true
    }

    packagingOptions {
        exclude ("**/attach_hotspot_windows.dll")
        exclude( "META-INF/licenses/**")
        exclude ("META-INF/AL2.0")
        exclude ("META-INF/LGPL2.1")
    }
}

dependencies {
    implementation(Kotlin.stdlib.jdk8)
    implementation(AndroidX.room.ktx)
    api(AndroidX.room.runtime)
    kapt(AndroidX.room.compiler)

    androidTestImplementation(Testing.junit4)
    androidTestImplementation(Testing.mockito.core)
    androidTestImplementation(KotlinX.coroutines.test)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.test.espresso.contrib)
    androidTestImplementation(AndroidX.archCore.testing)
    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito:2.28.0")
    androidTestImplementation("ch.tutteli.atrium:atrium-fluent-en_GB:0.12.0")
}