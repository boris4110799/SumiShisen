package tw.borishuang.sumishisen.ui

import android.content.Context
import android.view.LayoutInflater
import tw.borishuang.sumishisen.databinding.HomeLayoutBinding
import tw.borishuang.sumishisen.util.PermissionUtil

class HomeView(context: Context) : BaseView<HomeLayoutBinding>(context) {

    override val binding = HomeLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val isAccessibilityEnabled = PermissionUtil.isAccessibilityServiceEnabled(context)

        binding.btnEgg.isEnabled = isAccessibilityEnabled
        binding.btnFries.isEnabled = isAccessibilityEnabled
        binding.btnCroquettes.isEnabled = isAccessibilityEnabled
    }

    fun setOnMiniGameClick(onClick: () -> Unit) {
        binding.btnMiniGame.setOnClickListener { onClick() }
    }

    fun setOnEggsClick(onClick: () -> Unit) {
        binding.btnEgg.setOnClickListener { onClick() }
    }

    fun setOnFriesClick(onClick: () -> Unit) {
        binding.btnFries.setOnClickListener { onClick() }
    }

    fun setOnCroquettesClick(onClick: () -> Unit) {
        binding.btnCroquettes.setOnClickListener { onClick() }
    }

    fun setOnSettingsClick(onClick: () -> Unit) {
        binding.btnSettings.setOnClickListener { onClick() }
    }
}