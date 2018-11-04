package de.michaelpohl.loopy.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

//remember this smart solution to get a parcelable from any data class!!
// annotate in the front and implement in the back
@Parcelize
data class FileModel(
    val path: String,
    val fileType: FileType,
    val name: String,
    val sizeInMB: Double,
    val extension: String = "",
    val subFiles: Int = 0
) : Parcelable {

    fun getSubFiles(showHiddenFiles: Boolean = false, onlyFolders: Boolean = false): List<File> {
        return FileHelper.getFilesFromPath(path, showHiddenFiles, onlyFolders)
    }

    fun isValidFileType(): Boolean {

        //filtering only for .wav files for now
        // in the future there should be an enum "allowedExtensions" or so
        if (fileType == FileType.FILE) {
            return name.endsWith("wav")
        }
        // Folders stay in the list
        return true
    }

    fun hasSubFolders(): Boolean {
        var hasSubFolders = false
        val filesToCheck: List<File> = getSubFiles()

        val foundFolderModels: List<FileModel> =
            FileHelper.getFileModelsFromFiles(filesToCheck).filter { it.fileType == FileType.FOLDER }

        if (!foundFolderModels.isEmpty()) hasSubFolders = true

        return hasSubFolders
    }

    fun containsAudioFiles(): Boolean {
        val filesToCheck: List<File> = getSubFiles()
        if (FileHelper.getFileModelsFromFiles(filesToCheck).isEmpty()) {
            return false
        }
        return true
    }
}
