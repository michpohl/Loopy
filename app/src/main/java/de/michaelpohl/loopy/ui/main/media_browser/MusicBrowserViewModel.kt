package de.michaelpohl.loopy.ui.main.media_browser

import android.app.Application
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.storage_browser.BrowserViewModel

class MusicBrowserViewModel(application: Application) : BrowserViewModel(application) {

    private var adapter =
        MusicBrowserAdapter(this::onSelectedItemsChanged, this::onItemClicked)

    lateinit var audioModels: List<AudioModel>

    fun getAdapter(): MusicBrowserAdapter {
        return adapter
    }

    fun updateAdapter() {
        if (audioModels.isEmpty()) {
            emptyFolderLayoutVisibility.set(View.VISIBLE)
        } else {
            emptyFolderLayoutVisibility.set(View.INVISIBLE)
        }
        if (audioModels.size > 1) {
            bottomBarVisibility.set(View.VISIBLE)
        } else bottomBarVisibility.set(View.INVISIBLE)
        adapter.updateData(audioModels)
    }

    override fun onSelectButtonClicked(view: View) {
        if (adapter.selectedItems.size > 0) {
            adapter.deselectAll()
        } else {
            adapter.selectAll()
        }
    }

    private fun onSelectedItemsChanged(selectedItems: List<AudioModel>) {
        if (selectedItems.isNotEmpty()) {
            selectButtonText.set(getString(R.string.btn_deselect_all))
        } else {
            selectButtonText.set(getString(R.string.btn_select_all))
        }
        DataRepository.onAudioFileSelectionUpdated(selectedItems)
    }

    private fun onItemClicked(audioModel: AudioModel) {
        //TODO do something
    }
}