package de.michaelpohl.loopy.model

import android.content.SharedPreferences
import com.google.gson.Gson
import de.michaelpohl.loopy.common.AppData
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.Settings
import hugo.weaving.DebugLog
import timber.log.Timber

@DebugLog
object LoopsRepository {

    private const val PREFS_LOOPS_KEY = "loops_list"
    lateinit var sharedPrefs: SharedPreferences
    lateinit var savedAppData: AppData

    var currentSelectedFileModels = listOf<FileModel>()
        private set
    private var newSelectedFileModels = listOf<FileModel>()
    var settings = Settings()

    /**
     * initializes the LoopsRepository by fetching the saved state from sharedPreferences
     */
    fun init(sharedPrefs: SharedPreferences) {
        this.sharedPrefs = sharedPrefs
        this.savedAppData = loadSavedAppData()
        currentSelectedFileModels = savedAppData.models
        this.settings = savedAppData.settings
        Timber.d("Loaded these allowed fileTypes:")
        if (settings.allowedFileTypes.isEmpty()) Timber.d("No allowed types!")
        settings.allowedFileTypes.forEach { Timber.d("%s", it) }

    }

    fun saveCurrentState(selectedLoops: List<FileModel> = this.currentSelectedFileModels, settings: Settings = this.settings) {
        Timber.d("Saving settings, allowedFilesTypes: ")
        settings.allowedFileTypes.forEach { Timber.d("%s", it.suffix) }

        val jsonString = Gson().toJson(AppData(selectedLoops, settings))

//        TODO put fitting assertion
//        Assert.assertEquals(jsonString, """{"id":1,"description":"Test"}""")
        with(sharedPrefs.edit()) {
            putString(PREFS_LOOPS_KEY, jsonString)
            commit()
        }
    }

    fun updateAndSaveFileSelection(): Boolean {
        return if (newSelectedFileModels.isNotEmpty()) {
            Timber.d("it's not empty!!, it contains:")
            for (model in newSelectedFileModels) Timber.d("selected: %s", model.name)
            for (model in currentSelectedFileModels) Timber.d(" current before: %s", model.name)

            currentSelectedFileModels = newSelectedFileModels + currentSelectedFileModels
            for (model in currentSelectedFileModels) Timber.d("current after: %s", model.name)

            saveCurrentState()
            true
        } else false
    }

    fun onFileSelectionUpdated(newSelection: List<FileModel>) {
        Timber.d("adding %s selected items", newSelection.size)
        //clear list to prevent adding doubles or unwanted items (since this gets updated with every click)
        Timber.d("%s", newSelectedFileModels)
        //only add the ones that are not already selected
        newSelectedFileModels = newSelection.filter { it ->
            !currentSelectedFileModels.contains(it)
        }
        Timber.d("%s", newSelectedFileModels)
    }

    fun onLoopsListCleared() {
        currentSelectedFileModels = emptyList()
        saveCurrentState()
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

    fun getAllowedFileTypeListAsString(): String {
        val builder = StringBuilder()
        val allowedFileTypes = settings.allowedFileTypes
        allowedFileTypes.forEach {
            builder.append(it.name)
            if (allowedFileTypes.indexOf(it) == allowedFileTypes.size - 1) {
                builder.append(", ")
            }
        }
        return builder.toString()
    }

    private fun AppDataFromJson(jsonString: String): AppData {
        return Gson().fromJson(jsonString, AppData::class.java)
    }
}