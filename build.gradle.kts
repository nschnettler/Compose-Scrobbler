plugins {
    id("com.autonomousapps.dependency-analysis") version "0.56.0"
    id("com.android.application") version "4.2.0-alpha08" apply false
    id("dagger.hilt.android.plugin") version "2.28-alpha" apply false
    kotlin("android") version "1.4.0" apply false
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.FlowPreview"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}