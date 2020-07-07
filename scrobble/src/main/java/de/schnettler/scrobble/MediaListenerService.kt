package de.schnettler.scrobble

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.repo.ServiceCoroutineScope
import kotlinx.coroutines.cancel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MediaListenerService: NotificationListenerService(), MediaSessionManager.OnActiveSessionsChangedListener {
    private var controllers: List<MediaController>? = null
    private val controllersMap: HashMap<MediaSession.Token, Pair<MediaController, MediaController.Callback>> = hashMapOf()
    @Inject lateinit var tracker: PlayBackTracker
    @Inject lateinit var scope: ServiceCoroutineScope

    private val allowedControllers = listOf("com.google.android.apps.youtube.music")

    companion object {
        fun isEnabled(context: Context) = NotificationManagerCompat
                .getEnabledListenerPackages(context)
                .contains(context.packageName)
    }

    override fun onCreate() {
        super.onCreate()
        val manager: MediaSessionManager = application.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val componentName = ComponentName(this, this.javaClass)
        manager.addOnActiveSessionsChangedListener(this, componentName)
        Timber.i("Media Listener started")
        onActiveSessionsChanged(manager.getActiveSessions(componentName))
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onActiveSessionsChanged(activeControllers: MutableList<MediaController>?) {
        //Timber.d("Active Sessions changed ")
        controllers = activeControllers
        val tokens = hashSetOf<MediaSession.Token>()
        val packageNames = hashSetOf<String>()
        controllers?.forEach {controller ->
            if (allowedControllers.contains(controller.packageName)) {
                tokens.add(controller.sessionToken)
                packageNames.add(controller.packageName)
                //New Session
                if (!controllersMap.contains(controller.sessionToken)) {
                    Timber.d("onActiveSessionsChanged [${controllers?.size}] + ${controller.packageName}")
                    val callback = MediaControllerCallback(controller, tracker)
                    controller.registerCallback(callback)
                    val pair = controller to callback
                    synchronized(controllersMap) {
                        controllersMap.put(controller.sessionToken, pair)
                    }

                    //
                    controller.playbackState?.let { state ->
                        tracker.onStateChanged(controller = controller, state = state)
                    }
                    controller.metadata?.let { metadata ->
                        tracker.onMetadataChanged(controller = controller, metadata = metadata)
                    }
                }
            }
        }
        removeSessions(tokens)
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
            Timber.d("onActiveSessionsChanged [${controllers?.size}] - ${controllersMap[it]?.first?.packageName}")
            controllersMap.remove(it)
        }
    }
}