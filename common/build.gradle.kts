plugins {
    id("java-library")
    kotlin("jvm")
}
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Kotlin.stdlib.jdk7)

    //Timber
    api(JakeWharton.timber)
}