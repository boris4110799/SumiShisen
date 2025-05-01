package tw.borishuang.sumishisen.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.core.net.toUri
import kotlinx.coroutines.*
import tw.borishuang.sumishisen.BuildConfig
import tw.borishuang.sumishisen.databinding.SettingsLayoutBinding
import tw.borishuang.sumishisen.manager.startAction
import tw.borishuang.sumishisen.navigation.NavManager
import tw.borishuang.sumishisen.util.DataStoreUtil

class SettingsView(context: Context) : BaseView<SettingsLayoutBinding>(context) {

    override val binding = SettingsLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private val intent = Intent(Intent.ACTION_VIEW, "https://boris4110799.github.io/SumiShisen/".toUri()).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    init {
        binding.tvVersion.text = BuildConfig.VERSION_NAME
        binding.ivPrivacyLink.setOnClickListener {
            context.startAction(NavManager.ACTION_HIDE_SCREEN)
            context.startActivity(intent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            binding.switchShowScreen.isChecked = DataStoreUtil.readData(context, DataStoreUtil.SETTINGS_SHOW_SCREEN, true)
        }

        binding.switchShowScreen.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                DataStoreUtil.writeData(context, DataStoreUtil.SETTINGS_SHOW_SCREEN, isChecked)
            }
        }
    }

    fun setOnBackListener(onClick: () -> Unit) {
        binding.btnBack.setOnClickListener {
            onClick()
        }
    }
}