package tw.borishuang.sumishisen.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.NotificationManager
import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

/**
 * The utility of permission.
 */
object PermissionUtil {
    fun isOverlayEnabled(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun isNotificationEnabled(context: Context): Boolean {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        return notificationManager.areNotificationsEnabled()
    }

    /**
     * Check accessibility service is enabled or not
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(AccessibilityManager::class.java)
        val serviceList = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

        for (serviceInfo in serviceList) {
            if (serviceInfo.packageNames != null && serviceInfo.packageNames[0] == context.packageName) return true
        }
        return false
    }
}