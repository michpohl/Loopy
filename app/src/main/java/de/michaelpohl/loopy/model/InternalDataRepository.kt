package de.michaelpohl.loopy.model

import timber.log.Timber

class InternalDataRepository(private val sharedPrefsManager: SharedPreferencesManager, private val storage: ExternalStorageManager) {

    fun getSavedSets() : Sets {
        return sharedPrefsManager.loadLoopSets()
    }

    fun saveSet(set: LoopSet) {
        sharedPrefsManager.saveSingleSet(set)
    }

    fun getSingleSet() : LoopSet {
        //TODO fill with real functionality
        return LoopSet("",listOf())
    }

    fun autoCreateFirstLoopSet() {
    val wasFolderCreated = storage.createAppFolder() && storage.createLoopFolder("standard")
    Timber.d("Was the folder created?")
    }
}