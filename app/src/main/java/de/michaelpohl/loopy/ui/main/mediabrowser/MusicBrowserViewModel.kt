package de.michaelpohl.loopy.ui.main.mediabrowser

import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.filebrowser.BrowserViewModel

class MusicBrowserViewModel : BrowserViewModel() {

    private var adapter =
        MusicBrowserAdapter(this::onSelectedItemsChanged, this::onItemClicked)

    lateinit var audioModels: List<AudioModel>

    fun getAdapter(): MusicBrowserAdapter {
        return adapter
    }

    fun updateAdapter() {
        if (audioModels.isEmpty()) {
            _emptyFolderLayoutVisibility.postValue(View.VISIBLE)
        } else {
            _emptyFolderLayoutVisibility.postValue(View.INVISIBLE)
        }
//        if (audioModels.size > 0) {
//            _bottomBarVisibility.postValue(View.VISIBLE)
//        } else _bottomBarVisibility.postValue(View.INVISIBLE)
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
            _selectButtonText.postValue(getString(R.string.btn_deselect_all))
        } else {
            _selectButtonText.postValue(getString(R.string.btn_select_all))
        }
        DataRepository.onAudioFileSelectionUpdated(selectedItems)
    }

    private fun onItemClicked(audioModel: AudioModel) {
        //TODO do something
    }
}
