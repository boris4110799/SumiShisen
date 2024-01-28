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
		const val ACTION_CLICK = "boris.sumishisen.minigames"
		const val ACTION_COOK_EGGS = "boris.sumishisen.cook_eggs"
	}

	private lateinit var windowManager: WindowManager
	private var realWidth: Int = 0
	private var realHeight: Int = 0
	private lateinit var receiver: BroadcastReceiver

	/**
	 * The queue for minigames
	 */
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
					when (intent.action) {
						ACTION_CLICK     -> {
							val x = intent.getIntExtra("x", 1)
							val y = intent.getIntExtra("y", 1)
							clickQueue.add(Pair(x, y))
							if (!isThreadStart) {
								isThreadStart = true
								startClickMinigames()
							}
						}

						ACTION_COOK_EGGS -> {
							if (!isThreadStart) {
								isThreadStart = true
								startCookEggs()
							}
						}
					}
				}
			}
		}
		val intentFilter = IntentFilter().apply {
			addAction(ACTION_CLICK)
			addAction(ACTION_COOK_EGGS)
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			registerReceiver(receiver, intentFilter, RECEIVER_NOT_EXPORTED)
		}
		else {
			registerReceiver(receiver, intentFilter)
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

	/**
	 * Click minigames with queue
	 */
	private fun startClickMinigames() {
		Thread {
			while (clickQueue.isNotEmpty()) {
				sleep(300)
				val (x, y) = clickQueue.removeFirst()
				clickMinigames(x, y)
			}
			isThreadStart = false
		}.start()
	}

	/**
	 * Perform click gesture at minigames
	 */
	private fun clickMinigames(x: Int, y: Int) {
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

	/**
	 * Start cooking eggs
	 */
	private fun startCookEggs() {
		Thread {
			cookEggs()
			sleep(6500)
			collectEggs()
			isThreadStart = false
		}.start()
	}

	/**
	 * Perform the gesture of cooking eggs
	 */
	private fun cookEggs() {
		val cookPath = Path()
		val eggX = realWidth*0.5f
		val eggY = realHeight*(920/1080f)
		val startX = realWidth*(500/2340f)
		val startY = realHeight*(480/1080f)
		val endX = realWidth*(1870/2340f)
		val endY = realHeight*(480/1080f)
		cookPath.moveTo(eggX, eggY)
		cookPath.lineTo(startX, startY)
		cookPath.lineTo(endX, endY)
		val gestureDescription = GestureDescription.Builder()
			.addStroke(GestureDescription.StrokeDescription(cookPath, 0, 1000L))
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

	/**
	 * Perform the gesture of collecting the cooked eggs
	 */
	private fun collectEggs() {
		val collectPath = Path()
		val startX = realWidth*(500/2340f)
		val startY = realHeight*(480/1080f)
		val endX = realWidth*(1870/2340f)
		val endY = realHeight*(480/1080f)
		collectPath.moveTo(startX, startY)
		collectPath.lineTo(endX, endY)
		val gestureDescription = GestureDescription.Builder()
			.addStroke(GestureDescription.StrokeDescription(collectPath, 0L, 500L))
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