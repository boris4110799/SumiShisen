package boris.sumishisen

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import boris.sumishisen.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
	private lateinit var binding : ActivityMainBinding
	private lateinit var notificationManager : NotificationManager
	private var isFirst = false
	private var isLaunch = false
	private val requestResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
		isFirst = true
	}
	private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
		isLaunch = true
	}
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		
		if (!Settings.canDrawOverlays(this)) {
			Toast.makeText(this, "Please turn on 'Display over other apps'", Toast.LENGTH_LONG)
				.show()
			Thread {
				Thread.sleep(2000)
				requestResultLauncher.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", packageName, null)))
			}.start()
		}
		else {
			isFirst = true
		}
	}
	
	override fun onResume() {
		super.onResume()
		if (isFirst) {
			if (!Settings.canDrawOverlays(this)) {
				startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", packageName, null)))
			}
			else if (!notificationManager.areNotificationsEnabled()) {
				Toast.makeText(this, "Please turn on 'Notification'", Toast.LENGTH_LONG).show()
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					if (isLaunch) {
						startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra("android.provider.extra.APP_PACKAGE", packageName))
					}
					else {
						requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
					}
				}
				else {
					Thread {
						Thread.sleep(2000)
						startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra("android.provider.extra.APP_PACKAGE", packageName))
					}.start()
				}
			}
			else {
				startForegroundService(Intent(this@MainActivity, SumiWindow::class.java))
				finish()
			}
		}
	}
}