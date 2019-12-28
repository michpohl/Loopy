package de.michaelpohl.loopy.model

class InternalDataRepository(private val sharedPrefsManager: SharedPreferencesManager) {

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
}