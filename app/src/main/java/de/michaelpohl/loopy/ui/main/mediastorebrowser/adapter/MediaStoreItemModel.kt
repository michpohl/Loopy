package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

sealed class MediaStoreItemModel {

    abstract val name: String

    data class Album(
        override val name: String
    ) : MediaStoreItemModel()

    data class Artist(
        override val name: String
    ) : MediaStoreItemModel()

    data class Track(
        override val name: String
    ) : MediaStoreItemModel()
}
