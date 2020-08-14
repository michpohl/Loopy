package de.michaelpohl.loopy.common

import android.os.Parcelable
import de.michaelpohl.loopy.model.AppStateRepository
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
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
        return if (fileType == FileType.FILE) {
            AppStateRepository.Companion.AudioFileType.values().any { name.endsWith(it.suffix) }
        }
        // Folders stay in the list
        else true
    }
}

fun List<File>.toFileModels(): List<FileModel> {
    return this.map { file ->
        val subFiles = file.listFiles() ?: arrayOf()
        Timber.d("Subfiles: $subFiles")
        FileModel(
            file.path,
            FileType.getFileType(file),
            file.name,
            file.length().convertFileSizeToMB(),
            file.extension,
            subFiles.size,
            subFiles.any { it.isDirectory },
            subFiles.any { it.isValidAudioFile() }
        )
    }.filter { it.isValidFileType() }
}

