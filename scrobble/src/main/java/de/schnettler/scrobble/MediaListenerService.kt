package de.schnettler.scrobble

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.media.session.MediaController
import android.media.session.MediaController.Callback
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.repo.preferences.PreferenceConstants
import de.schnettler.repo.util.defaultSharedPrefs
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MediaListenerService : LifecycleNotificationListenerService(), OnActiveSessionsChangedListener,
    OnSharedPreferenceChangeListener {

    private val activeSessions: HashMap<MediaSession.Token, Pair<MediaController, Callback>> = hashMapOf()
    private var allowedPackages = emptySet<String>()

    private lateinit var manager: MediaSessionManager
    private lateinit var componentName: ComponentName

    @Inject
    lateinit var tracker: PlaybackTracker
    private lateinit var prefs: SharedPreferences

    companion object {
        fun isEnabled(context: Context) = NotificationManagerCompat
            .getEnabledListenerPackages(context)
            .contains(context.packageName)
    }

    override fun onCreate() {
        super.onCreate()

        // 1. Register Shared Preferences Listener
        prefs = application.defaultSharedPrefs()
        prefs.registerOnSharedPreferenceChangeListener(this)

        // 2. Register ActiveSessionChanged Listener
        manager = application.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        componentName = ComponentName(this, this.javaClass)
        manager.addOnActiveSessionsChangedListener(this, componentName)

        // 3. Get allowed Packages
        allowedPackages = getAllowedControllerPackages()

        // 4. Get currently active Sessions
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
        }.forEach { token, (controller, callback) ->
            Timber.d("[Removed Session] - ${controller.packageName}")
            controller.unregisterCallback(callback)
            activeSessions.remove(token)
        }

        /** 2. Register Callbacks for new Controllers
         * Add Controllers from allowedControllers which are not yet in activeSessions
         */
        allowedControllers.filter { !activeSessions.contains(it.sessionToken) }.forEach { controller ->
            Timber.d("[Added Session] - ${controller.packageName}")
            val callback = MediaControllerCallback(controller, tracker)
            controller.registerCallback(callback)
            activeSessions[controller.sessionToken] = controller to callback

            // 2.1 Supply initial information
            callback.apply {
                onMetadataChanged(controller.metadata)
                onPlaybackStateChanged(controller.playbackState)
            }
        }
    }

    private fun getAllowedControllerPackages() =
        prefs.getStringSet(PreferenceConstants.SCROBBLE_SOURCES_KEY, emptySet()) ?: emptySet()

    // TODO: Check if comparison with old value is necessary
    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        if (key == PreferenceConstants.SCROBBLE_SOURCES_KEY) {
            val newAllowedPackages = getAllowedControllerPackages()
            if (newAllowedPackages != allowedPackages) {
                allowedPackages = newAllowedPackages
                onActiveSessionsChanged(manager.getActiveSessions(componentName))
            }
        }
    }
}