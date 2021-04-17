import de.fayard.refreshVersions.bootstrapRefreshVersions

include(":libraries:submission")


include(":features:history")


include(":libraries:persistence")


include(":features:search")
include(":features:settings")

include(":libraries:network:common")
include(":libraries:network:spotify")
include(":libraries:network:lastfm")
include(":libraries:model")
include(":libraries:core")
include(":libraries:compose")
include(":libraries:resources")

include(":ui:history")
include(":ui:detail")
include(":ui:profile")
include(":ui:common:util")
include(":ui:charts")
include(":ui:common:compose")
include(":common")
include(":repo")
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

bootstrapRefreshVersions(extraArtifactVersionKeyRules = listOf(file("versionRules.txt").readText()))