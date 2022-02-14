package de.schnettler.scrobble

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.scrobbler.persistence.PreferenceRequestStore
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MediaListenerService : LifecycleNotificationListenerService(), OnActiveSessionsChangedListener, LifecycleOwner {
    private val activeSessions: HashMap<MediaSession.Token, ScrobbleState> = hashMapOf()
    private var allowedPackages = emptySet<String>()

    private lateinit var manager: MediaSessionManager
    private lateinit var componentName: ComponentName

    @Inject
    lateinit var scrobbler: Scrobbler

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate() {
        super.onCreate()

        // 1. Register ActiveSessionChanged Listener
        manager = application.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        componentName = ComponentName(this, this.javaClass)
        manager.addOnActiveSessionsChangedListener(this, componentName)

        // 2. Listen for allowed Sources Preference
        lifecycleScope.launch {
            dataStoreManager.getPreferenceFlow(PreferenceRequestStore.scrobbleSources).collect { sources ->
                allowedPackages = sources
                Timber.d("[Allowed Apps] $sources")
                onActiveSessionsChanged(manager.getActiveSessions(componentName))
            }
        }

        // 3. Start with currently active Sessions
        onActiveSessionsChanged(manager.getActiveSessions(componentName))
    }

    override fun onActiveSessionsChanged(controllers: List<MediaController>?) {
        val allowedControllers = controllers?.filter {
            allowedPackages.contains(it.packageName)
        } ?: emptyList()

        /** 1. Unregister Callbacks of old Controllers
         * Remove sessions which are in activeSessions, but not valid anymore because..
         *      a) Not active anymore (not in controllers)
         *      b) Not allowed anymore (not in allowedPackages)
         */
        activeSessions.filterNot { (token, _) ->
            allowedControllers.map { it.sessionToken }.contains(token)
        }.forEach { (token, state) ->
            Timber.d("[Removed Session] - ${state.controller.packageName}")
            state.controller.unregisterCallback(state)
            activeSessions.remove(token)
        }

        /** 2. Register Callbacks for new Controllers
         * Add Controllers from allowedControllers which are not yet in activeSessions
         */
        allowedControllers.filter { !activeSessions.contains(it.sessionToken) }.forEach { controller ->
            Timber.d("[Added Session] - ${controller.packageName}")
            val scrobbleState = ScrobbleState(controller, scrobbler, lifecycleScope)
            controller.registerCallback(scrobbleState)
            activeSessions[controller.sessionToken] = scrobbleState

            // 2.1 Supply initial information
            scrobbleState.apply {
                onMetadataChanged(controller.metadata)
                onPlaybackStateChanged(controller.playbackState)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }
}