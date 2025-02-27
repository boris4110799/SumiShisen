package tw.borishuang.sumishisen.service

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
        const val ACTION_START = "boris.sumishisen.startgames"
        const val ACTION_STOP = "boris.sumishisen.stopgames"
        const val ACTION_SUMI = "boris.sumishisen.sumi"
        const val ACTION_COOK_EGGS = "boris.sumishisen.cook_eggs"
        const val ACTION_COOK_FRIES = "boris.sumishisen.cook_fries"
        const val ACTION_COOK_CROQUETTES = "boris.sumishisen.cook_croquettes"
        const val TOTAL_FOOD_COUNT = 3
    }

    private lateinit var windowManager: WindowManager
    private lateinit var receiver: BroadcastReceiver
    private lateinit var foodPositionList: List<Float>
    private var realWidth = 0
    private var realHeight = 0

    /** Is currently cooking. */
    private var isCooking = false

    /** The queue for mini game. */
    private val clickQueue = mutableListOf<Pair<Int, Int>>()

    private val baseWidth = 2340
    private val baseHeight = 1080
    private val panStartX
        get() = realWidth * (300f / baseWidth)
    private val panEndX
        get() = realWidth * (1870f / baseWidth)
    private val panY
        get() = realHeight * (480f / baseHeight)
    private val middleY
        get() = realHeight * (560f / baseHeight)
    private val foodY
        get() = realHeight * (920f / baseHeight)

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WindowManager::class.java)
        setScreenSize()
        setReceiver()
        foodPositionList = calculateFoodX()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Nothing need to retrieve
    }

    override fun onInterrupt() {
        // onInterrupt
    }

    override fun onUnbind(intent: Intent?): Boolean {
        unregisterReceiver(receiver)
        return super.onUnbind(intent)
    }

    /**
     * Retrieve the actual screen size for auto click feature.
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
     * Set the broadcast receiver for the service.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    when (intent.action) {
                        ACTION_START                                                -> {
                            isCooking = true
                            startClickMiniGame()
                        }

                        ACTION_STOP                                                 -> {
                            isCooking = false
                        }

                        ACTION_SUMI                                                 -> {
                            val x = intent.getIntExtra("x", 1)
                            val y = intent.getIntExtra("y", 1)
                            clickQueue.add(Pair(x, y))
                        }

                        ACTION_COOK_EGGS, ACTION_COOK_FRIES, ACTION_COOK_CROQUETTES -> {
                            isCooking = true
                            startCook(intent.action!!)
                        }
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_START)
            addAction(ACTION_STOP)
            addAction(ACTION_SUMI)
            addAction(ACTION_COOK_EGGS)
            addAction(ACTION_COOK_FRIES)
            addAction(ACTION_COOK_CROQUETTES)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentFilter, RECEIVER_NOT_EXPORTED)
        }
        else {
            registerReceiver(receiver, intentFilter)
        }
    }

    /**
     * Calculate the x position of the food.
     */
    private fun calculateFoodX(): List<Float> {
        val list = mutableListOf<Float>()
        val middleX = baseWidth / 2f

        if (TOTAL_FOOD_COUNT % 2 == 0) {
            val loop = TOTAL_FOOD_COUNT / 2
            var dX = 70

            repeat(loop) {
                list.add(middleX + dX)
                list.add(middleX - dX)
                dX += 140
            }
        }
        else {
            val loop = (TOTAL_FOOD_COUNT - 1) / 2
            var dX = 0

            list.add(middleX)
            repeat(loop) {
                dX += 140
                list.add(middleX + dX)
                list.add(middleX - dX)
            }
        }
        return list.sorted()
    }

    /**
     * Click mini game with queue.
     */
    private fun startClickMiniGame() {
        CoroutineScope(Dispatchers.Default).launch {
            while (clickQueue.isNotEmpty()) {
                sleep(300)
                val (x, y) = clickQueue.removeAt(0)
                clickMiniGame(x, y)
            }

            sendBroadcast(Intent().setAction(WindowService.SHOW_SCREEN_SOLVER).setPackage(packageName))
        }
    }

    /**
     * Perform click gesture at mini game.
     */
    private fun clickMiniGame(x: Int, y: Int) {
        val path = Path()
        var px = realWidth * (300f / baseWidth)
        var py = realHeight * (145f / baseHeight)
        px += realWidth * (1452f / baseWidth) / 22 * (2 * y - 1)
        py += realHeight * (790f / baseHeight) / 12 * (2 * x - 1)
        path.moveTo(px, py)
        performGesture(path, 100)
    }

    /**
     * Start cooking.
     */
    private fun startCook(action: String) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(1000)

            while (isCooking) {
                when (action) {
                    ACTION_COOK_EGGS       -> {
                        cookEggs()
                        delay(6000)
                    }

                    ACTION_COOK_FRIES      -> {
                        cookFries()
                        delay(5000)
                    }

                    ACTION_COOK_CROQUETTES -> {
                        cookCroquettes()
                        delay(7000)
                    }
                }
                // extra delay for collecting
                delay(500)
                collect()
                // delay for next cooking
                delay(3000)
            }

            sendBroadcast(Intent().setAction(WindowService.SHOW_SCREEN).setPackage(packageName))
        }
    }

    /**
     * Perform the gesture of cooking eggs.
     */
    private fun cookEggs() {
        val cookPath = Path()
        val eggX = realWidth * (foodPositionList[0] / baseWidth)

        cookPath.moveTo(eggX, foodY)
        cookPath.lineTo(panStartX, middleY)
        cookPath.lineTo(panStartX, panY)
        cookPath.lineTo(panEndX, panY)
        performGesture(cookPath, 1000)
    }

    /**
     * Perform the gesture of cooking fries.
     */
    private fun cookFries() {
        val cookPath = Path()
        val friesX = realWidth * (foodPositionList[1] / baseWidth)

        cookPath.moveTo(friesX, foodY)
        cookPath.lineTo(panStartX, middleY)
        cookPath.lineTo(panStartX, panY)
        cookPath.lineTo(panEndX, panY)
        performGesture(cookPath, 1000)
    }

    /**
     * Perform the gesture of cooking croquettes.
     */
    private fun cookCroquettes() {
        val cookPath = Path()
        val croquettesX = realWidth * (foodPositionList[2] / baseWidth)

        cookPath.moveTo(croquettesX, foodY)
        cookPath.lineTo(panStartX, middleY)
        cookPath.lineTo(panStartX, panY)
        cookPath.lineTo(panEndX, panY)
        performGesture(cookPath, 1000)
    }

    /**
     * Perform the gesture of collecting the cooked food.
     */
    private fun collect() {
        val collectPath = Path()

        collectPath.moveTo(panStartX, panY)
        collectPath.lineTo(panEndX, panY)
        performGesture(collectPath, 500)
    }

    /**
     * Perform the gesture.
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