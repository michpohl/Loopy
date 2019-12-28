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

    fun loadLoopSets(): Sets {
        return getString(SETS_KEY)?.let {
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
        const val SETS_KEY = "sets"
    }
}
