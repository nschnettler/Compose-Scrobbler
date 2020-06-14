import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

include(":common")
include(":repo")
include(":lastfm")
include(":database")
include(":app")
rootProject.name = "Scrobbler"

buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard:dependencies:0.5.7")
}

bootstrapRefreshVersionsAndDependencies()