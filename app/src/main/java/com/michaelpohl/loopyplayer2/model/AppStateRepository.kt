package com.michaelpohl.loopyplayer2.model

import com.michaelpohl.loopyplayer2.common.SampleRate
import com.michaelpohl.loopyplayer2.common.Settings

class AppStateRepository(private val sharedPrefs: SharedPreferencesManager) {

    var settings: Settings = sharedPrefs.getSettings() ?: createDefaultSettings()
        set(value) {
            if (field != value) {
                sharedPrefs.saveSettings(value)
                field = value
            }
        }

    var isSetupComplete: Boolean
        get() {
            return sharedPrefs.getBoolean(APP_SETUP_COMPLTE)
        }
        set(value) {
            sharedPrefs.putBoolean(APP_SETUP_COMPLTE, value)
        }

    private fun createDefaultSettings(): Settings {
        settings = Settings(
            acceptedFileTypes = mutableListOf(AudioFileType.WAVE, AudioFileType.MP3, AudioFileType.OGG),
            isWaitMode = false,
            sampleRate = SampleRate.RATE_44_KHZ,
            showLoopCount = true,
            keepScreenOn = true,
            playInBackground = true
        )
        return settings
    }

    companion object {

        private const val PREFS_LOOPY_KEY = "loops_list"
        const val APP_SETUP_COMPLTE = "setup"

        enum class AudioFileType(val suffix: String) {
            WAVE("wav"),
            MP3("mp3"),
            OGG("ogg"),
            PCM("pcm")
        }

        enum class ForbiddenFolder(val folderName: String) {
            ANDROID("Android,"),
            DCIM("DCIM")
        }
    }
}
