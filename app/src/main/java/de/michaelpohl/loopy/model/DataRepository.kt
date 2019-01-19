package de.michaelpohl.loopy.model

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.google.gson.Gson
import de.michaelpohl.loopy.common.*
import timber.log.Timber
import java.io.File

object DataRepository {

    private const val PREFS_LOOPY_KEY = "loops_list"
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var savedAppData: AppData

    var currentSelectedAudioModels = listOf<AudioModel>()
        private set
    private var newSelectedAudioModels = listOf<AudioModel>()
    var settings = Settings()

    /**
     * initializes the DataRepository by fetching the saved selectedState from sharedPreferences
     */
    fun init(sharedPrefs: SharedPreferences) {
        this.sharedPrefs = sharedPrefs
        this.savedAppData = loadSavedAppData()
        currentSelectedAudioModels = savedAppData.audioModels
        this.settings = savedAppData.settings
    }

    fun saveCurrentState(
        selectedLoops: List<AudioModel> = this.currentSelectedAudioModels,
        settings: Settings = this.settings
    ) {
        val jsonString = Gson().toJson(
            AppData(
                audioModels = selectedLoops,
                settings = settings
            )
        )
//        TODO put fitting assertion
//        Assert.assertEquals(jsonString, """{"id":1,"description":"Test"}""")
        with(sharedPrefs.edit()) {
            putString(PREFS_LOOPY_KEY, jsonString)
            apply() //writes the data in the background, as opposed to commit()
        }
    }

    fun updateAndSaveFileSelection(): Boolean {
        return if (newSelectedAudioModels.isNotEmpty()) {
            currentSelectedAudioModels = newSelectedAudioModels + currentSelectedAudioModels

            saveCurrentState()
            true
        } else false
    }

    fun onFileModelSelectionUpdated(newFileModelSelection: List<FileModel>) {
        //TODO do something right now FileBrowser is detached!!
    }

    fun onAudioFileSelectionUpdated(newSelection: List<AudioModel>) {
        //only add the ones that are not already selected
        newSelectedAudioModels = newSelection.filter { it ->
            !currentSelectedAudioModels.contains(it)
        }
    }

    fun onLoopsListCleared() {
        currentSelectedAudioModels = emptyList()
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
            AppData(settings = Settings(allowedFileTypes = ValidAudioFileType.values()))
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

    fun testIntegrity(audioModels: List<AudioModel>): List<AudioModel> {
        var validModels = mutableListOf<AudioModel>()
        audioModels.forEach {
            //TODO revisit this, how to validate??
//            val file = FileHelper.getSingleFile(it.path)
//            if (file.exists()) {
//                Timber.v("File %s exists", file.path)
            validModels.add(it)
//            } else {
//                Timber.v("File %s doesn't exist, removing", file.path)
//            }
        }
        return validModels
    }

    fun handleFileFromIntent(file: File) {
        //TODO repair
//        onAudioFileSelectionUpdated(FileHelper.getAudioModelsFromFiles(listOf(file)))
    }

    fun getAlbumTitles(context: Context): MutableList<String> {
        val list: MutableList<String> = mutableListOf()

        val projection =
            arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS
            )

        val selection: String? = null
        val selectionArgs: Array<String>? = null
        val sortOrder = MediaStore.Audio.Media.ALBUM + " ASC"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        if (cursor.moveToFirst()) {
            val albumTitle = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
            do {
                list.add(cursor.getString(albumTitle))
            } while (cursor.moveToNext())
        }
        return list
    }

    fun getMediaStoreEntries(context: Context): MutableList<AudioModel> {
        val list: MutableList<AudioModel> = mutableListOf()

        // Get the external storage media store audio uri
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        //val uri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI

        // IS_MUSIC : Non-zero if the audio file is music
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"

        // Sort the musics
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        //val sortOrder = MediaStore.Audio.Media.TITLE + " DESC"

        // Query the external storage for music files
        val cursor: Cursor = context.contentResolver.query(
            uri,
            null,
            selection,
            null,
            sortOrder
        )

        //TODO total renaming for niceness
        if (cursor.moveToFirst()) {
            val id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
//            val fileUri = cursor.getColumnIndex.(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            val data: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val data2 = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            val album2 = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            do {
                val audioId: Long = cursor.getLong(id)
                val audioTitle: String = cursor.getString(title)
                val albumName: String = cursor.getString(album2)
                val albumData = cursor.getString(data)
                Timber.d("Music file: %s, %s, %s", audioId, audioTitle, albumName)

                list.add(AudioModel(audioTitle, audioId, albumName, albumData))
            } while (cursor.moveToNext())
        }
        return list
    }

    private fun appDataFromJson(jsonString: String): AppData {
        var restoredAppData =
            Gson().fromJson(jsonString, AppData::class.java) ?: throw Exception("Error parsing saved app data")
        var validModels = mutableListOf<AudioModel>()
        restoredAppData.audioModels.forEach {
            //TODO this is the same as in the validate method! Not necessary
//            try {
//                val file = FileHelper.getSingleFile(it.path)
//                if (file.exists()) {
//                    Timber.v("File %s exists", file.path)
            validModels.add(it)
        }
//            } catch (e: FileNotFoundException) {
//                Timber.d("We are in the catch block")
//                Timber.e(e)
//            }
//        }

        if (restoredAppData.fileModels.size > validModels.size) {
            Timber.w("Found invalid / nonexisting files - removing")
            restoredAppData = AppData(
                audioModels = validModels,
                settings = restoredAppData.settings
            )
        }
        return restoredAppData
    }
}