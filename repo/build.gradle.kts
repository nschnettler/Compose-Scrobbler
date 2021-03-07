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
        resources {
            excludes.apply {
                add("/META-INF/AL2.0")
                add("/META-INF/LGPL2.1")
            }
        }
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    // Modules
    implementation(project(":common"))
    implementation(project(":network:spotify"))
    implementation(project(":network:lastfm"))
    api(project(":database"))

    // Hilt
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)
    kapt(AndroidX.hilt.compiler)

    // Work
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.hilt.work)

    // Retrofit
    implementation(Square.Retrofit2.retrofit)

    // Other
    implementation(KotlinX.coroutines.core)
    implementation("androidx.datastore:datastore-preferences:_")
    api("com.dropbox.mobile.store", "store4", "_")

    // Instrumented Test
    androidTestImplementation(Testing.junit4)
    androidTestImplementation(KotlinX.coroutines.test)
    androidTestImplementation(AndroidX.test.ext.junitKtx)
    androidTestImplementation(AndroidX.archCore.testing)
    androidTestImplementation(AndroidX.test.rules)
    androidTestImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
    androidTestImplementation(Square.moshi.kotlinReflect)
    androidTestImplementation("app.cash.turbine:turbine:0.2.0")

    // Test
    testImplementation(Testing.junit4)
    testImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
}