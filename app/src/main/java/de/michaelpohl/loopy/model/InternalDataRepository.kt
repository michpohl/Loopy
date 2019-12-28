package de.michaelpohl.loopy.model

class InternalDataRepository(val sharedPrefsManager: SharedPreferencesManager) {

    fun getSavedSets() : List<LoopSet> {
        sharedPrefsManager
    }
}