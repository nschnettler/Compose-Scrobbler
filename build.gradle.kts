plugins {
    id("com.autonomousapps.dependency-analysis") version "0.56.0"
}

buildscript {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap/") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha07")
        classpath(kotlin("gradle-plugin", version = "1.4.0-rc"))
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.28-alpha")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap/") }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.FlowPreview"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}