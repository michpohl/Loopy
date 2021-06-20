package com.michaelpohl.loopyplayer2.common

import com.michaelpohl.loopyplayer2.model.ExternalStorageManager
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



