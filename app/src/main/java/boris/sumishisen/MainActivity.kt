package boris.sumishisen

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import boris.sumishisen.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
	private lateinit var binding : ActivityMainBinding
	private var isFirst = false
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		if (!Settings.canDrawOverlays(this)) {
			AlertDialog.Builder(this).setTitle("Permission Required")
				.setMessage("Please turn on 'Display over other apps' for this app")
				.setPositiveButton("Setting") { _, _ ->
					isFirst = true
					startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
				}.show()
		}
		else {
			isFirst = true
		}
	}
	
	override fun onResume() {
		super.onResume()
		if (isFirst) {
			if (!Settings.canDrawOverlays(this)) {
				startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
			}
			else {
				startService(Intent(this@MainActivity, SumiWindow::class.java))
				finish()
			}
		}
	}
}