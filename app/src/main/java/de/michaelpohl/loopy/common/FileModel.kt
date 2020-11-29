package de.michaelpohl.loopy.common

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
        val extension: String = ""
    ) : FileModel()

    @Parcelize
    data class Folder(
        override val path: String,
        override val name: String,
        val subFiles: Int = 0,
        val hasSubFolders: Boolean,
        val containsAudioFiles: Boolean
    ) : FileModel()
}

