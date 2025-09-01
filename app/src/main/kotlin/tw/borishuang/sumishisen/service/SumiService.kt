package tw.borishuang.sumishisen.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.*
import tw.borishuang.sumishisen.enums.PreferencesKey
import tw.borishuang.sumishisen.manager.BroadcastManager
import tw.borishuang.sumishisen.manager.startAction
import tw.borishuang.sumishisen.navigation.NavManager
import tw.borishuang.sumishisen.util.DataStoreUtil

class SumiService : AccessibilityService() {
    companion object {
        const val ACTION_START = "SumiService.StartGame"
        const val ACTION_STOP = "SumiService.StopGame"
        const val ACTION_SUMI = "SumiService.sumi"
        const val ACTION_COOK_EGGS = "SumiService.CookEggs"
        const val ACTION_COOK_FRIES = "SumiService.CookFries"
        const val ACTION_COOK_CROQUETTES = "SumiService.CookCroquettes"
        private const val BASE_WIDTH = 2340
        private const val BASE_HEIGHT = 1080
        private const val TOTAL_FOOD_COUNT = 3
    }

    private lateinit var windowManager: WindowManager
    private lateinit var broadcastManager: BroadcastManager
    private var realWidth = 0
    private var realHeight = 0

    private var isGesturePerform = false

    private val foodPositionList = calculateFoodX()

    /** The queue for mini game. */
    private val clickQueue = mutableListOf<Pair<Int, Int>>()

    private val panStartX
        get() = realWidth * (300f / BASE_WIDTH)
    private val panEndX
        get() = realWidth * (1870f / BASE_WIDTH)
    private val panY
        get() = realHeight * (480f / BASE_HEIGHT)
    private val middleY
        get() = realHeight * (560f / BASE_HEIGHT)
    private val foodY
        get() = realHeight * (920f / BASE_HEIGHT)

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WindowManager::class.java)

        setScreenSize()
        setBroadcastManager()

        CoroutineScope(Dispatchers.Default).launch {
            DataStoreUtil.createData(this@SumiService, PreferencesKey.SETTINGS_SHOW_SCREEN, true)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Nothing need to retrieve
    }

    override fun onInterrupt() {
        // onInterrupt
    }

    override fun onUnbind(intent: Intent?): Boolean {
        unregisterReceiver(broadcastManager)
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
    private fun setBroadcastManager() {
        broadcastManager = object : BroadcastManager() {
            override fun handleIntent(intent: Intent) {
                when (intent.action) {
                    ACTION_START                                                -> {
                        isGesturePerform = true
                        startClickMiniGame()
                    }

                    ACTION_STOP                                                 -> {
                        isGesturePerform = false
                    }

                    ACTION_SUMI                                                 -> {
                        val x = intent.getIntExtra("x", 1)
                        val y = intent.getIntExtra("y", 1)

                        clickQueue.add(Pair(x, y))
                    }

                    ACTION_COOK_EGGS, ACTION_COOK_FRIES, ACTION_COOK_CROQUETTES -> {
                        isGesturePerform = true
                        startCook(intent.action!!)
                    }
                }
            }
        }
        broadcastManager.register(this,
            listOf(ACTION_START, ACTION_STOP, ACTION_SUMI, ACTION_COOK_EGGS, ACTION_COOK_FRIES, ACTION_COOK_CROQUETTES))
    }

    /**
     * Calculate the x position of the food.
     */
    private fun calculateFoodX(): List<Float> {
        val list = mutableListOf<Float>()
        val middleX = BASE_WIDTH / 2f

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
                delay(300)
                val (x, y) = clickQueue.removeAt(0)
                clickMiniGame(x, y)
            }

            if (DataStoreUtil.readData(this@SumiService, PreferencesKey.SETTINGS_SHOW_SCREEN, true)) {
                startAction(NavManager.ACTION_SHOW_SCREEN)
            }
            else {
                startAction(NavManager.ACTION_STOP_PERFORM_GESTURE)
                startAction(NavManager.ACTION_NAVIGATE_SCREEN_SOLVER)
            }
        }
    }

    /**
     * Perform click gesture at mini game.
     */
    private fun clickMiniGame(x: Int, y: Int) {
        val path = Path()
        var px = realWidth * (300f / BASE_WIDTH)
        var py = realHeight * (145f / BASE_HEIGHT)

        px += realWidth * (1452f / BASE_WIDTH) / 22 * (2 * y - 1)
        py += realHeight * (790f / BASE_HEIGHT) / 12 * (2 * x - 1)
        path.moveTo(px, py)
        performGesture(path, 100)
    }

    /**
     * Start cooking.
     */
    private fun startCook(action: String) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(1000)

            while (isGesturePerform) {
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

            if (DataStoreUtil.readData(this@SumiService, PreferencesKey.SETTINGS_SHOW_SCREEN, true)) {
                startAction(NavManager.ACTION_SHOW_SCREEN)
            }
            else {
                startAction(NavManager.ACTION_STOP_PERFORM_GESTURE)
            }
        }
    }

    /**
     * Perform the gesture of cooking eggs.
     */
    private fun cookEggs() {
        val cookPath = Path()
        val eggX = realWidth * (foodPositionList[0] / BASE_WIDTH)

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
        val friesX = realWidth * (foodPositionList[1] / BASE_WIDTH)

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
        val croquettesX = realWidth * (foodPositionList[2] / BASE_WIDTH)

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

        dispatchGesture(gestureDescription, object : GestureResultCallback() {}, null)
    }
}