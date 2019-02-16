package de.michaelpohl.loopy.common

import android.content.Context
import android.net.Uri
import timber.log.Timber
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

    fun containsAudioFiles(path: String): Boolean {
        var containsAudio = false

        if (!isExcludedFolderName(path)) {
            val filesToCheck: List<File> = getFilesFromPath(path)
            val foundFileModels: List<FileModel> =
                getFileModelsFromFiles(filesToCheck).filter { it.fileType == FileType.FILE }

            foundFileModels.forEach {
                if (it.isValidFileType()) containsAudio = true
            }
        }
        return containsAudio
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

    fun getPath(context: Context, uri: Uri): String? {

        var filePath: String? = null
        Timber.d("URI = %s", uri)
        if (uri != null && "content" == uri.scheme) {
            val cursor = context.contentResolver
                .query(uri, arrayOf(android.provider.MediaStore.Images.ImageColumns.DATA), null, null, null)
            cursor.moveToFirst()
            filePath = cursor.getString(0)
            cursor.close()
        } else {
            filePath = uri.path
        }
        Timber.d(
            "Chosen path = %s", filePath!!
        )
        return filePath
    }

    fun fileModelToAudioModel(fileModel: FileModel) : AudioModel {

        // this just takes the last containing folder and assues it is the album name
        // this might be wrong if there is metainformation stored in the file. We'll see
        val albumNameFromFolder: () -> String = {
            val pathPieces = fileModel.path.split("/")
            val length = pathPieces.size
            pathPieces[length - 2]
        }

        return AudioModel(
            name = fileModel.name.split(".")[0], //throw away file extension from name
            album = albumNameFromFolder(),
            path = fileModel.path,
            fileExtension = fileModel.extension,
            isMediaStoreItem = false
        )
    }
}