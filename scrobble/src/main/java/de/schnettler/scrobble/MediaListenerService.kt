package de.schnettler.scrobble

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.os.IBinder
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.repo.preferences.PreferenceConstants
import de.schnettler.repo.util.defaultSharedPrefs
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MediaListenerService : NotificationListenerService(),
    MediaSessionManager.OnActiveSessionsChangedListener,
    SharedPreferences.OnSharedPreferenceChangeListener,
    LifecycleOwner {
    private val dispatcher = ServiceLifecycleDispatcher(this)

    private var controllers: List<MediaController>? = null
    private val controllersMap: HashMap<MediaSession.Token, Pair<MediaController, MediaController.Callback>> =
        hashMapOf()
    @Inject lateinit var tracker: PlayBackTracker
    private lateinit var prefs: SharedPreferences

    companion object {
        fun isEnabled(context: Context) = NotificationManagerCompat
            .getEnabledListenerPackages(context)
            .contains(context.packageName)
    }

    override fun onCreate() {
        dispatcher.onServicePreSuperOnCreate()
        super.onCreate()
        prefs = application.defaultSharedPrefs()
        prefs.registerOnSharedPreferenceChangeListener(this)
        val manager: MediaSessionManager =
            application.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val componentName = ComponentName(this, this.javaClass)
        manager.addOnActiveSessionsChangedListener(this, componentName)
        Timber.i("Media Listener started")
        onActiveSessionsChanged(manager.getActiveSessions(componentName))
    }

    override fun onActiveSessionsChanged(activeControllers: List<MediaController>?) {
        controllers = activeControllers
        val tokens = hashSetOf<MediaSession.Token>()
        val packageNames = hashSetOf<String>()
        val allowedControllers = prefs.getStringSet(PreferenceConstants.SCROBBLE_SOURCES_KEY, emptySet())
        controllers?.forEach { controller ->
            if (allowedControllers?.contains(controller.packageName) == true) {
                tokens.add(controller.sessionToken)
                packageNames.add(controller.packageName)
                // New Session
                if (!controllersMap.contains(controller.sessionToken)) {
                    addNewSession(controller)
                }
            }
        }
        removeSessions(tokens)
    }

    private fun addNewSession(controller: MediaController) {
        Timber.d("onActiveSessionsChanged [Added] + ${controller.packageName}")
        val callback = MediaControllerCallback(controller, tracker)
        controller.registerCallback(callback)
        val pair = controller to callback
        synchronized(controllersMap) {
            controllersMap.put(controller.sessionToken, pair)
        }

        //
        controller.playbackState?.let { state ->
            tracker.onStateChanged(packageName = controller.packageName, state = state)
        }
        controller.metadata?.let { metadata ->
            tracker.onMetadataChanged(
                packageName = controller.packageName,
                metadata = metadata
            )
        }
    }

    private fun removeSessions(tokens: HashSet<MediaSession.Token>) {
        val toBeRemoved = mutableListOf<MediaSession.Token>()
        controllersMap.forEach { (token, value) ->
            if (!tokens.contains(token)) {
                // Not active anymore
                val controller = value.first
                controller.unregisterCallback(value.second)
                toBeRemoved.add(token)
            }
        }
        toBeRemoved.forEach {
            Timber.d("onActiveSessionsChanged [Removed] - ${controllersMap[it]?.first?.packageName}")
            controllersMap.remove(it)
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        if (key == PreferenceConstants.SCROBBLE_SOURCES_KEY) {
            val enabledPlayers = prefs?.getStringSet(PreferenceConstants.SCROBBLE_SOURCES_KEY, emptySet()) ?: emptySet()

            controllersMap.filter {
                !enabledPlayers.contains(it.value.first.packageName)
            }.forEach { token, (controller, callback) ->
                controller.unregisterCallback(callback)
                controllersMap.remove(token)
            }
            onActiveSessionsChanged(controllers)
        }
    }

    override fun getLifecycle(): Lifecycle {
        return dispatcher.lifecycle
    }

    override fun onBind(intent: Intent?): IBinder? {
        dispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    @Suppress("deprecation")
    override fun onStart(intent: Intent?, startId: Int) {
        dispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }
}