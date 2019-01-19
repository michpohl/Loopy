package de.michaelpohl.loopy.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AudioModel(
    val name: String,
    val id: Long,
//    val uri: Uri,
    val album: String,
    val data: String,
    val extension: String = "" //TODO how to get the filetype, since that is what it essential is
) : Parcelable
