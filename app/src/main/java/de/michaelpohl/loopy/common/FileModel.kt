package de.michaelpohl.loopy.common

import android.os.Parcelable
import de.michaelpohl.loopy.model.DataRepository
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
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

    //TODO beautify this into an Enum or so
    private var supportedFileTypes = listOf("wav", "mp3", "ogg")

    fun getSubFiles(showHiddenFiles: Boolean = false, onlyFolders: Boolean = false): List<File> {
        return FileHelper.getFilesFromPath(path, showHiddenFiles, onlyFolders)
    }

    fun isValidFileType(): Boolean {
        if (fileType == FileType.FILE) {
            var isValid = false

            DataRepository.settings.allowedFileTypes.forEach {
//                Timber.d("Testing for: %s", it.suffix)
                if (name.endsWith(it.suffix)) {
                    isValid = true
//                    Timber.d("This is a valid audio file: %s, %s", name, it.suffix)
                } else {
//                    Timber.d("This is not a valid audio file: %s, %s", name, it.suffix)
                }
            }
            return isValid
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
