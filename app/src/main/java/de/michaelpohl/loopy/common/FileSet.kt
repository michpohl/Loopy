package de.michaelpohl.loopy.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FileSet(
    val models: List<FileModel>
) : Parcelable
