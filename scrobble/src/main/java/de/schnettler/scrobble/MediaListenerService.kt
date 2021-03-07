package de.schnettler.scrobble

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.os.IBinder
import android.service.notification.NotificationListenerService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.repo.preferences.PreferenceEntry
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MediaListenerService : NotificationListenerService(),
    MediaSessionManager.OnActiveSessionsChangedListener,
    LifecycleOwner {
    private val dispatcher = ServiceLifecycleDispatcher(this)

    private var controllers: List<MediaController>? = null
    private val controllersMap: HashMap<MediaSession.Token, Pair<MediaController, MediaController.Callback>> =
        hashMapOf()
    @Inject
    lateinit var tracker: PlayBackTracker
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate() {
        dispatcher.onServicePreSuperOnCreate()
        super.onCreate()

        lifecycleScope.launch {
            dataStoreManager.getPreferenceFlow(PreferenceEntry.ScrobbleSources).collect { sources ->
                onSourcesChanged(sources)
            }
        }
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

        lifecycleScope.launch {
            val allowedControllers = dataStoreManager.getPreference(PreferenceEntry.ScrobbleSources)
            controllers?.forEach { controller ->
                if (allowedControllers.contains(controller.packageName)) {
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

    private fun onSourcesChanged(enabledSources: Set<String>) {
        controllersMap.filter {
            !enabledSources.contains(it.value.first.packageName)
        }.forEach { token, (controller, callback) ->
            controller.unregisterCallback(callback)
            controllersMap.remove(token)
        }
        onActiveSessionsChanged(controllers)
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