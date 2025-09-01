package tw.borishuang.sumishisen.ui

import android.content.Context
import android.view.LayoutInflater
import kotlinx.coroutines.*
import tw.borishuang.sumishisen.databinding.PrivacyLayoutBinding
import tw.borishuang.sumishisen.enums.PreferencesKey
import tw.borishuang.sumishisen.util.DataStoreUtil

class PrivacyView(context: Context) : BaseView<PrivacyLayoutBinding>(context) {
    override val binding = PrivacyLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.tvPrivacy.loadDataWithBaseURL(null,
            context.assets.open("privacy.txt").bufferedReader().use { it.readText() }, null, null, null)
    }

    fun setOnOkClickListener(onClick: () -> Unit) {
        binding.btnPrivacyOk.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                DataStoreUtil.writeData(context, PreferencesKey.SHOW_PRIVACY, true)
                onClick()
            }
        }
    }
}