package de.michaelpohl.loopy.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Settings (
    var allowedFileTypes: Array<ValidAudioFileType> = emptyArray(),
    var switchingLoopsBehaviour: SwitchingLoopsBehaviour = SwitchingLoopsBehaviour.SWITCH
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Settings

        if (!allowedFileTypes.contentEquals(other.allowedFileTypes)) return false

        return true
    }

    override fun hashCode(): Int {
        return allowedFileTypes.contentHashCode()
    }
}

