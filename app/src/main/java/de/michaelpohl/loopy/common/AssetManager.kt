package de.michaelpohl.loopy.common

import java.util.*

class AssetManager {


    val ABOUT_FILE_NAME = "about_"
    val HELP_FILE_NAME = "help_"
    val POST_FIX = ".md"
    val DEFAULT_LOCALE = "default"
    fun getHelpTextResource(): String {
        val locale = getLocale()
        if (locale == "de") {
            return HELP_FILE_NAME + locale + POST_FIX
        } else {
            return HELP_FILE_NAME + DEFAULT_LOCALE + POST_FIX
        }
    }

    fun getAboutTextResource(): String {
        val locale = getLocale()
        if (locale == "de") {
            return ABOUT_FILE_NAME + locale + POST_FIX
        } else {
            return ABOUT_FILE_NAME + DEFAULT_LOCALE + POST_FIX
        }
    }

    private fun getLocale(): String {
        return Locale.getDefault().language.toLowerCase()
    }
}
