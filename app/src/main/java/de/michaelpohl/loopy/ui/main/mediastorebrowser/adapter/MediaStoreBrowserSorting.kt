package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

import com.example.adapter.adapter.Sorting

class MediaStoreBrowserSorting : Sorting<MediaStoreItemModel>() {
    override fun sort(input: List<MediaStoreItemModel>): List<MediaStoreItemModel> {
        return when {
            input.containsOnly<MediaStoreItemModel.Album>() -> input.sortedBy { it.name }
            input.containsOnly<MediaStoreItemModel.Artist>() -> input.sortedBy { it.name }
            input.containsOnly<MediaStoreItemModel.Track>() -> sortTracks(input as List<MediaStoreItemModel.Track>)
            else -> input
        }
    }

    private fun sortTracks(input: List<MediaStoreItemModel.Track>): List<MediaStoreItemModel.Track> {
        val numberedTracks = input.filter { it.trackNo != 0 }
        val otherTracks = input.filter { it.trackNo == 0 }
        return numberedTracks.sortedBy { it.trackNo } + otherTracks.sortedBy { it.name }
    }


    private inline fun <reified T> List<MediaStoreItemModel>.containsOnly(): Boolean {
        var result = true
        this.forEach { if (it !is T) result = false }
        return result
    }
}
