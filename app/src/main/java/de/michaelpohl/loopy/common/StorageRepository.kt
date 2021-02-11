package de.michaelpohl.loopy.common

import android.content.Context
import android.net.Uri
import de.michaelpohl.loopy.model.ExternalStorageManager
import timber.log.Timber
import java.io.File

class StorageRepository(val storage: ExternalStorageManager) {

    private val excludedFolders = listOf("Android", "DCIM")
    fun getPathContent(
        path: String,
        showHiddenFiles: Boolean = false,
        onlyFolders: Boolean = false
    ): List<File> {
        return storage.getPathContent(path, showHiddenFiles, onlyFolders)
    }

}



