package de.michaelpohl.loopy.model

import android.content.SharedPreferences
import com.google.gson.Gson
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.AppData
import de.michaelpohl.loopy.common.Settings

object LoopsRepository {

    private const val PREFS_LOOPS_KEY = "loops_list"
    lateinit var sharedPrefs: SharedPreferences
    lateinit var savedAppData: AppData

    var currentSelectedFileModels = mutableListOf<FileModel>()
    private val newSelectedFileModels = mutableListOf<FileModel>()
    var settings = Settings()
        private set


    /**
     * initializes the LoopsRepository by fetching the saved state from sharedPreferences
     */
    fun init(sharedPrefs: SharedPreferences) {
        this.sharedPrefs = sharedPrefs
        this.savedAppData = loadSavedAppData()
        currentSelectedFileModels.addAll(savedAppData.models)
        settings = savedAppData.settings
    }

    fun saveCurrentSelection(selectedLoops: List<FileModel> = currentSelectedFileModels) {
        val jsonString = Gson().toJson(AppData(selectedLoops, settings))

//        TODO put fitting assertion
//        Assert.assertEquals(jsonString, """{"id":1,"description":"Test"}""")
        with(sharedPrefs.edit()) {
            putString(PREFS_LOOPS_KEY, jsonString)
            commit()
        }
        currentSelectedFileModels.clear()
        currentSelectedFileModels.addAll(selectedLoops)
    }

    fun updateAndSaveFileSelection(): Boolean {
        return if (!newSelectedFileModels.isEmpty()) {
            currentSelectedFileModels.addAll(newSelectedFileModels)
            saveCurrentSelection(currentSelectedFileModels)
            true
        } else false
    }

    fun onFileSelectionUpdated(newSelection: List<FileModel>) {

        //clear list to prevent adding doubles or unwanted items (since this gets updated with every click)
        newSelectedFileModels.clear()

        //only add the ones that are not already selected
        newSelectedFileModels.addAll(newSelection.filter { it ->
            !currentSelectedFileModels.contains(it)
        })
    }

    fun onLoopsListCleared() {
        currentSelectedFileModels.clear()
        saveCurrentSelection()
    }

    private fun loadSavedAppData(): AppData {
        //warnString is put as the defaultValue and is given if there's nothing to return from sharedPrefs
        //this is not the most sexy way to do it, butI'll go with it for now, need to learn how to first
        //TODO improve this
        val warnString = "warning"
        val jsonString = sharedPrefs.getString(PREFS_LOOPS_KEY, warnString)

        //TODO take the FileModelList and test its integrity (do the files still exist?)

        return if (jsonString != "warning") {
            AppDataFromJson(jsonString)
        } else AppData(arrayListOf(), Settings())
    }

    private fun AppDataFromJson(jsonString: String): AppData {
        return Gson().fromJson(jsonString, AppData::class.java)
    }
}