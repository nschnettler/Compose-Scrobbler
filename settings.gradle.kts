import de.fayard.refreshVersions.bootstrapRefreshVersions

include(":common")
include(":repo")
include(":lastfm")
include(":database")
include(":scrobble")
include(":app")
rootProject.name = "Scrobbler"

pluginManagement {
    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
        gradlePluginPortal()
        jcenter()
        google()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "dagger.hilt.android.plugin" ->
                    useModule("com.google.dagger:hilt-android-gradle-plugin:2.28-alpha:${requested.version}")
            }
        }
    }
}

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
}

bootstrapRefreshVersions()