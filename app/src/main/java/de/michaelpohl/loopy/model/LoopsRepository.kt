package de.michaelpohl.loopy.model

import android.content.SharedPreferences
import com.google.gson.Gson
import de.michaelpohl.loopy.common.AppData
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.Settings
import de.michaelpohl.loopy.common.ValidAudioFileType

object LoopsRepository {

    private const val PREFS_LOOPS_KEY = "loops_list"
    lateinit var sharedPrefs: SharedPreferences
    lateinit var savedAppData: AppData

    var currentSelectedFileModels = listOf<FileModel>()
        private set
    private var newSelectedFileModels = listOf<FileModel>()
    var settings = Settings()

    /**
     * initializes the LoopsRepository by fetching the saved selectedState from sharedPreferences
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
            putString(PREFS_LOOPS_KEY, jsonString)
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
        //clear list to prevent adding doubles or unwanted items (since this gets updated with every click)
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
        //warnString is put as the defaultValue and is given if there's nothing to return from sharedPrefs
        //this is not the most sexy way to do it, butI'll go with it for now, need to learn how to first
        //TODO improve this
        val warnString = "warning"
        val jsonString = sharedPrefs.getString(PREFS_LOOPS_KEY, warnString)

        //TODO take the FileModelList and test its integrity (do the files still exist?)

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

    private fun appDataFromJson(jsonString: String): AppData {
        return Gson().fromJson(jsonString, AppData::class.java)
    }
}