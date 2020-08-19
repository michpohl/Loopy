package de.michaelpohl.loopy.model

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.JsonDataClass
import de.michaelpohl.loopy.common.Settings
import timber.log.Timber

/**
 * Possible null/empty objects should be handled here, so that downstream there are no nulls!
 */
class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder().build()

    var selectedSetName: String?
        get() {
            return getString(SELECTED_SET, "")
        }
        set(value) {
            putString(SELECTED_SET, value!!)
        }

    fun loadLoopSets(): Sets {
        return getString(SETS)?.let {
            JsonDataClass.fromJsonString<Sets>(it)
        } ?: Sets(listOf())
    }

    fun saveLoopSets(sets: Sets) {
        return putString(sets.toJsonString(), Sets(listOf()).toJsonString())
    }

    fun getSettings(): Settings? {
        return getString(SETTINGS_KEY)?.let {
            moshi.adapter(Settings::class.java).fromJson(it)
        }
    }

    fun saveSettings(settings: Settings) {
        Timber.d("Settings String: $settings")
        putString(SETTINGS_KEY, moshi.adapter(Settings::class.java).toJson(settings))
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    private fun delete(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    /**
     * First checks for a set of the same name, then removes it, adds the new one,
     * then saves a new Set
     */
    fun saveSingleSet(set: LoopSet) {
        var sets = loadLoopSets().loopSets.toMutableList()
        sets.remove(sets.find { it.name == set.name })
        sets.add(set)
        saveLoopSets(Sets(sets))
    }

    /**
     * Save the selected loops the player currently displays, not a Set!. These loops can be anywhere.
     */
    fun saveLoopSelection(loopsList: MutableList<AudioModel>) {
//        putString(SELECTED_LOOPS, loopsList.toJson)
    }

    companion object {
        const val SHARED_PREFS_KEY = "loopy"
        const val SETS = "sets"
        const val SELECTED_SET = "selectedset"
        const val SELECTED_LOOPS = "selectedloop"

        const val SETTINGS_KEY = "settings"
    }
}
