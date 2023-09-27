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
import boris.sumishisen.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
	private lateinit var binding : ActivityMainBinding
	private lateinit var notificationManager : NotificationManager
	private lateinit var accessibilityManager : AccessibilityManager
	private var isOverlayFinish = false
	private var isNotificationFinish = false
	private var isAccessibilityFinish = false
	private val requestOverlayLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
		isOverlayFinish = true
	}
	private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
		isNotificationFinish = true
	}
	private val requestAccessibilityLauncher = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()) {
		isAccessibilityFinish = true
	}
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
		
		if (!Settings.canDrawOverlays(this)) {
			Toast.makeText(this, "Please turn on 'Display over other apps'", Toast.LENGTH_LONG).show()
			Thread {
				Thread.sleep(2000)
				requestOverlayLauncher.launch(
					Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", packageName, null)))
			}.start()
		}
		else {
			isOverlayFinish = true
		}
	}
	
	override fun onResume() {
		super.onResume()
		if (isOverlayFinish) {
			if (!Settings.canDrawOverlays(this)) {
				startActivity(
					Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", packageName, null)))
			}
			else if (!notificationManager.areNotificationsEnabled()) {
				Toast.makeText(this, "Please turn on 'Notification'", Toast.LENGTH_LONG).show()
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					if (isNotificationFinish) {
						startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(
							"android.provider.extra.APP_PACKAGE", packageName))
					}
					else {
						requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
					}
				}
				else {
					Thread {
						Thread.sleep(1000)
						startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(
							"android.provider.extra.APP_PACKAGE", packageName))
					}.start()
				}
			}
			else if (!isAccessibilityServiceEnabled()) {
				if (isAccessibilityFinish) {
					Toast.makeText(this, "Enable accessibility service for auto click feature", Toast.LENGTH_LONG)
						.show()
					startForegroundService(Intent(this@MainActivity, SumiWindow::class.java))
					finish()
				}
				else {
					requestAccessibilityLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
				}
			}
			else {
				startForegroundService(Intent(this@MainActivity, SumiWindow::class.java))
				finish()
			}
		}
	}
	
	private fun isAccessibilityServiceEnabled() : Boolean {
		val serviceList = accessibilityManager.getEnabledAccessibilityServiceList(
			AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
		for (serviceInfo in serviceList) {
			if (serviceInfo.packageNames != null && serviceInfo.packageNames[0] == packageName) return true
		}
		return false
	}
}