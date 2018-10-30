package de.michaelpohl.loopy.common

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//remember this smart solution to get a parcelable from any data class!!
// annotate in the front and implement in the back
@Parcelize
data class FileModel(
    val path: String,
    val fileType: FileType,
    val name: String,
    val sizeInMB: Double,
    val extension: String = "",
    val subFiles: Int = 0
) : Parcelable
