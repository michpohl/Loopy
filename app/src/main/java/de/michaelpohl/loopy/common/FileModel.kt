package de.michaelpohl.loopy.common

import android.os.Parcelable
import de.michaelpohl.loopy.model.AppStateRepository
import kotlinx.android.parcel.Parcelize
import java.io.File

sealed class FileModel {

    data class File(
        val path: String,
        val name: String,
        val sizeInMB: Double,
        val extension: String = ""
    ) : FileModel()

    data class AudioFile(
        val path: String,
        val name: String,
        val sizeInMB: Double,
        val extension: String = ""
    ) : FileModel()

    data class Folder(
        val path: String,
        val name: String,
        val subFiles: Int = 0,
        val hasSubFolders: Boolean,
        val containsAudioFiles: Boolean
    ) : FileModel()
}

//@Parcelize
//data class File2(
//    val path: String,
//    val fileType: FileType,
//    val name: String,
//    val sizeInMB: Double,
//    val extension: String = "",
//    val subFiles: Int = 0,
//    val hasSubFolders: Boolean,
//    val containsAudioFiles: Boolean
//) : Parcelable {
//
//    fun isValidFileType(): Boolean {
//        return if (fileType == FileType.FILE) {
//            AppStateRepository.Companion.AudioFileType.values().any { name.endsWith(it.suffix) }
//        }
//        // Folders stay in the list
//        else true
//    }
//}







fun File.isFolder() : Boolean {
    return this.isDirectory
}

