plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("room.incremental", true)
            }
        }
    }

    sourceSets {
        getByName("androidTest").assets.srcDirs("$projectDir/schemas")
    }

    testOptions.unitTests {
        isIncludeAndroidResources = true
        isReturnDefaultValues = true
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
}

dependencies {
    // Modules
    implementation(project(":common"))

    // Room
    implementation(AndroidX.room.ktx)
    implementation(AndroidX.room.runtime)
    kapt(AndroidX.room.compiler)
    implementation("com.github.MatrixDev.Roomigrant:RoomigrantLib:0.2.0")
    kapt("com.github.MatrixDev.Roomigrant:RoomigrantCompiler:0.2.0")

    // Hilt
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)

    // Test
    androidTestImplementation(Testing.junit4)
    androidTestImplementation(KotlinX.coroutines.test)
    androidTestImplementation(AndroidX.test.ext.junitKtx)
    androidTestImplementation(AndroidX.archCore.testing)
    androidTestImplementation(AndroidX.test.rules)
    androidTestImplementation(AndroidX.room.testing)
    androidTestImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
}