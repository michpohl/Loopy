package de.michaelpohl.loopy.common

import java.io.File

object FileHelper {

    val excludedFolders = listOf("Android", "DCIM")

    fun getFilesFromPath(path: String, showHiddenFiles: Boolean = false, onlyFolders: Boolean = false): List<File> {
        val file = File(path)

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
        filesToReturn = allFiles.filter { it.isValidFileType() }


        return filesToReturn
    }

    fun getSingleFile(path: String): File {
        return File(path)
    }

    //TODO this method is slooow with large file numbers. Do something about it (or limit its use)
    //TODO also it should be in FileModel, but I couldn't get it to work
    fun containsAudioFilesInAnySubFolders(path: String): Boolean {
        var containsAudio = false

        if (!isExcludedFolderName(path)) {
                val filesToCheck: List<File> = getFilesFromPath(path)

                val foundFolderModels: List<FileModel> =
                    getFileModelsFromFiles(filesToCheck)
                        .filter { it.fileType == FileType.FOLDER }
                        .filter { !isExcludedFolderName(it.path) }
                val foundFileModels: List<FileModel> =
                    getFileModelsFromFiles(filesToCheck).filter { it.fileType == FileType.FILE }

                for (fileModel in foundFileModels) {
                    if (fileModel.isValidFileType()) containsAudio = true
                    return containsAudio
                }
                if (!foundFolderModels.isEmpty()) {
                    for (fileModel in foundFolderModels) {
                        if (containsAudioFilesInAnySubFolders(fileModel.path)) containsAudio = true
                    }
                }
            }

        return containsAudio
    }

    private fun convertFileSizeToMB(sizeInBytes: Long): Double {
        return (sizeInBytes.toDouble()) / (1024 * 1024)
    }

    fun isExcludedFolderName(path: String): Boolean {
        var result = false
        excludedFolders.forEach {
            if (path.endsWith(it)) result = true
        }
        return result
    }
}
