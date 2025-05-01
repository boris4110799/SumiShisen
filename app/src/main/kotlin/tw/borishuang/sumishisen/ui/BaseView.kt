package tw.borishuang.sumishisen.ui

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.viewbinding.ViewBinding

/**
 * Base view class for all views.
 */
abstract class BaseView<V : ViewBinding>(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    protected abstract val binding: V

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in).apply {
            duration = 200
        }

        binding.root.startAnimation(animation)
    }

    /**
     * Wrap up the find button method.
     */
    protected fun findButton(id: Int): Button {
        return binding.root.findViewById(id)
    }
}