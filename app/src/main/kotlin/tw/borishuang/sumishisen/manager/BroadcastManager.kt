package tw.borishuang.sumishisen.manager

import android.app.Service.RECEIVER_NOT_EXPORTED
import android.content.*
import android.os.Build

/**
 * Wrap up the broadcast receiver.
 */
abstract class BroadcastManager : BroadcastReceiver() {
    abstract fun handleIntent(intent: Intent)

    final override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            handleIntent(intent)
        }
    }

    fun register(context: Context, actions: List<String>) {
        val intentFilter = IntentFilter().apply {
            actions.forEach {
                addAction(it)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, intentFilter, RECEIVER_NOT_EXPORTED)
        }
        else {
            context.registerReceiver(this, intentFilter)
        }
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }
}

/**
 * Send broadcast with action.
 */
fun Context.startAction(action: String) = sendBroadcast(Intent().setAction(action).setPackage(packageName))