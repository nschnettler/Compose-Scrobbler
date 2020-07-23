plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "de.schnettler.scrobbler"
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerVersion = "1.4.0-dev-withExperimentalGoogleExtensions-20200720"
        kotlinCompilerExtensionVersion = "0.1.0-dev15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check")
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":repo"))
    implementation(project(":common"))
    implementation(project(":scrobble"))

    //Kotlin
    implementation(Kotlin.stdlib.jdk8)

    //AndroidX
    implementation(AndroidX.browser)
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.activityKtx)
    implementation("androidx.compose.foundation", "foundation-layout", "_")
    implementation("androidx.compose.material", "material", "_")
    implementation("androidx.ui", "ui-tooling", "_")
    implementation("androidx.compose.runtime","runtime-livedata","_")
    implementation(AndroidX.lifecycle.extensions)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.lifecycle.viewModelSavedState)
    implementation(AndroidX.work.runtimeKtx)

    //Google
    implementation(Google.android.material)

    implementation("io.coil-kt", "coil", "_")
    implementation("dev.chrisbanes.accompanist","accompanist-coil", "_")
    implementation("com.jakewharton.threetenabp","threetenabp", "_")
    implementation("com.github.mvarnagiris.compose-navigation", "navigation", "_")
    debugImplementation("com.squareup.leakcanary", "leakcanary-android", "_")

    //Dagger
    implementation("androidx.hilt","hilt-lifecycle-viewmodel", "_")
    implementation("androidx.hilt", "hilt-work", "_")
    implementation("com.google.dagger","hilt-android","_")
    kapt("androidx.hilt","hilt-compiler", "_")
    kapt("com.google.dagger","hilt-android-compiler","_")
}