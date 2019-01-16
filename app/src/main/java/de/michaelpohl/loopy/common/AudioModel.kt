package de.michaelpohl.loopy.common

import android.os.Parcelable
import de.michaelpohl.loopy.model.DataRepository
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.io.File

@Parcelize
data class AudioModel(
    val name: String,
    val id: Long,
    val album: String,
    val data: Int,
    val extension: String = "" //TODO how to get the filetype, since that is what it essential is
) : Parcelable
