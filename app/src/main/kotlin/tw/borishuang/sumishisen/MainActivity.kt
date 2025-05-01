package tw.borishuang.sumishisen

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import tw.borishuang.sumishisen.databinding.ActivityMainBinding
import tw.borishuang.sumishisen.service.WindowService
import tw.borishuang.sumishisen.util.PermissionUtil

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        if (checkOverlay() && checkNotification() && checkAccessibility()) {
            startForegroundService(Intent(this, WindowService::class.java))
            finishAndRemoveTask()
        }
    }

    private fun checkOverlay(): Boolean {
        if (PermissionUtil.isOverlayEnabled(this)) {
            isOverlayFinished = true
            return true
        }
        else if (!isOverlayFinished) {
            requestOverlayLauncher.launch(PermissionUtil.getOverlayIntent(this))
        }
        else {
            startActivity(PermissionUtil.getOverlayIntent(this))
        }
        return false
    }

    private fun checkNotification(): Boolean {
        if (PermissionUtil.isNotificationEnabled(this)) {
            isNotificationFinished = true
            return true
        }
        else if (!isNotificationFinished) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else {
                startActivity(PermissionUtil.getNotificationIntent(this))
            }
        }
        else {
            startActivity(PermissionUtil.getNotificationIntent(this))
        }
        return false
    }

    private fun checkAccessibility(): Boolean {
        if (PermissionUtil.isAccessibilityServiceEnabled(this)) {
            isAccessibilityFinished = true
            return true
        }
        else if (!isAccessibilityFinished) {
            requestAccessibilityLauncher.launch(PermissionUtil.getAccessibilityIntent())
        }
        else {
            return true
        }
        return false
    }
}