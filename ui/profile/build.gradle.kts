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
        kotlinCompilerExtensionVersion = "1.0.0-beta03"
    }
}

dependencies {
    // Modules
    implementation(project(":features:settings"))

    implementation(project(":libraries:persistence"))


    implementation(project(":common"))
    implementation(project(":ui:common:compose"))
    implementation(project(":libraries:resources"))
    implementation(project(":ui:common:util"))
    implementation(project(":repo"))

    implementation(AndroidX.lifecycle.viewModelKtx)
    api(AndroidX.browser)

    implementation(AndroidX.hilt.lifecycleViewModel)
    kapt(AndroidX.hilt.compiler)
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)

    coreLibraryDesugaring("com.android.tools", "desugar_jdk_libs", "_")
}