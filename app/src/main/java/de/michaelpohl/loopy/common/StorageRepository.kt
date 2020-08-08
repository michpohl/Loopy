package de.michaelpohl.loopy.common

import android.content.Context
import android.net.Uri
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.model.ExternalStorageManager
import timber.log.Timber
import java.io.File

class StorageRepository(val storage: ExternalStorageManager) {

    private val excludedFolders = listOf("Android", "DCIM")

    fun getPathContent(path: String, showHiddenFiles: Boolean = false, onlyFolders: Boolean = false): List<File> {
        return storage.getPathContent(path, showHiddenFiles, onlyFolders)
    }

    fun getFileModelsFromFiles(files: List<File>): List<FileModel> {
        var filesToReturn: List<FileModel>
        val allFiles: List<FileModel> = files.map {
            val subFiles = it.listFiles()
            FileModel(
                it.path,
                FileType.getFileType(it),
                it.name,
                it.length().convertFileSizeToMB(),
                it.extension,
                subFiles.size,
                subFiles.any { it.isDirectory },
                subFiles.any { it.isValidAudioFile() }
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
            val filesToCheck: List<File> = getPathContent(path)
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
            val filesToCheck: List<File> = getPathContent(path)

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

    fun isExcludedFolderName(path: String): Boolean {
        var result = false
        excludedFolders.forEach {
            if (path.endsWith(it)) result = true
        }
        return result
    }

    fun getSubFilesFor(model: FileModel, showHiddenFiles: Boolean = false, onlyFolders: Boolean = false): List<File> {
        return getPathContent(model.path, showHiddenFiles, onlyFolders)
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
}

fun FileModel.toAudioModel(): AudioModel {

    // this just takes the last containing folder and assumes it is the album name
    // this might be wrong if there is metainformation stored in the file. We'll see
    val albumNameFromFolder: () -> String = {
        val pathPieces = this.path.split("/")
        val length = pathPieces.size
        pathPieces[length - 2]
    }
    return AudioModel(
        name = this.path, //throw away file extension from name
        album = albumNameFromFolder(),
        path = this.path,
        fileExtension = this.extension,
        isMediaStoreItem = false
    )
}

fun File.isValidAudioFile(): Boolean {
    val extension = this.extension
    AppStateRepository.Companion.AudioFileType.values().forEach {
        if (it.suffix == extension) return true
    }
    return false
}
