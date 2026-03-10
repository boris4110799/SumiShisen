package tw.borishuang.sumishisen.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

/**
 * The utility of permission.
 */
object PermissionUtil {
    /**
     * Check overlay permission is enabled or not.
     */
    fun isOverlayEnabled(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * Check notification permission is enabled or not.
     */
    fun isNotificationEnabled(context: Context): Boolean {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        return notificationManager.areNotificationsEnabled()
    }

    /**
     * Check accessibility service is enabled or not.
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(AccessibilityManager::class.java)
        val serviceList = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

        for (serviceInfo in serviceList) {
            if (serviceInfo.packageNames != null && serviceInfo.packageNames.contains(context.packageName)) return true
        }
        return false
    }

    fun getOverlayIntent(context: Context) =
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", context.packageName, null))

    fun getNotificationIntent(context: Context) =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)

    fun getAccessibilityIntent() = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
}