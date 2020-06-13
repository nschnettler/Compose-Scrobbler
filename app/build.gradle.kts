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
        minSdkVersion(21)
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.browser:browser:1.2.0")

    implementation(project(":repo"))
    implementation(project(":common"))
    implementation(project(":router"))

    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")
    //AppCompat
    implementation("androidx.core:core-ktx:1.2.01.2.0")
    implementation("androidx.appcompat:appcompat:$1.1.0")
    implementation("androidx.activity:activity-ktx:$1.1.0")
    //Material
    implementation("com.google.android.material:material:$1.1.0")
    //Compose
    implementation("androidx.ui:ui-layout:$0.1.0-dev11")
    implementation("androidx.ui:ui-material:$0.1.0-dev11")
    implementation("androidx.ui:ui-tooling:$0.1.0-dev11")
    implementation("androidx.ui:ui-livedata:$0.1.0-dev11")

    //Lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:$2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$2.2.0")

    //Koil
    implementation("io.coil-kt:coil:$0.11.0")
    implementation("dev.chrisbanes.accompanist:accompanist-mdc-theme:$0.1.1")
    implementation("dev.chrisbanes.accompanist:accompanist-coil:$0.1.1")

    implementation("com.jakewharton.threetenabp:threetenabp:1.2.4")
    implementation("com.github.etiennelenhart:eiffel:$4.1.0")

    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha01")
    kapt("androidx.hilt:hilt-compiler:1.0.0-alpha01")
    implementation("com.google.dagger:hilt-android:2.28-alpha")
    kapt("com.google.dagger:hilt-android-compiler:2.28-alpha")


    debugImplementation("com.amitshekhar.android:debug-db:1.0.6")
}