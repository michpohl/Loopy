package de.michaelpohl.loopy.model

import android.content.Context
import android.content.SharedPreferences
import de.michaelpohl.loopy.common.JsonDataClass

/**
 * Possible null/empty objects should be handled here, so that downstream there are no nulls!
 */
class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)

    var setupComplete: Boolean
        get() {
            return getBoolean(APP_SETUP_COMPLTE)
        }
        set(value) {
            putBoolean(APP_SETUP_COMPLTE, value)
        }

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

    private fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun getString(key: String, defaultValue: String? = ""): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    private fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    private fun putBoolean(key: String, value: Boolean) {
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

    companion object {
        const val SHARED_PREFS_KEY = "loopy"
        const val SETS = "sets"
        const val APP_SETUP_COMPLTE = "setup"
        const val SELECTED_SET = "selectedset"
    }
}
