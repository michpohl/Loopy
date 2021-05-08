package de.michaelpohl.loopy.common

import de.michaelpohl.loopy.model.ExternalStorageManager
import java.io.File

class StorageRepository(val storage: ExternalStorageManager) {

    fun getPathContent(
        path: String,
        showHiddenFiles: Boolean = false,
        onlyFolders: Boolean = false
    ): List<File> {
        return storage.getPathContent(path, showHiddenFiles, onlyFolders)
    }
}



