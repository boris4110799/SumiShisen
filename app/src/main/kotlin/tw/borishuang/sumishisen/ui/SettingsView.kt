package tw.borishuang.sumishisen.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.core.net.toUri
import kotlinx.coroutines.*
import tw.borishuang.sumishisen.BuildConfig
import tw.borishuang.sumishisen.databinding.SettingsLayoutBinding
import tw.borishuang.sumishisen.enums.PreferencesKey
import tw.borishuang.sumishisen.util.DataStoreUtil

class SettingsView(context: Context) : BaseView<SettingsLayoutBinding>(context) {

    override val binding = SettingsLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private val intent = Intent(Intent.ACTION_VIEW, "https://boris4110799.github.io/SumiShisen/".toUri()).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    init {
        binding.tvVersion.text = BuildConfig.VERSION_NAME

        CoroutineScope(Dispatchers.IO).launch {
            binding.switchShowScreen.isChecked = DataStoreUtil.readData(context, PreferencesKey.SETTINGS_SHOW_SCREEN, true)
        }

        binding.switchShowScreen.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                DataStoreUtil.writeData(context, PreferencesKey.SETTINGS_SHOW_SCREEN, isChecked)
            }
        }
    }

    fun setOnPrivacyClickListener(onClick: () -> Unit) {
        binding.ivPrivacyLink.setOnClickListener {
            onClick()
        }
    }

    fun setOnBackListener(onClick: () -> Unit) {
        binding.btnBack.setOnClickListener {
            onClick()
        }
    }
}