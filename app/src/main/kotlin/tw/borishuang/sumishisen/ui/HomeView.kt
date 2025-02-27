package tw.borishuang.sumishisen.ui

import android.content.Context
import android.view.LayoutInflater
import tw.borishuang.sumishisen.BuildConfig
import tw.borishuang.sumishisen.databinding.HomeLayoutBinding

class HomeView(context: Context) : BaseView<HomeLayoutBinding>(context) {

    override val binding = HomeLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.tvVersion.text = BuildConfig.VERSION_NAME
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
}