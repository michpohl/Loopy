package com.michaelpohl.loopyplayer2.ui.settings

import com.michaelpohl.loopyplayer2.R

enum class AppSetting(val displayNameResource: Int?, val group: Int) {
    WAIT_MODE(R.string.settings_item_wait_until_finished, 0),
    FILE_TYPE_MP3(R.string.settings_item_allow_mp3, 1),
    FILE_TYPE_OGG(R.string.settings_item_allow_ogg, 1),
    FILE_TYPE_WAV(R.string.settings_item_allow_wav, 1),
    SAMPLE_RATE(R.string.settings_item_sample_rate, 2),
    COUNT_LOOPS(R.string.settings_item_show_loop_count, 3),
    KEEP_SCREEN_ON(R.string.settings_item_keep_screen_on, 3),
    PLAY_IN_BACKGROUND(R.string.settings_item_play_in_background, 3),
    NONE(null, -1)
}
