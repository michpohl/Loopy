package de.michaelpohl.loopy.model

import android.content.res.AssetManager
import de.michaelpohl.loopy.common.AudioModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class AudioFilesRepository(
    private val sharedPrefsManager: SharedPreferencesManager,
    private val storage: ExternalStorageManager
) : KoinComponent {

    private val assets: AssetManager by inject()

    fun getSavedSets(): Sets {
        return sharedPrefsManager.loadLoopSets()
    }

    fun saveSet(set: LoopSet) {
        sharedPrefsManager.saveSingleSet(set)
    }

    fun getSingleSet(setFolderName: String? = null): List<AudioModel> {
        return storage.listSetContents(setFolderName ?: STANDARD_SET_FOLDER_NAME)
    }

    /**
     * Creates the standard set folder and moves all found audio files from assets to there
     * @return true if everything was successful
     */
    fun autoCreateStandardLoopSet(): Boolean {
        return if (storage.createSetFolder() && storage.copyStandardFilesToSdCard()) {
            sharedPrefsManager.selectedSetName = STANDARD_SET_FOLDER_NAME
            true
        } else false
    }

    fun addLoopsToSet(newLoops: List<AudioModel>) {
    }

    fun saveLoopSelection(loopsList: MutableList<AudioModel>) {
        sharedPrefsManager.saveLoopSelection(loopsList)
    }
}
