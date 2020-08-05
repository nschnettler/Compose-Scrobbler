# Scrobble (WIP, name not final)

Scrobble is a wip music tracking and browsing app. It uses the [Lastf.fm](https://www.last.fm) API 
to realize music tracking and browsing and the [Spotify](https://www.spotify.com) API for additional
artist artwork.

This app is very much work in progress and still in heavy development. Im building this app to learn Android 
development using best practises and also cutting edge technologies.


##  Architecture

• Dependency Injection: [Dagger Hilt](https://dagger.dev/hilt/)
### app (User Interface)
• 1 Activity, no Fragments

• UI Toolkit: [Jetpack Compose](https://developer.android.com/jetpack/compose)

• Navigation: [Compose Router](https://github.com/zsoltk/compose-router)

• Image Loading: [Coil](https://github.com/coil-kt/coil)

• Viewmodel: [Jetpack Viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel)

### database (Local Data Source)
• SQLite abstraction: [Jetpack Room](https://developer.android.com/topic/libraries/architecture/room)

### lastfm (Remote Data Source)
• HTTP Client: [Retrofit](https://square.github.io/retrofit/)

• Json Parser: [Moshi](https://github.com/square/moshi)

### repo (Repository)
• Data loading & cashing: [Store](https://github.com/dropbox/Store)

• Task schedule and execution: [Jetpack WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
