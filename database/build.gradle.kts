plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.incremental"] = "true"
            }
        }
    }

    testOptions.unitTests {
        isIncludeAndroidResources = true
        isReturnDefaultValues = true
    }

    packagingOptions {
        exclude("**/attach_hotspot_windows.dll")
        exclude("META-INF/licenses/**")
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(Kotlin.stdlib.jdk8)
    implementation(AndroidX.room.ktx)
    api(AndroidX.room.runtime)
    kapt(AndroidX.room.compiler)

    androidTestImplementation(Testing.junit4)
    androidTestImplementation(Testing.mockito.core)
    androidTestImplementation(KotlinX.coroutines.test)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.archCore.testing)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation("com.linkedin.dexmaker", "dexmaker-mockito", "_")
    androidTestImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
}