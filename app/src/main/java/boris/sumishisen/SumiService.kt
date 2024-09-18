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
import kotlinx.coroutines.*
import java.lang.Thread.sleep

class SumiService : AccessibilityService() {
	companion object {
		const val ACTION_CLICK = "boris.sumishisen.minigames"
		const val ACTION_COOK_EGGS = "boris.sumishisen.cook_eggs"
		const val ACTION_COOK_FRIES = "boris.sumishisen.cook_fries"
	}

	private lateinit var windowManager: WindowManager
	private lateinit var receiver: BroadcastReceiver
	private var realWidth = 0
	private var realHeight = 0

	/**
	 * The queue for minigames
	 */
	private val clickQueue = mutableListOf<Pair<Int, Int>>()
	private var isThreadStart = false

	private val panStartX
		get() = realWidth*(500/2340f)
	private val panEndX
		get() = realWidth*(1870/2340f)
	private val panY
		get() = realHeight*(480/1080f)

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

						ACTION_COOK_EGGS, ACTION_COOK_FRIES -> {
							if (!isThreadStart) {
								isThreadStart = true
								startCook(intent.action!!)
							}
						}
					}
				}
			}
		}
		val intentFilter = IntentFilter().apply {
			addAction(ACTION_CLICK)
			addAction(ACTION_COOK_EGGS)
			addAction(ACTION_COOK_FRIES)
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
		CoroutineScope(Dispatchers.Default).launch {
			while (clickQueue.isNotEmpty()) {
				sleep(300)
				val (x, y) = clickQueue.removeFirst()
				clickMinigames(x, y)
			}
			isThreadStart = false
		}
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
		performGesture(path, 100)
	}

	/**
	 * Start cooking
	 */
	private fun startCook(action: String) {
		CoroutineScope(Dispatchers.Default).launch {
			when (action) {
				ACTION_COOK_EGGS -> {
					cookEggs()
					sleep(6500)
				}
				ACTION_COOK_FRIES -> {
					cookFries()
					sleep(5500)
				}
			}
			collect()
			isThreadStart = false
		}
	}

	/**
	 * Perform the gesture of cooking eggs
	 */
	private fun cookEggs() {
		val cookPath = Path()
		val eggX = realWidth*(1100/2340f)
		val eggY = realHeight*(920/1080f)
		cookPath.moveTo(eggX, eggY)
		cookPath.lineTo(panStartX, panY)
		cookPath.lineTo(panEndX, panY)
		performGesture(cookPath, 1000)
	}

	/**
	 * Perform the gesture of cooking fries
	 */
	private fun cookFries() {
		val cookPath = Path()
		val friesX = realWidth*(1242/2340f)
		val friesY = realHeight*(920/1080f)
		cookPath.moveTo(friesX, friesY)
		cookPath.lineTo(panStartX, panY)
		cookPath.lineTo(panEndX, panY)
		performGesture(cookPath, 1000)
	}

	/**
	 * Perform the gesture of collecting the cooked food
	 */
	private fun collect() {
		val collectPath = Path()
		collectPath.moveTo(panStartX, panY)
		collectPath.lineTo(panEndX, panY)
		performGesture(collectPath, 500)
	}

	/**
	 * Perform the gesture
	 */
	private fun performGesture(path: Path, duration: Long) {
		val gestureDescription = GestureDescription.Builder()
			.addStroke(GestureDescription.StrokeDescription(path, 0, duration))
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