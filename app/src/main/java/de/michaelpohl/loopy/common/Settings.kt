package de.michaelpohl.loopy.common

import com.squareup.moshi.JsonClass
import de.michaelpohl.loopy.model.AppStateRepository
import kotlin.properties.Delegates

// TODO replace with immutable concept
@JsonClass(generateAdapter = true)
data class Settings(
    var acceptedFileTypes: MutableList<AppStateRepository.Companion.AudioFileType>,
    var isWaitMode: Boolean,
    var showLoopCount: Boolean = true,
    var keepScreenOn: Boolean = false,
    var playInBackground: Boolean = true
)

class SettingsBuilder {
    var acceptedFileTypes: MutableList<AppStateRepository.Companion.AudioFileType> = mutableListOf()
    var isWaitMode by Delegates.notNull<Boolean>()
    var showLoopCount by Delegates.notNull<Boolean>()
    var keepScreenOn by Delegates.notNull<Boolean>()
    var playInBackground by Delegates.notNull<Boolean>()

    fun addFileType(type: AppStateRepository.Companion.AudioFileType) {
        if (!acceptedFileTypes.contains(type)) acceptedFileTypes.add(type)
    }

    fun removeFileType(type: AppStateRepository.Companion.AudioFileType) {
        acceptedFileTypes.remove(type)
    }

    fun build() : Settings {
        return Settings(acceptedFileTypes, isWaitMode, showLoopCount, keepScreenOn, playInBackground)
    }
}


