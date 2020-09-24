# Scrobble (WIP, name not final)

Scrobble is a wip music tracking and browsing app. It uses the [Lastf.fm](https://www.last.fm) API 
to realize music tracking and browsing and the [Spotify](https://www.spotify.com) API for additional
artist artwork.

This app is very much work in progress and still in heavy development. Im building this app to learn Android 
development using best practises and also cutting edge technologies.

<img src="https://raw.githubusercontent.com/Sh4dowSoul/Compose-Scrobbler/main/art/profile.png" width=25% height=25%> <img src="https://raw.githubusercontent.com/Sh4dowSoul/Compose-Scrobbler/main/art/artist.png" width=25% height=25%> <img src="https://raw.githubusercontent.com/Sh4dowSoul/Compose-Scrobbler/main/art/settings.png" width=25% height=25%>

## Development Setup
The project requires a version of Android Studio which supports Jetpack Compose. Currently it is supported by Android Studio 4.2 Canary.

### API Keys
The app uses various APIs to deliver dynamic data. You need to supply your own API keys for [Last.fm](https://www.last.fm/api/account/create) and [Spotify](https://developer.spotify.com/dashboard/applications).

After you obtained the api keys you can provide them to the app by putting the following in the
`gradle.properties` file in your user home:

```
# Get these from Last.fm
lastfmKey = <insert>
lastFmSecret = <insert>

# Get this from Spotify
spotifyAuth = <insert>
```

Linux/Mac: `~/.gradle/gradle.properties`
Windows: `C:\Users\USERNAME\.gradle`

##  Architecture

• Dependency Injection: [Dagger Hilt](https://dagger.dev/hilt/)
### app (User Interface)
• 1 Activity, no Fragments

• UI Toolkit: [Jetpack Compose](https://developer.android.com/jetpack/compose)

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
