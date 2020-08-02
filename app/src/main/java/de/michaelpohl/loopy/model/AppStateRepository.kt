package de.michaelpohl.loopy.model

import de.michaelpohl.loopy.common.Settings

class AppStateRepository(val sharedPrefs: SharedPreferencesManager) {

    fun getSettings(): Settings {
        return sharedPrefs.getSettings() ?: createDefaultSettings()
    }

    fun saveSettings(settings: Settings) {
        sharedPrefs.saveSettings(settings)
    }

    private fun createDefaultSettings(): Settings {
        val defaultSettings =
            Settings(
                acceptedFileTypes = mutableListOf(AudioFileType.WAVE, AudioFileType.MP3),
                isWaitMode = false,
                showLoopCount = true,
                keepScreenOn = true,
                playInBackground = true
            )
        saveSettings(defaultSettings)
        return defaultSettings
    }

    companion object {

        private const val PREFS_LOOPY_KEY = "loops_list"

        enum class AudioFileType(val suffix: String) {
            WAVE("wav"),
            MP3("mp3"),
            OGG("ogg")
        }
    }
}
