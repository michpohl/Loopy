package de.michaelpohl.loopy.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FileModelsList(
    val models: List<FileModel>
) : Parcelable
