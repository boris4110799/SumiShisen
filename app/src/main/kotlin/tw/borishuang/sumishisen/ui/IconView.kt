package tw.borishuang.sumishisen.ui

import android.content.Context
import android.content.res.Configuration
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import tw.borishuang.sumishisen.R
import tw.borishuang.sumishisen.databinding.IconLayoutBinding
import kotlin.math.abs

class IconView(context: Context) : BaseView<IconLayoutBinding>(context) {

    override val binding = IconLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private val windowManager = context.getSystemService(WindowManager::class.java)
    private val metrics
        get() = context.resources.displayMetrics
    private val screenWidth
        get() = metrics.widthPixels
    private val screenHeight
        get() = metrics.heightPixels

    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var onStopListener: () -> Unit
    private var px = 0.0
    private var py = 0.0
    private var sx = 0.0
    private var sy = 0.0

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        layoutParams.apply {
            x = (x / abs(x)) * screenWidth / 2
            y = (y.toDouble() / screenWidth.toDouble() * screenHeight.toDouble()).toInt()
        }
        windowManager.updateViewLayout(this, layoutParams)
    }

    override fun performClick(): Boolean {
        windowManager.updateViewLayout(this, layoutParams)
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                sx = layoutParams.x.toDouble()
                sy = layoutParams.y.toDouble()
                px = event.rawX.toDouble()
                py = event.rawY.toDouble()
            }

            MotionEvent.ACTION_MOVE -> {
                layoutParams.x = (sx + event.rawX - px).toInt()
                layoutParams.y = (sy + event.rawY - py).toInt()
                windowManager.updateViewLayout(this, layoutParams)
            }

            MotionEvent.ACTION_UP   -> {
                if (layoutParams.x in -150..150 && layoutParams.y in screenHeight / 2 - 300..screenHeight / 2) {
                    onStopListener()
                }
                else {
                    if (layoutParams.x < 0) {
                        layoutParams.x = -screenWidth / 2
                        windowManager.updateViewLayout(this, layoutParams)
                    }
                    else if (layoutParams.x >= 0) {
                        layoutParams.x = screenWidth / 2
                        windowManager.updateViewLayout(this, layoutParams)
                    }
                    if (abs(event.rawX.toDouble() - px) < 10 && abs(event.rawY.toDouble() - py) < 10) {
                        performClick()
                    }
                }
            }
        }
        return false
    }

    fun setLayoutParams(params: WindowManager.LayoutParams) {
        layoutParams = params
    }

    fun setOnStopListener(listener: () -> Unit) {
        onStopListener = listener
    }

    /**
     * Set the icon of the view.
     */
    fun setIcon(isCooking: Boolean) {
        if (isCooking) {
            binding.llIcon.background = AppCompatResources.getDrawable(context, R.drawable.ic_cooking)
        }
        else {
            binding.llIcon.background = AppCompatResources.getDrawable(context, R.drawable.ic_sumi)
        }
    }
}