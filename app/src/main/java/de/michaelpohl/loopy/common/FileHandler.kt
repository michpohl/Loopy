package de.michaelpohl.loopy.common

import hugo.weaving.DebugLog
import timber.log.Timber
import java.io.File

@DebugLog
class FileHandler {

    fun getFilesFromPath(path: String, showHiddenFiles: Boolean = false, onlyFolders: Boolean = false): List<File> {
        Timber.d("path: %s, files: not yet", path)
        val file = File(path)

        return file.listFiles()
            .filter { showHiddenFiles || !it.name.startsWith(".") }
            .filter { !onlyFolders || it.isDirectory }
            .toList()
    }

    fun getFileModelsFromFiles(files: List<File>): List<FileModel> {
        return files.map {
            FileModel(
                it.path,
                FileType.getFileType(it),
                it.name,
                convertFileSizeToMB(it.length()),
                it.extension,
                it.listFiles()?.size
                    ?: 0
            )
        }
    }

    fun convertFileSizeToMB(sizeInBytes: Long): Double {
        return (sizeInBytes.toDouble()) / (1024 * 1024)
    }

}
