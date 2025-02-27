package tw.borishuang.sumishisen.service

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import androidx.lifecycle.LifecycleService
import kotlinx.coroutines.*
import tw.borishuang.sumishisen.navigation.Screens
import tw.borishuang.sumishisen.ui.*
import tw.borishuang.sumishisen.util.NotificationUtil
import kotlin.math.roundToInt

/**
 * The Service that handles the window view.
 */
class WindowService : LifecycleService() {
    companion object {
        const val ACTION_CLOSE = "CLOSE"
        const val SHOW_SCREEN = "SHOW_SCREEN"
        const val SHOW_SCREEN_SOLVER = "SHOW_SCREEN_SOLVER"
        const val HIDE_SCREEN = "HIDE_SCREEN"
    }

    private lateinit var metrics: DisplayMetrics
    private lateinit var windowManager: WindowManager
    private lateinit var accessibilityManager: AccessibilityManager

    private lateinit var iconView: IconView
    private lateinit var homeView: HomeView
    private lateinit var miniGameView: MiniGameView
    private lateinit var resultView: ResultView
    private lateinit var iconLayoutParams: WindowManager.LayoutParams
    private lateinit var windowLayoutParams: WindowManager.LayoutParams
    private lateinit var receiver: BroadcastReceiver
    private val screenWidth
        get() = metrics.widthPixels
    private val screenHeight
        get() = metrics.heightPixels

    /** Record the current screen. */
    private var currentScreen: Screens = Screens.Home

    /** Is the icon being click. */
    private var isScreenShow = false

    /** Is currently cooking. */
    private var isCooking = false

    override fun onCreate() {
        super.onCreate()

        metrics = applicationContext.resources.displayMetrics
        windowManager = getSystemService(WindowManager::class.java)
        accessibilityManager = getSystemService(AccessibilityManager::class.java)

        setNotification()
        setIconView()
        setHomeView()
        setMiniGameView()
        setReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action

            if (action != null) {
                when (action) {
                    // When user clicked the 'Close' button in notification, stop the service
                    ACTION_CLOSE -> stopSelf()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        hideScreen()
        windowManager.removeView(iconView)
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    /**
     * Set the notification for the service.
     */
    private fun setNotification() {
        val builder = NotificationUtil.setupBuilder(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, builder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        }
        else {
            startForeground(1, builder.build())
        }
    }

    /**
     * Set the icon view and show on the screen.
     */
    private fun setIconView() {
        //Set the icon's size and position
        iconLayoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT).apply {
            gravity = Gravity.CENTER
            x = screenWidth / 2
            y = -screenHeight / 4
            height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, metrics).roundToInt()
            width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, metrics).roundToInt()
        }

        iconView = IconView(this).apply {
            setLayoutParams(iconLayoutParams)
            setOnClickListener {
                if (isCooking) {
                    setIcon(false)
                    sendBroadcast(Intent().setAction(SumiService.ACTION_STOP).setPackage(packageName))
                }
                else if (isScreenShow) {
                    hideScreen()
                }
                else {
                    showScreen()
                }
            }
            setOnStopListener {
                stopSelf()
            }
        }

        windowManager.addView(iconView, iconLayoutParams)
    }

    /**
     * Set the board view.
     */
    private fun setHomeView() {
        //Set the board size and position
        windowLayoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.RGBA_8888).apply {
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

        homeView = HomeView(this).apply {
            setLayoutParams(windowLayoutParams)
            setOnMiniGameClick {
                navigate(Screens.MiniGameSolver)
            }
            setOnEggsClick {
                isCooking = true
                iconView.setIcon(true)
                hideScreen()
                sendBroadcast(Intent().setAction(SumiService.ACTION_COOK_EGGS).setPackage(packageName))
            }
            setOnFriesClick {
                isCooking = true
                iconView.setIcon(true)
                hideScreen()
                sendBroadcast(Intent().setAction(SumiService.ACTION_COOK_FRIES).setPackage(packageName))
            }
            setOnCroquettesClick {
                isCooking = true
                iconView.setIcon(true)
                hideScreen()
                sendBroadcast(Intent().setAction(SumiService.ACTION_COOK_CROQUETTES).setPackage(packageName))
            }
        }
    }

    /**
     * Set the mini game solver view.
     */
    private fun setMiniGameView() {
        miniGameView = MiniGameView(this).apply {
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
    }

    /**
     * Set the mini game result view.
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
     * Set the broadcast receiver for the service.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun setReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    when (intent.action) {
                        SHOW_SCREEN        -> {
                            isCooking = false
                            showScreen()
                        }

                        SHOW_SCREEN_SOLVER -> {
                            isCooking = false
                            currentScreen = Screens.MiniGameSolver
                            showScreen()
                        }

                        HIDE_SCREEN        -> {
                            isCooking = true
                            hideScreen()
                        }
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(SHOW_SCREEN)
            addAction(SHOW_SCREEN_SOLVER)
            addAction(HIDE_SCREEN)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentFilter, RECEIVER_NOT_EXPORTED)
        }
        else {
            registerReceiver(receiver, intentFilter)
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
            }
        }
        isScreenShow = false
    }
}