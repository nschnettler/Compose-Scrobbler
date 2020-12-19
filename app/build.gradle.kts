plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "de.schnettler.scrobbler"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerVersion = "1.4.21"
        kotlinCompilerExtensionVersion = "1.0.0-alpha09"
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        freeCompilerArgs += listOf(
            "-Xallow-jvm-ir-dependencies",
            "-Xskip-prerelease-check"
        )
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
        }
    }

    packagingOptions {
        resources {
            excludes.apply {
                add("/META-INF/AL2.0")
                add("/META-INF/LGPL2.1")
            }
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(project(":repo"))
    implementation(project(":common"))
    implementation(project(":scrobble"))
    implementation(project(":ui:common:compose"))
    implementation(project(":ui:common:util"))
    implementation(project(":ui:settings"))
    implementation(project(":ui:charts"))
    implementation(project(":ui:profile"))
    implementation(project(":ui:detail"))

    // AndroidX
    implementation(AndroidX.browser)
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.activityKtx)
    implementation(AndroidX.compose.runtime.liveData)
    implementation("androidx.navigation:navigation-compose:_")
    implementation(AndroidX.ui.tooling)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.lifecycle.viewModelSavedState)
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.paletteKtx)
    implementation(AndroidX.hilt.lifecycleViewModel)
    implementation(AndroidX.hilt.work)
    kapt(AndroidX.hilt.compiler)

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)

    // Other
    debugImplementation(Square.leakCanary.android)
    implementation("dev.chrisbanes.accompanist", "accompanist-coil", "_")
    implementation("dev.chrisbanes.accompanist", "accompanist-insets", "_")
    coreLibraryDesugaring("com.android.tools", "desugar_jdk_libs", "_")

    // Testing
    testImplementation(Testing.junit4)
    androidTestImplementation(KotlinX.coroutines.test)
    androidTestImplementation(AndroidX.test.ext.junit)
    testImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
}