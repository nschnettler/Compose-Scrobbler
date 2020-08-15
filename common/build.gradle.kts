plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }

    val lastfmKey: String? by project
    val lastFmSecret: String? by project
    val spotifyAuth: String? by project

    buildTypes {
        buildTypes.forEach {
            it.buildConfigField("String", "LASTFM_API_KEY", lastfmKey ?: "\"\"")
            it.buildConfigField("String", "LASTFM_SECRET", lastFmSecret ?: "\"\"")
            it.buildConfigField("String", "SPOTIFY_AUTH", spotifyAuth ?: "\"\"")
        }
    }
}

dependencies {
    api(JakeWharton.timber)
}