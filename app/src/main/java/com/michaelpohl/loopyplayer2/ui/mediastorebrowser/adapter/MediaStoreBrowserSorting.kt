package com.michaelpohl.loopyplayer2.ui.mediastorebrowser.adapter

import com.michaelpohl.delegationadapter.Sorting
import com.michaelpohl.loopyplayer2.ui.mediastorebrowser.MediaStoreBrowserViewModel
import timber.log.Timber

class MediaStoreBrowserSorting : Sorting.Custom<MediaStoreItemModel, MediaStoreBrowserViewModel.UIState>() {

    override fun sort(input: MediaStoreBrowserViewModel.UIState): List<MediaStoreItemModel> {
        val sorted = with(input.itemsToDisplay) {
            when {
                this.containsOnly<MediaStoreItemModel.Album>() -> this.sortedBy { it.name }
                this.containsOnly<MediaStoreItemModel.Artist>() -> this.sortedBy { it.name }
                this.containsOnly<MediaStoreItemModel.Track>() -> sortTracks(this as List<MediaStoreItemModel.Track>)
                else -> this
            }
        }
        Timber.d("${input.itemsToDisplay}")
        Timber.d("${input.selectedItems}")
        return sorted.map { model ->
            if (model is MediaStoreItemModel.Track) {
                model.copy(isSelected = (input.selectedItems ?: listOf()).map { it.path }.contains(model.path))
            } else model
        }
    }

    private fun sortTracks(input: List<MediaStoreItemModel.Track>): List<MediaStoreItemModel.Track> {
        val numberedTracks = input.filter { it.trackNo != 0 }
        val otherTracks = input.filter { it.trackNo == 0 }
        return numberedTracks.sortedBy { it.trackNo } + otherTracks.sortedBy { it.name }
    }

    private inline fun <reified T> List<MediaStoreItemModel>.containsOnly(): Boolean {
        this.forEach { if (it !is T) return false }
        return true
    }
}
