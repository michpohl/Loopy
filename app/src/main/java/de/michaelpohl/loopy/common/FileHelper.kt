package de.michaelpohl.loopy.common

import timber.log.Timber
import java.io.File

object FileHelper {

    fun getFilesFromPath(path: String, showHiddenFiles: Boolean = false, onlyFolders: Boolean = false): List<File> {
        val file = File(path)
        Timber.d("path: %s, files:%s", path, file.listFiles())

        if (file.listFiles() == null) {
            return arrayListOf()
        }

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

    fun getSingleFile(path: String): File {
        return File(path)
    }

    fun convertFileSizeToMB(sizeInBytes: Long): Double {
        return (sizeInBytes.toDouble()) / (1024 * 1024)
    }

    fun isValidFileType(fileModel: FileModel): Boolean {

        //filtering only for .wav files for now
        // in the future there should be an enum "allowedExtensions" or so
        if (fileModel.fileType == FileType.FILE) {
            return fileModel.name.endsWith("wav")
        }
        // Folders stay in the list
        return true
    }

    fun hasSubFolders(path: String): Boolean {
        var hasSubFolders = false
        val filesToCheck: List<File> = getFilesFromPath(path)

        val foundFolderModels: List<FileModel> =
            getFileModelsFromFiles(filesToCheck).filter { it.fileType == FileType.FOLDER }

        if (!foundFolderModels.isEmpty()) hasSubFolders = true

        return hasSubFolders
    }

    fun containsAudioFilesInAnySubFolders(path: String): Boolean {
        var containsAudio = false
        val filesToCheck: List<File> = getFilesFromPath(path)

        val foundFolderModels: List<FileModel> =
            getFileModelsFromFiles(filesToCheck).filter { it.fileType == FileType.FOLDER }
        val foundFileModels: List<FileModel> =
            getFileModelsFromFiles(filesToCheck).filter { it.fileType == FileType.FILE }

        for (fileModel in foundFileModels) {
            if (isValidFileType(fileModel)) containsAudio = true
        }
        if (!foundFolderModels.isEmpty()) {
            for (fileModel in foundFolderModels) {
                if (containsAudioFilesInAnySubFolders(fileModel.path)) containsAudio = true
            }
        }
        return containsAudio
    }

    fun containsAudioFiles(path: String): Boolean {
        val filesToCheck: List<File> = getFilesFromPath(path)
        if (getFileModelsFromFiles(filesToCheck).isEmpty()) {
            return false
        }
        return true
    }
}
