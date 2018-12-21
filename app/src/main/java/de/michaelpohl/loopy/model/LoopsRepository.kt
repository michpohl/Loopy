package de.michaelpohl.loopy.model

import android.content.SharedPreferences
import com.google.gson.Gson
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileModelsList

object LoopsRepository {

    private const val PREFS_LOOPS_KEY = "loops_list"
    lateinit var sharedPrefs: SharedPreferences

    val currentSelectedFileModels = mutableListOf<FileModel>()
    private val newSelectedFileModels = mutableListOf<FileModel>()

    var wavAllowed = true
    var mp3Allowed = true
    var oggAllowed = true

    /**
     * initializes the LoopsRepository by fetching the saved state from sharedPreferences
     */
    fun init(sharedPrefs: SharedPreferences) {
        this.sharedPrefs = sharedPrefs
        currentSelectedFileModels.addAll(loadSavedSelection().models)
    }

    fun saveCurrentSelection(list: FileModelsList = FileModelsList(currentSelectedFileModels)) {
        val jsonString = Gson().toJson(list)

//        TODO put fitting assertion
//        Assert.assertEquals(jsonString, """{"id":1,"description":"Test"}""")

        with(sharedPrefs.edit()) {
            putString(PREFS_LOOPS_KEY, jsonString)
            commit()
        }
    }

    fun updateAndSaveFileSelection(): Boolean {
        return if (!newSelectedFileModels.isEmpty()) {
            currentSelectedFileModels.addAll(newSelectedFileModels)
            saveCurrentSelection(FileModelsList(currentSelectedFileModels))
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

    private fun loadSavedSelection(): FileModelsList {
        //warnString is put as the defaultValue and is given if there's nothing to return from sharedPrefs
        //this is not the most sexy way to do it, butI'll go with it for now, need to learn how to first
        //TODO improve this
        val warnString = "warning"
        val jsonString = sharedPrefs.getString(PREFS_LOOPS_KEY, warnString)

        //TODO take the FileModelList and test its integrity (do the files still exist?)

        return if (jsonString != "warning") {
            fileModelsListFromJson(jsonString)
        } else FileModelsList(arrayListOf())
    }

    private fun fileModelsListFromJson(jsonString: String): FileModelsList {
        return Gson().fromJson(jsonString, FileModelsList::class.java)
    }
}