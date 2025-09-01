package tw.borishuang.sumishisen.enums

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

/**
 * The key of all preferences.
 */
object PreferencesKey {
    /** The key of data count. */
    val SUMISHISEN_DATA_COUNT = intPreferencesKey("count")

    /** The key of show screen. */
    val SETTINGS_SHOW_SCREEN = booleanPreferencesKey("show_screen")

    /** The key of show privacy when first time launch. */
    val SHOW_PRIVACY = booleanPreferencesKey("show_privacy")
}