package de.michaelpohl.loopy.model

import android.content.res.AssetManager
import com.squareup.moshi.Moshi
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.jni.JniBridge
import de.michaelpohl.loopy.ui.licenses.Libraries
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.InputStream

class FilesRepository(
    private val sharedPrefsManager: SharedPreferencesManager,
    private val storage: ExternalStorageManager
) : KoinComponent {

    private val assets: AssetManager by inject()
    fun saveSet(set: LoopSet) {
        sharedPrefsManager.saveSingleSet(set)
    }

    /**
     * Load a) the set from the specified folder, b) the saved last selected loops, c) the standard set
     */
    fun getSingleSetOrStandardSet(setFolderName: String? = null): List<AudioModel> {
        return storage.getAudioModelsInSet(setFolderName ?: STANDARD_SET_FOLDER_NAME)
    }

    suspend fun convertFilesInSet(setFolderName: String = STANDARD_SET_FOLDER_NAME): Boolean {
        return JniBridge.convertFilesInFolder(storage.getFullPath(setFolderName))
    }

    // TODO move the whole standard loop set stuff into a separate class.
    //  It clutters this one, and that's not necessary for "execute once ever" code.

    /**
     * Creates the standard set folder and moves all found audio files from assets to there
     * @return true if everything was successful
     */
    fun autoCreateStandardLoopSet(): Boolean {

        return if (storage.createSetFolder() && storage.copyStandardFilesToSdCard() && storage.copyMiscFiles()) {
            sharedPrefsManager.selectedSetName = STANDARD_SET_FOLDER_NAME
            true
        } else false
    }

    suspend fun addLoopsToSet(
        newLoops: List<FileModel.AudioFile>,
        setName: String? = null
    ): JniBridge.ConversionResult {

        return JniBridge.convertAndAddToSet(
            newLoops, storage.getFullPath(setName ?: STANDARD_SET_FOLDER_NAME)
        )
    }

    fun saveLoopSelectionToSet(setFolderName: String? = null, loopsList: List<AudioModel>) {
        val currentlyInSet =
            storage.getAudioModelsInSet(setFolderName ?: STANDARD_SET_FOLDER_NAME).toMutableSet()
        currentlyInSet.forEach {
            if (!loopsList.contains(it)) storage.deleteFromSet(setFolderName ?: STANDARD_SET_FOLDER_NAME, it)
        }
    }

    fun getStringAsset(fileName: String) : String? {
        return assets.open(fileName).bufferedReader().use {
            it.readText()
        }
    }

    fun getLicenses() : Libraries? {
            val inputStream: InputStream = assets.open("licenses.json")
            val inputString = inputStream.bufferedReader().use{it.readText()}
            Timber.d(inputString)
        val adapter = Moshi.Builder().build().adapter(Libraries::class.java)
        return adapter.fromJson(inputString)
    }
}
