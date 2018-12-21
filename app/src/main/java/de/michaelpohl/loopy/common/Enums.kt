package de.michaelpohl.loopy.common

enum class ValidAudioFileType(val suffix : String) {
    WAVE("wav"),
    MP3("mp3"),
    OGG("ogg")
}

enum class SwitchingLoopsBehaviour {
    SWITCH, WAIT
}