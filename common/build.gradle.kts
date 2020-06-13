plugins {
    id("java-library")
    kotlin("jvm")
}
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")

    //Timber
    api("com.jakewharton.timber:timber:4.7.1")
}