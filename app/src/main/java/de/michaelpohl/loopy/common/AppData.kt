package de.michaelpohl.loopy.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppData(
    val models: List<FileModel>,
    val settings: Settings
) : Parcelable
