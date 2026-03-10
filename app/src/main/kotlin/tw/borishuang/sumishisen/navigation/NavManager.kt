package tw.borishuang.sumishisen.navigation

import android.content.*
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import kotlinx.coroutines.*
import tw.borishuang.sumishisen.enums.PreferencesKey
import tw.borishuang.sumishisen.manager.BroadcastManager
import tw.borishuang.sumishisen.manager.startAction
import tw.borishuang.sumishisen.service.SumiService
import tw.borishuang.sumishisen.service.WindowService
import tw.borishuang.sumishisen.ui.*
import tw.borishuang.sumishisen.util.DataStoreUtil
import kotlin.math.roundToInt

class NavManager(context: Context) : ContextWrapper(context) {
    companion object {
        const val ACTION_SHOW_SCREEN = "NavManager.ShowScreen"
        const val ACTION_HIDE_SCREEN = "NavManager.HideScreen"
        const val ACTION_START_PERFORM_GESTURE = "NavManager.StartPerformGesture"
        const val ACTION_STOP_PERFORM_GESTURE = "NavManager.StopPerformGesture"
        const val ACTION_NAVIGATE_SCREEN_SOLVER = "NavManager.NavigateScreenSolver"
    }

    private val metrics = resources.displayMetrics
    private val windowManager = getSystemService(WindowManager::class.java)
    private val screenWidth
        get() = metrics.widthPixels
    private val screenHeight
        get() = metrics.heightPixels

    /** Record the current screen. */
    private var currentScreen: Screens = Screens.Home

    /** Is the icon being clicked. */
    private var isScreenShow = false

    /** Is currently cooking. */
    private var isGesturePerform = false

    private val broadcastManager = object : BroadcastManager() {
        override fun handleIntent(intent: Intent) {
            when (intent.action) {
                ACTION_SHOW_SCREEN            -> {
                    isGesturePerform = false
                    showScreen()
                }

                ACTION_HIDE_SCREEN            -> hideScreen()
                ACTION_START_PERFORM_GESTURE  -> isGesturePerform = true
                ACTION_STOP_PERFORM_GESTURE   -> isGesturePerform = false
                ACTION_NAVIGATE_SCREEN_SOLVER -> currentScreen = Screens.MiniGameSolver
            }
        }
    }

    /**
     * Set the icon's size and position.
     */
    private val iconLayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSPARENT
    ).apply {
        gravity = Gravity.CENTER
        x = screenWidth / 2
        y = -screenHeight / 4
        height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, metrics).roundToInt()
        width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, metrics).roundToInt()
    }

    /**
     * Set the board size and position.
     */
    private val windowLayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.RGBA_8888
    ).apply {
        gravity = Gravity.CENTER
        x = 0
        y = 0
        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        if (screenHeight > screenWidth) {
            height = (screenWidth * 0.85f).toInt()
            width = (screenHeight * 0.85f).toInt()
        }
        else {
            height = (screenHeight * 0.85f).toInt()
            width = (screenWidth * 0.85f).toInt()
        }
        windowAnimations = android.R.style.Animation
    }

    private val iconView = IconView(this).apply {
        setLayoutParams(iconLayoutParams)
        setOnClickListener {
            if (isGesturePerform) {
                setIcon(false)
                startAction(SumiService.ACTION_STOP)
            }
            else if (isScreenShow) {
                hideScreen()
            }
            else {
                showScreen()
            }
        }
        setOnStopListener {
            startAction(WindowService.ACTION_STOP)
        }
    }

    private val homeView = HomeView(this).apply {
        setLayoutParams(windowLayoutParams)
        setOnMiniGameClick {
            navigate(Screens.MiniGameSolver)
        }
        setOnEggsClick {
            hideScreen()
            isGesturePerform = true
            iconView.setIcon(true)
            startAction(SumiService.ACTION_COOK_EGGS)
        }
        setOnFriesClick {
            hideScreen()
            isGesturePerform = true
            iconView.setIcon(true)
            startAction(SumiService.ACTION_COOK_FRIES)
        }
        setOnCroquettesClick {
            hideScreen()
            isGesturePerform = true
            iconView.setIcon(true)
            startAction(SumiService.ACTION_COOK_CROQUETTES)
        }
        setOnSettingsClick {
            navigate(Screens.Settings)
        }
    }

    private val miniGameView = MiniGameView(this).apply {
        setLayoutParams(windowLayoutParams)
        setOnBackClick {
            navigate(Screens.Home)
        }
        setOnOkClick {
            hideScreen()
            CoroutineScope(Dispatchers.Main).launch {
                setResultView(it)
                navigate(Screens.MiniGameResult)
            }
        }
    }

    private lateinit var resultView: ResultView

    private val settingsView = SettingsView(this).apply {
        setLayoutParams(windowLayoutParams)
        setOnPrivacyClickListener {
            navigate(Screens.Privacy)
        }
        setOnBackListener {
            navigate(Screens.Home)
        }
    }

    private val privacyView = PrivacyView(this).apply {
        setLayoutParams(windowLayoutParams)
        setOnOkClickListener {
            navigate(Screens.Home)
        }
    }

    /**
     * Initialize the manager.
     */
    fun init() {
        broadcastManager.register(
            this, listOf(
                ACTION_SHOW_SCREEN,
                ACTION_NAVIGATE_SCREEN_SOLVER,
                ACTION_HIDE_SCREEN,
                ACTION_START_PERFORM_GESTURE,
                ACTION_STOP_PERFORM_GESTURE
            )
        )
        windowManager.addView(iconView, iconLayoutParams)
        CoroutineScope(Dispatchers.Main).launch {
            if (!DataStoreUtil.readData(this@NavManager, PreferencesKey.SHOW_PRIVACY, false)) {
                currentScreen = Screens.Privacy
            }
        }
    }

    /**
     * Destroy the manager.
     */
    fun destroy() {
        hideScreen()
        windowManager.removeView(iconView)
        broadcastManager.unregister(this)
    }

    /**
     * Set the mini-game result view.
     */
    private fun setResultView(input: String) {
        resultView = ResultView(this).apply {
            setLayoutParams(windowLayoutParams)
            setOnCloseClick {
                navigate(Screens.MiniGameSolver)
            }
            setData(input)
        }
    }

    /**
     * Navigate to the given screen.
     */
    private fun navigate(screen: Screens) {
        hideScreen()
        currentScreen = screen
        showScreen()
    }

    /**
     * Show the screen on the screen.
     */
    private fun showScreen() {
        if (!isScreenShow) {
            when (currentScreen) {
                Screens.Home           -> windowManager.addView(homeView, windowLayoutParams)
                Screens.MiniGameSolver -> windowManager.addView(miniGameView, windowLayoutParams)
                Screens.MiniGameResult -> windowManager.addView(resultView, windowLayoutParams)
                Screens.Settings       -> windowManager.addView(settingsView, windowLayoutParams)
                Screens.Privacy        -> windowManager.addView(privacyView, windowLayoutParams)
            }
        }
        isScreenShow = true
    }

    /**
     * Hide the screen on the screen.
     */
    private fun hideScreen() {
        if (isScreenShow) {
            when (currentScreen) {
                Screens.Home           -> windowManager.removeView(homeView)
                Screens.MiniGameSolver -> windowManager.removeView(miniGameView)
                Screens.MiniGameResult -> windowManager.removeView(resultView)
                Screens.Settings       -> windowManager.removeView(settingsView)
                Screens.Privacy        -> windowManager.removeView(privacyView)
            }
        }
        isScreenShow = false
    }
}