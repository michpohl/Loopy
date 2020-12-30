package de.michaelpohl.loopy.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.FileDescriptor

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
    ) : FileModel()

    @Parcelize
    data class Folder(
        override val path: String,
        override val name: String,
        val audioSubFiles: Int = 0,
        val hasSubFolders: Boolean,
        val containsAudioFiles: Boolean
    ) : FileModel()
}

