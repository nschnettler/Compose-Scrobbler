plugins {
    id("com.android.library")
    kotlin("android")
    id("net.saliman.properties")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
    }

    val lastFmApiKey: String by project
    val lastFmSecret: String by project
    val spotifyClientId: String by project
    val spotifyClientSecret: String by project

    buildTypes {
        buildTypes.forEach {
            it.buildConfigField("String", "LASTFM_API_KEY", lastFmApiKey)
            it.buildConfigField("String", "LASTFM_SECRET", lastFmSecret)
            it.buildConfigField("String", "SPOTIFY_CLIENT", spotifyClientId)
            it.buildConfigField("String", "SPOTIFY_SECRET", spotifyClientSecret)
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(Kotlin.stdlib.jdk7)
    api(JakeWharton.timber)
}