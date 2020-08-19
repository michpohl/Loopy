package de.michaelpohl.loopy.common

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
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

    @IgnoredOnParcel
    val displayName = {
        val pieces = name.split("/")
        val length = pieces.size
        pieces[length - 2]
    }
}

//fileModel.name.split(".")[0]
