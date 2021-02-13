plugins {
    id("com.android.application")
    kotlin("android")
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
        kotlinCompilerExtensionVersion = "1.0.0-alpha12"
    }

    compileOptions {
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

hilt {
    enableExperimentalClasspathAggregation = true
}

dependencies {
    implementation(project(":repo"))
    implementation(project(":common"))
    implementation(project(":scrobble"))
    implementation(project(":ui:common:compose"))
    implementation(project(":ui:common:util"))
    implementation(project(":ui:settings"))
    implementation(project(":ui:charts"))
    implementation(project(":ui:profile"))
    implementation(project(":ui:detail"))
    implementation(project(":ui:search"))
    implementation(project(":ui:history"))

    // AndroidX
    implementation(AndroidX.appCompat)
    implementation(AndroidX.compose.runtime.liveData)
    implementation("androidx.navigation:navigation-compose:_")
    implementation("androidx.activity:activity-compose:_")
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.hilt.lifecycleViewModel)
    implementation(AndroidX.hilt.work)
    kapt(AndroidX.hilt.compiler)

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)

    // Other
    debugImplementation(Square.leakCanary.android)
}