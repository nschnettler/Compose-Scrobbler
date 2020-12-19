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
    buildFeatures {
        compose = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerVersion = "1.4.21"
        kotlinCompilerExtensionVersion = "1.0.0-alpha09"
    }
}

dependencies {
    implementation(project(":ui:common:compose"))
    implementation(project(":ui:common:resources"))
    implementation(project(":ui:common:util"))
    implementation(project(":repo"))

    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.browser)

    implementation(AndroidX.hilt.lifecycleViewModel)
    kapt(AndroidX.hilt.compiler)
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)

    coreLibraryDesugaring("com.android.tools", "desugar_jdk_libs", "_")
}