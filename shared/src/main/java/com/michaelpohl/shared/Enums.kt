package com.michaelpohl.loopyplayer2.common

import com.squareup.moshi.JsonClass

enum class PlayerState {
    PLAYING, PAUSED, STOPPED, UNKNOWN, READY
}

@JsonClass(generateAdapter = false)
enum class SampleRate(val intValue: Int, val displayName: String) {

    RATE_44_KHZ(44100, "44.1KHz"), RATE_48_KHZ(48000, "48KHz"), RATE_96_KHZ(96000, "96KHz")
}

fun String.toSampleRate(): SampleRate {
    SampleRate.values().find { it.displayName == this }?.let {
        return it
    } ?: error("Cannot convert this string to a SampleRate enum value")
}

