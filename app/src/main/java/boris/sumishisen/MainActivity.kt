package boris.sumishisen

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import boris.sumishisen.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
	private lateinit var binding: ActivityMainBinding
	private lateinit var notificationManager: NotificationManager
	private lateinit var accessibilityManager: AccessibilityManager
	private var isOverlayFinished = false
	private var isNotificationFinished = false
	private var isAccessibilityFinished = false
	private val requestOverlayLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
		isOverlayFinished = true
	}
	private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
		isNotificationFinished = true
	}
	private val requestAccessibilityLauncher = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()) {
		isAccessibilityFinished = true
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

		if (!Settings.canDrawOverlays(this)) {
			lifecycleScope.launch {
				Snackbar.make(binding.root, "Please enable 'Display over other apps'", Snackbar.LENGTH_LONG).show()
				delay(2000)
				requestOverlayLauncher.launch(
					Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", packageName, null)))
			}
		}
		else {
			isOverlayFinished = true
		}
	}

	override fun onResume() {
		super.onResume()
		if (isOverlayFinished) {
			if (!Settings.canDrawOverlays(this)) {
				startActivity(
					Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", packageName, null)))
			}
			else if (!notificationManager.areNotificationsEnabled()) {
				Snackbar.make(binding.root, "Please enable 'Notification'", Snackbar.LENGTH_LONG).show()
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					if (isNotificationFinished) {
						lifecycleScope.launch {
							delay(2000)
							startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(
								"android.provider.extra.APP_PACKAGE", packageName))
						}
					}
					else {
						requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
					}
				}
				else {
					lifecycleScope.launch {
						delay(2000)
						startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(
							"android.provider.extra.APP_PACKAGE", packageName))
					}
				}
			}
			else if (!isAccessibilityServiceEnabled()) {
				if (isAccessibilityFinished) {
					Toast.makeText(this, "Enable accessibility service for auto click feature", Toast.LENGTH_LONG)
						.show()
					startForegroundService(Intent(this@MainActivity, SumiWindow::class.java))
					finishAndRemoveTask()
				}
				else {
					requestAccessibilityLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
				}
			}
			else {
				startForegroundService(Intent(this@MainActivity, SumiWindow::class.java))
				finishAndRemoveTask()
			}
		}
	}

	/**
	 * Check accessibility service is enabled or not
	 */
	private fun isAccessibilityServiceEnabled(): Boolean {
		val serviceList = accessibilityManager.getEnabledAccessibilityServiceList(
			AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
		for (serviceInfo in serviceList) {
			if (serviceInfo.packageNames != null && serviceInfo.packageNames[0] == packageName) return true
		}
		return false
	}
}