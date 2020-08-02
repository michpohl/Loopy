package de.michaelpohl.loopy.common

import com.squareup.moshi.JsonClass
import de.michaelpohl.loopy.model.AppStateRepository

// TODO replace with immutable concept
@JsonClass(generateAdapter = true)
data class Settings(
    var acceptedFileTypes: MutableList<AppStateRepository.Companion.AudioFileType>,
    var isWaitMode: Boolean,
    var showLoopCount: Boolean = true,
    var keepScreenOn: Boolean = false,
    var playInBackground: Boolean = true
)


