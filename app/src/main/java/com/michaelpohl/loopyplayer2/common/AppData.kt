package com.michaelpohl.loopyplayer2.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Holds the informations to save in SavedInstanceState
 * At this point it allows to save both fileModels and audioModels
 * and - if not specified - places empty lists for them
 */

@Deprecated("in this form we won't use this anymore")
@Parcelize
data class AppData(
    @JvmField val fileModels: @RawValue() List<FileModel> = listOf(),
    @JvmField val audioModels: List<AudioModel> = listOf(),
    @JvmField val settings: @RawValue() Settings
) : Parcelable
