plugins {
    id ("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}
android {
    compileSdkVersion(29)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerVersion = "1.3.70-dev-withExperimentalGoogleExtensions-20200424"
        kotlinCompilerExtensionVersion = "0.1.0-dev11"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Kotlin.stdlib.jdk7)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.ui.layout)
    implementation(AndroidX.ui.material)
    implementation(AndroidX.ui.tooling)
    implementation(AndroidX.ui.foundation)
    testImplementation(Testing.junit4)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
}
