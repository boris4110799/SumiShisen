package tw.borishuang.sumishisen.service

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.view.ContextThemeWrapper
import androidx.lifecycle.LifecycleService
import tw.borishuang.sumishisen.R
import tw.borishuang.sumishisen.manager.BroadcastManager
import tw.borishuang.sumishisen.navigation.NavManager
import tw.borishuang.sumishisen.util.NotificationUtil

/**
 * The Service that handles the window view.
 */
class WindowService : LifecycleService() {
    companion object {
        const val ACTION_CLOSE = "WindowService.Close"
        const val ACTION_STOP = "WindowService.Stop"
    }

    private lateinit var navManager: NavManager
    private lateinit var broadcastManager: BroadcastManager

    override fun onCreate() {
        super.onCreate()

        navManager = NavManager(ContextThemeWrapper(this, R.style.AppTheme))
        navManager.init()

        broadcastManager = object : BroadcastManager() {
            override fun handleIntent(intent: Intent) {
                when (intent.action) {
                    ACTION_STOP -> stopSelf()
                }
            }
        }
        broadcastManager.register(this, listOf(ACTION_STOP))

        setNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action

            if (action != null) {
                when (action) {
                    // When user clicked the 'Close' button in notification, stop the service
                    ACTION_CLOSE -> stopSelf()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        navManager.destroy()
        broadcastManager.unregister(this)
        super.onDestroy()
    }

    /**
     * Set the notification for the service.
     */
    private fun setNotification() {
        val notification = NotificationUtil.createNotification(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        }
        else {
            startForeground(1, notification)
        }
    }
}