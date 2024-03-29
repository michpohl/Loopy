package com.michaelpohl.loopyplayer2.model

import com.michaelpohl.loopyplayer2.common.toFileModel
import com.michaelpohl.shared.AudioModel
import com.michaelpohl.shared.FileModel
import com.squareup.moshi.JsonClass
import java.io.File

@JsonClass(generateAdapter = true)
data class Sets(val loopSets: List<LoopSet>)

@JsonClass(generateAdapter = true)
data class LoopSet(val name: String, val loops: List<Loop>) {

    fun toAudioModels(): List<AudioModel> {
        return loops
            .map { File(it.fileName).toFileModel() }
            .filterIsInstance<FileModel.AudioFile>()
            .map { it.toAudioModel() }
    }
}

@JsonClass(generateAdapter = true)
data class Loop(val fileName: String)

