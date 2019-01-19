package de.michaelpohl.loopy.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Holds the informations to save in SavedInstanceState
 * At this point it allows to save both fileModels and audioModels
 * and - if not specified - places empty lists for them
 */

@Parcelize
data class AppData(
    val fileModels: List<FileModel> = listOf(),
    val audioModels: List<AudioModel> = listOf(),
    val settings: Settings
) : Parcelable
