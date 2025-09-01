package tw.borishuang.sumishisen.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * The utility of datastore.
 */
object DataStoreUtil {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sumishisen")

    /**
     * Read data from datastore.
     */
    suspend fun <T> readData(context: Context, key: Preferences.Key<T>, default: T): T = withContext(Dispatchers.IO) {
        val data = context.dataStore.data.first()

        data[key] ?: default
    }

    /**
     * Write data to datastore.
     */
    suspend fun <T> writeData(context: Context, key: Preferences.Key<T>, value: T) = withContext(Dispatchers.IO) {
        context.dataStore.updateData { it.toMutablePreferences().apply { this[key] = value } }
    }

    /**
     * Create data in datastore.
     */
    suspend fun <T> createData(context: Context, key: Preferences.Key<T>, default: T) = withContext(Dispatchers.IO) {
        writeData(context, key, readData(context, key, default))
    }
}