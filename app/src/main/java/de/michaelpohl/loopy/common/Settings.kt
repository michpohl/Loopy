package de.michaelpohl.loopy.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Settings (
    var allowedFileTypes: MutableList<ValidAudioFileType> = arrayListOf(),
    var switchingLoopsBehaviour: SwitchingLoopsBehaviour = SwitchingLoopsBehaviour.SWITCH
) : Parcelable

