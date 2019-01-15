package de.michaelpohl.loopy.model

import android.content.SharedPreferences
import com.google.gson.Gson
import de.michaelpohl.loopy.common.*
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException

object DataRepository {

    private const val PREFS_LOOPY_KEY = "loops_list"
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var savedAppData: AppData

    var currentSelectedFileModels = listOf<FileModel>()
        private set
    private var newSelectedFileModels = listOf<FileModel>()
    var settings = Settings()

    /**
     * initializes the DataRepository by fetching the saved selectedState from sharedPreferences
     */
    fun init(sharedPrefs: SharedPreferences) {
        this.sharedPrefs = sharedPrefs
        this.savedAppData = loadSavedAppData()
        currentSelectedFileModels = savedAppData.models
        this.settings = savedAppData.settings
    }

    fun saveCurrentState(
        selectedLoops: List<FileModel> = this.currentSelectedFileModels,
        settings: Settings = this.settings
    ) {
        val jsonString = Gson().toJson(AppData(selectedLoops, settings))
//        TODO put fitting assertion
//        Assert.assertEquals(jsonString, """{"id":1,"description":"Test"}""")
        with(sharedPrefs.edit()) {
            putString(PREFS_LOOPY_KEY, jsonString)
            apply() //writes the data in the background, as opposed to commit()
        }
    }

    fun updateAndSaveFileSelection(): Boolean {
        return if (newSelectedFileModels.isNotEmpty()) {
            currentSelectedFileModels = newSelectedFileModels + currentSelectedFileModels

            saveCurrentState()
            true
        } else false
    }

    fun onFileSelectionUpdated(newSelection: List<FileModel>) {
        //only add the ones that are not already selected
        newSelectedFileModels = newSelection.filter { it ->
            !currentSelectedFileModels.contains(it)
        }
    }

    fun onLoopsListCleared() {
        currentSelectedFileModels = emptyList()
        saveCurrentState()
    }

    /**
     * loads the saved app selectedState from SharedPreferences
     * If there is no selectedState, an AppData object is created with the standard settings
     * @return The AppData object from SharedPreferences or a new one, if none exists
     */
    private fun loadSavedAppData(): AppData {
        val warnString = "warning" //this can be done better :-)
        val jsonString = sharedPrefs.getString(PREFS_LOOPY_KEY, warnString)

        return if (jsonString != "warning") {
            appDataFromJson(jsonString)
        } else {
            // if we have no saved selectedState, we start up with an empty list of loops and allow all audio file types
            AppData(arrayListOf(), Settings(allowedFileTypes = ValidAudioFileType.values()))
        }
    }

    fun getAllowedFileTypeListAsString(): String {
        val builder = StringBuilder()
        val allowedFileTypes = settings.allowedFileTypes
        allowedFileTypes.forEach {
            builder.append(it.name)
            if (allowedFileTypes.indexOf(it) != allowedFileTypes.size - 1) {
                builder.append(", ")
            }
        }
        return builder.toString()
    }

    fun testIntegrity(fileModels: List<FileModel>): List<FileModel> {
        var validModels = mutableListOf<FileModel>()
        fileModels.forEach {
            val file = FileHelper.getSingleFile(it.path)
            if (file.exists()) {
                Timber.v("File %s exists", file.path)
                validModels.add(it)
            } else {
                Timber.v("File %s doesn't exist, removing", file.path)
            }
        }
        return validModels
    }

    fun handleFileFromIntent(file: File) {
        onFileSelectionUpdated(FileHelper.getFileModelsFromFiles(listOf(file)))
    }

    fun getMediaStoreEntries() {
        
    }

    private fun appDataFromJson(jsonString: String): AppData {
        var restoredAppData =
            Gson().fromJson(jsonString, AppData::class.java) ?: throw Exception("Error parsing saved app data")
        var validModels = mutableListOf<FileModel>()
        restoredAppData.models.forEach {
            try {
                val file = FileHelper.getSingleFile(it.path)
                if (file.exists()) {
                    Timber.v("File %s exists", file.path)
                    validModels.add(it)
                }
            } catch (e: FileNotFoundException) {
                Timber.d("We are in the catch block")
                Timber.e(e)
            }
        }

        if (restoredAppData.models.size > validModels.size) {
            Timber.w("Found invalid / nonexisting files - removing")
            restoredAppData = AppData(validModels, restoredAppData.settings)
        }
        return restoredAppData
    }
}