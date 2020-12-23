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

    fun getSingleFile(path: String): File {
        return File(path)
    }

//    fun containsAudioFiles(path: String): Boolean {
//        var containsAudio = false
//
//        if (!isExcludedFolderName(path)) {
//            val filesToCheck: List<File> = getPathContent(path)
//            val foundFileModels: List<FileModel> =
//                filesToCheck.toFileModels().filter { it.fileType == FileType.FILE }
//
//            foundFileModels.forEach {
//                if (it.isValidFileType()) containsAudio = true
//            }
//        }
//        return containsAudio
//    }

    //TODO this method might still be slow with large file numbers. Do something about it (or limit its use)
    //TODO also it should be in FileModel, but I couldn't get it to work
//    fun containsAudioFilesInAnySubFolders(path: String): Boolean {
//        if (!path.isForbiddenFolderName()) {
//            val modelsToCheck = getPathContent(path).toFileModels()
//            return if (modelsToCheck.any { it is FileModel.AudioFile }) {
//                true
//            } else {
//                modelsToCheck
//                    .filter { it is FileModel.Folder }
//                    .map { containsAudioFilesInAnySubFolders((it as FileModel.Folder).path) }
//                    .contains(true)
//            }
//        }
//        return false
//    }

//    fun getSubFilesFor(
//        model: FileModel,
//        showHiddenFiles: Boolean = false,
//        onlyFolders: Boolean = false
//    ): List<File> {
//        return getPathContent(model.path, showHiddenFiles, onlyFolders)
//    }

    fun getPath(context: Context, uri: Uri): String? {

        var filePath: String? = null
        Timber.d("URI = %s", uri)
        if (uri != null && "content" == uri.scheme) {
            val cursor = context.contentResolver
                .query(
                    uri,
                    arrayOf(android.provider.MediaStore.Images.ImageColumns.DATA),
                    null,
                    null,
                    null
                )
            cursor?.let {
                cursor.moveToFirst()
                filePath = cursor.getString(0)
                cursor.close()
            }
        } else {
            filePath = uri.path
        }
        Timber.d(
            "Chosen path = %s", filePath!!
        )
        return filePath
    }
}

fun FileModel.AudioFile.toAudioModel(): AudioModel {

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

