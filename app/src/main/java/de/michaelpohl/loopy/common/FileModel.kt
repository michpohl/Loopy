package de.michaelpohl.loopy.common

import java.io.File

sealed class FileModel {

    abstract val path: String
    abstract val name: String

    data class File(
        override val path: String,
        override val name: String,
        val sizeInMB: Double,
        val extension: String = ""
    ) : FileModel()

    data class AudioFile(
        override val path: String,
        override val name: String,
        val sizeInMB: Double,
        val extension: String = ""
    ) : FileModel()

    data class Folder(
        override val path: String,
        override val name: String,
        val subFiles: Int = 0,
        val hasSubFolders: Boolean,
        val containsAudioFiles: Boolean
    ) : FileModel()
}

