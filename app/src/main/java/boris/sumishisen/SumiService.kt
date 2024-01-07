package boris.sumishisen

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.*
import android.graphics.Path
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import java.lang.Thread.sleep

class SumiService : AccessibilityService() {
	companion object {
		const val ACTION_CLICK = "boris.sumishisen.click"
	}

	private lateinit var windowManager: WindowManager
	private var realWidth: Int = 0
	private var realHeight: Int = 0
	private lateinit var receiver: BroadcastReceiver

	private val clickQueue = mutableListOf<Pair<Int, Int>>()
	private var isThreadStart = false

	@SuppressLint("UnspecifiedRegisterReceiverFlag")
	override fun onCreate() {
		super.onCreate()
		windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
		setScreenSize()

		receiver = object : BroadcastReceiver() {
			override fun onReceive(context: Context?, intent: Intent?) {
				if (intent != null) {
					val x = intent.getIntExtra("x", 1)
					val y = intent.getIntExtra("y", 1)
					clickQueue.add(Pair(x, y))
					if (!isThreadStart) {
						isThreadStart = true
						startClick()
					}
				}
			}
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			registerReceiver(receiver, IntentFilter(ACTION_CLICK), RECEIVER_NOT_EXPORTED)
		}
		else {
			registerReceiver(receiver, IntentFilter(ACTION_CLICK))
		}
	}

	override fun onAccessibilityEvent(event: AccessibilityEvent?) {
		//Nothing need to retrieve
	}

	override fun onInterrupt() {
		//onInterrupt
	}

	override fun onUnbind(intent: Intent?): Boolean {
		unregisterReceiver(receiver)
		return super.onUnbind(intent)
	}

	/**
	 * Retrieve the actual screen size for auto click feature
	 */
	private fun setScreenSize() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			realWidth = windowManager.maximumWindowMetrics.bounds.width()
			realHeight = windowManager.maximumWindowMetrics.bounds.height()
		}
		else {
			val displayMetrics = DisplayMetrics()
			windowManager.defaultDisplay?.getRealMetrics(displayMetrics)
			realWidth = displayMetrics.widthPixels
			realHeight = displayMetrics.heightPixels
		}
		if (realWidth < realHeight) realWidth = realHeight.also { realHeight = realWidth }
	}

	private fun startClick() {
		Thread {
			while (clickQueue.isNotEmpty()) {
				sleep(300)
				val (x, y) = clickQueue.removeFirst()
				click(x, y)
			}
			isThreadStart = false
		}.start()
	}

	/**
	 * Perform click gesture
	 */
	private fun click(x: Int, y: Int) {
		val path = Path()
		var px = realWidth*(300/2340f)
		var py = realHeight*(145/1080f)
		px += realWidth*(1452/2340f)/22*(2*y-1)
		py += realHeight*(790/1080f)/12*(2*x-1)
		path.moveTo(px, py)
		val gestureDescription = GestureDescription.Builder()
			.addStroke(GestureDescription.StrokeDescription(path, 0, 100L))
			.build()
		dispatchGesture(gestureDescription, object : GestureResultCallback() {
			override fun onCompleted(gestureDescription: GestureDescription?) {
				super.onCompleted(gestureDescription)
			}

			override fun onCancelled(gestureDescription: GestureDescription?) {
				super.onCancelled(gestureDescription)
			}
		}, null)
	}
}