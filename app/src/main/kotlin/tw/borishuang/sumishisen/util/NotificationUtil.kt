package tw.borishuang.sumishisen.util

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import tw.borishuang.sumishisen.R
import tw.borishuang.sumishisen.service.WindowService
import tw.borishuang.sumishisen.service.WindowService.Companion.ACTION_CLOSE

/**
 * The utility of notification.
 */
object NotificationUtil {
    private const val CHANNEL_ID = "SumiShisen"

    /**
     * Setup the notification.
     */
    fun setupBuilder(context: Context): NotificationCompat.Builder {
        setChannel(context)

        val intent = Intent(context.applicationContext, WindowService::class.java).setAction(ACTION_CLOSE)
        val pendingIntent = PendingIntent.getService(context.applicationContext, 1, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .addAction(R.drawable.sumikko_cat, "Close", pendingIntent)
            .setSmallIcon(R.drawable.sumikko_cat)
            .setContentText("SumiShisen is running")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
    }

    /**
     * Create a notification channel.
     */
    private fun setChannel(context: Context) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT).apply {
            setShowBadge(false)
        }

        notificationManager.createNotificationChannel(channel)
    }
}