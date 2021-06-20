package com.michaelpohl.loopyplayer2.ui.mediastorebrowser.adapter

sealed class MediaStoreItemModel {

    abstract val name: String

    data class Album(
        override val name: String,
        val artist: String

    ) : MediaStoreItemModel()

    data class Artist(
        override val name: String,
        val album: String
    ) : MediaStoreItemModel()

    data class Track(
        override val name: String,
        val album: String,
        val artist: String,
        val trackNo: Int?,
        val path: String,
        val extension: String = "",
        val isSelected: Boolean? = null

    ) : MediaStoreItemModel()
}
