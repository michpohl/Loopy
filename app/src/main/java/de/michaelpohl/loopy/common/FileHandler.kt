package de.michaelpohl.loopy.common

import hugo.weaving.DebugLog
import timber.log.Timber
import java.io.File

@DebugLog
class FileHandler {

    fun getFilesFromPath(path: String, showHiddenFiles: Boolean = false, onlyFolders: Boolean = false): List<File> {
        val file = File(path)
        Timber.d("path: %s, files:%s", path, file.listFiles())

        return file.listFiles()
            .filter { showHiddenFiles || !it.name.startsWith(".") }
            .filter { !onlyFolders || it.isDirectory }
            .toList()
    }

    fun getFileModelsFromFiles(files: List<File>): List<FileModel> {
        var filesToReturn: List<FileModel>
        val allFiles: List<FileModel> = files.map {
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
        filesToReturn = allFiles.filter { isValidFileType(it) }


        return filesToReturn
    }

    fun convertFileSizeToMB(sizeInBytes: Long): Double {
        return (sizeInBytes.toDouble()) / (1024 * 1024)
    }

    fun isValidFileType(fileModel: FileModel): Boolean {

        //filtering for .wav files for now
        if (fileModel.fileType == FileType.FILE) {
            return fileModel.name.endsWith("wav")
        }
        // Folders stay in the list
        return true
    }

    fun containsAudioFiles(path: String): Boolean {
        var filesToCheck: List<File> = getFilesFromPath(path)
        if (getFileModelsFromFiles(filesToCheck).isEmpty()) {
            return false
        }
        return true
    }
}
