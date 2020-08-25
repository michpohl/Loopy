package de.michaelpohl.loopy.model

import com.squareup.moshi.JsonClass
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.toAudioModel
import de.michaelpohl.loopy.common.toFileModel
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

