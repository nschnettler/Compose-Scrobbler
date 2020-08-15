import de.fayard.refreshVersions.bootstrapRefreshVersions

include(":common")
include(":repo")
include(":lastfm")
include(":database")
include(":scrobble")
include(":app")
rootProject.name = "Scrobbler"

buildscript {
    repositories {
        gradlePluginPortal()
        maven(url = "https://dl.bintray.com/jmfayard/maven")
    }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.5-dev-009")
}

bootstrapRefreshVersions()