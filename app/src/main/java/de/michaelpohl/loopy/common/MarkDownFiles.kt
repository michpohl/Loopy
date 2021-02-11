package de.michaelpohl.loopy.common

import java.util.*

object MarkDownFiles {

    val WHATSNEW_FILE_NAME = "whatsnew_"
    val ABOUT_FILE_NAME = "about_"
    val HELP_FILE_NAME = "help_"
    val POST_FIX = ".md"
    val DEFAULT_LOCALE = "default"

    fun getHelpTextFileName(): String {
        val locale = getLocale()
        return if (locale == "de") {
            HELP_FILE_NAME + locale + POST_FIX
        } else {
            HELP_FILE_NAME + DEFAULT_LOCALE + POST_FIX
        }
    }

    fun getAboutFileName(): String {
        val locale = getLocale()
        return if (locale == "de") {
            ABOUT_FILE_NAME + locale + POST_FIX
        } else {
            ABOUT_FILE_NAME + DEFAULT_LOCALE + POST_FIX
        }
    }

    fun getWhatsNewFileName(): String {
        val locale = getLocale()
        return if (locale == "de") {
            WHATSNEW_FILE_NAME + locale + POST_FIX
        } else {
            WHATSNEW_FILE_NAME + DEFAULT_LOCALE + POST_FIX
        }
    }

    private fun getLocale(): String {
        return Locale.getDefault().language.toLowerCase()
    }
}
