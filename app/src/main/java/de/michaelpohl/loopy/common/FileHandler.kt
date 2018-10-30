package de.michaelpohl.loopy.common

import android.os.Environment
import hugo.weaving.DebugLog
import timber.log.Timber
import java.io.File

@DebugLog
class FileHandler {

    //TODO this is a constant one as a starting point. Improve handling this situaltion, please
    private val defaultFilesPath = Environment.getExternalStorageDirectory().toString()


    fun getFilesFromPath(path: String, showHiddenFiles: Boolean = false, onlyFolders: Boolean = false): List<File> {
        val file = File(path)
        Timber.d("path: %s, files:%s", path, file.listFiles())

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
