package com.michaelpohl.loopyplayer2.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//@JsonClass(generateAdapter = true)
@Parcelize
data class AudioModel(
    val name: String,
    val id: Long = 0,
    val album: String,
    val path: String,
    val fileExtension: String = "unknown",
    val isMediaStoreItem: Boolean = true
) : Parcelable {

    val displayName: String
        get() {
            val filename = path.subSequence(path.lastIndexOf("/") + 1, path.lastIndex)
            return filename.substring(0, filename.lastIndexOf("."))
        }
}
