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
        kotlinCompilerVersion = "1.3.70-dev-withExperimentalGoogleExtensions-20200424"
        kotlinCompilerExtensionVersion = "0.1.0-dev14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
    implementation(AndroidX.ui.layout)
    implementation(AndroidX.ui.material)
    implementation(AndroidX.ui.tooling)
    implementation("androidx.ui","ui-livedata","0.1.0-dev14")
    implementation(AndroidX.lifecycle.extensions)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.lifecycle.viewModelSavedState)

    //Google
    implementation(Google.android.material)

    //Other
    val versionAccompanist = "0.1.6"
    val versionCoil = "0.11.0"
    val versionNavigator = "0.3.3"
    val versionHilt = "1.0.0-alpha01"
    val versionDagger = "2.28-alpha"
    val versionThreeten = "1.2.4"
    val versionLeakCanary = "2.4"

    implementation("io.coil-kt", "coil", versionCoil)
    implementation("dev.chrisbanes.accompanist","accompanist-mdc-theme", versionAccompanist)
    implementation("dev.chrisbanes.accompanist","accompanist-coil", versionAccompanist)
    implementation("com.jakewharton.threetenabp","threetenabp", versionThreeten)
    implementation("com.github.mvarnagiris.compose-navigation", "navigation", versionNavigator)
    debugImplementation("com.squareup.leakcanary", "leakcanary-android", versionLeakCanary)

    //Dagger
    implementation("androidx.hilt","hilt-lifecycle-viewmodel", versionHilt)
    implementation("com.google.dagger","hilt-android",versionDagger)
    kapt("androidx.hilt","hilt-compiler", versionHilt)
    kapt("com.google.dagger","hilt-android-compiler",versionDagger)
}