package de.michaelpohl.loopy.common

import android.os.Parcelable
import de.michaelpohl.loopy.model.AppStateRepository
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class FileModel(
    val path: String,
    val fileType: FileType,
    val name: String,
    val sizeInMB: Double,
    val extension: String = "",
    val subFiles: Int = 0,
    val hasSubFolders: Boolean,
    val containsAudioFiles: Boolean
) : Parcelable {

    fun isValidFileType(): Boolean {
        if (fileType == FileType.FILE) {
            var isValid = false

            AppStateRepository.Companion.AudioFileType.values().forEach {
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
}

fun List<File>.toFileModels(): List<FileModel> {
    val allFiles: List<FileModel> = this.map {
        FileModel(
            it.path,
            FileType.getFileType(it),
            it.name,
            it.length().convertFileSizeToMB(),
            it.extension,
            it.listFiles()?.size
                ?: 0
        )
    }
    return allFiles.filter { it.isValidFileType() }
}

