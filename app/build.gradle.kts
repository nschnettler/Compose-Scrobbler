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
        kotlinCompilerExtensionVersion = "0.1.0-dev13"
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
    implementation("androidx.ui","ui-livedata","0.1.0-dev13")
    implementation(AndroidX.lifecycle.extensions)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.lifecycle.viewModelSavedState)

    //Google
    implementation(Google.android.material)

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.4")

    //Other
    val versionAccompanist = "0.1.5"
    val versionCoil = "0.11.0"

    implementation("io.coil-kt", "coil", versionCoil)
    implementation("dev.chrisbanes.accompanist","accompanist-mdc-theme", versionAccompanist)
    implementation("dev.chrisbanes.accompanist","accompanist-coil", versionAccompanist)
    implementation("com.jakewharton.threetenabp","threetenabp","1.2.4")
    implementation("com.github.etiennelenhart","eiffel","4.1.0")
    implementation("androidx.hilt","hilt-lifecycle-viewmodel","1.0.0-alpha01")
    kapt("androidx.hilt","hilt-compiler","1.0.0-alpha01")
    implementation("com.google.dagger","hilt-android","2.28-alpha")
    kapt("com.google.dagger","hilt-android-compiler","2.28-alpha")
    debugImplementation("com.amitshekhar.android","debug-db","1.0.6")
    implementation("com.github.mvarnagiris.compose-navigation:navigation:0.3.2")
}