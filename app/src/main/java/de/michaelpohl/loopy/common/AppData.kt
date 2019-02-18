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
    @JvmField val fileModels: List<FileModel> = listOf(),
    @JvmField val audioModels: List<AudioModel> = listOf(),
    @JvmField val settings: Settings
) : Parcelable
