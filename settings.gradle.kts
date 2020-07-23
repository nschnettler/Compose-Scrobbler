import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

include(":navigation")
include(":common")
include(":repo")
include(":lastfm")
include(":database")
include(":scrobble")
include(":app")
rootProject.name = "Scrobbler"

buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard:dependencies:0.5.7")
}

bootstrapRefreshVersionsAndDependencies()