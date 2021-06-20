package com.michaelpohl.loopyplayer2.common

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.michaelpohl.loopyplayer2.model.AppStateRepository
import kotlinx.android.parcel.Parcelize
import kotlin.properties.Delegates

@JsonClass(generateAdapter = true)
@Parcelize
data class Settings(
    val acceptedFileTypes: MutableList<AppStateRepository.Companion.AudioFileType>,
    val isWaitMode: Boolean,
    val sampleRate: SampleRate,
    val showLoopCount: Boolean = true,
    val keepScreenOn: Boolean = false,
    val playInBackground: Boolean = true
) : Parcelable

class SettingsBuilder {
    var acceptedFileTypes: MutableList<AppStateRepository.Companion.AudioFileType> = mutableListOf()
    var isWaitMode by Delegates.notNull<Boolean>()
    var showLoopCount by Delegates.notNull<Boolean>()
    var keepScreenOn by Delegates.notNull<Boolean>()
    var playInBackground by Delegates.notNull<Boolean>()
    var sampleRate by Delegates.notNull<SampleRate>()
    fun addFileType(type: AppStateRepository.Companion.AudioFileType) {
        if (!acceptedFileTypes.contains(type)) acceptedFileTypes.add(type)
    }

    fun removeFileType(type: AppStateRepository.Companion.AudioFileType) {
        acceptedFileTypes.remove(type)
    }

    fun build(): Settings {
        return Settings(acceptedFileTypes, isWaitMode, sampleRate, showLoopCount, keepScreenOn, playInBackground)
    }
}


