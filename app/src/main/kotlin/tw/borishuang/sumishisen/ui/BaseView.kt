package tw.borishuang.sumishisen.ui

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding

abstract class BaseView<V : ViewBinding>(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

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