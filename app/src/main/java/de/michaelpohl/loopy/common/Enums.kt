package de.michaelpohl.loopy.common

import com.franmontiel.attributionpresenter.entities.Attribution
import com.franmontiel.attributionpresenter.entities.License


enum class PlayerState {
    PLAYING, PAUSED, STOPPED, UNKNOWN, READY
}

enum class Library(
    val attribution: Attribution
) {
    GSON(
        Attribution.Builder("Gson").addCopyrightNotice("Copyright 2008 Google Inc.").addLicense(License.APACHE).setWebsite(
            "https://github.com/google/gson"
        ).build()
    ),
    TIMBER(
        Attribution.Builder("Timber").addCopyrightNotice("Copyright 2013 Jake Wharton").addLicense(License.APACHE).setWebsite(
            "https://github.com/JakeWharton/timber"
        ).build()
    ),
    AUDIOGRAM(
        Attribution.Builder("Audiogram").addCopyrightNotice("Copyright (c) 2016 Alexey Derbyshev").addLicense(License.MIT).setWebsite(
            "https://github.com/alxrm/audiowave-progressbar"
        ).build()
    ),
    MARKWON(
        Attribution.Builder("Markwon").addCopyrightNotice("Copyright 2017 Dimitry Ivanov").addLicense(License.APACHE).setWebsite(
            "https://github.com/JakeWharton/timber"
        ).build()
    )
}
