package com.michaelpohl.loopyplayer2.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class FileModel : Parcelable {

    abstract val path: String
    abstract val name: String

    @Parcelize
    data class File(
        override val path: String,
        override val name: String,
        val sizeInMB: Double,
        val extension: String = ""
    ) : FileModel()

    @Parcelize
    data class AudioFile(
        override val path: String,
        override val name: String,
        val sizeInMB: Double,
        val extension: String = "",
        val isSelected: Boolean? = null
    ) : FileModel() {
        fun toAudioModel(): AudioModel {

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
    }

    @Parcelize
    data class Folder(
        override val path: String,
        override val name: String,
        val audioSubFiles: Int = 0,
        val hasSubFolders: Boolean,
        val containsAudioFiles: Boolean
    ) : FileModel()
}

