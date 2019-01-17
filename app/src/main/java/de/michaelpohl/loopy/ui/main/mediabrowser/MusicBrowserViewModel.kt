package de.michaelpohl.loopy.ui.main.mediabrowser

import android.app.Application
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.mediabrowser.MusicBrowserAdapter

//TODO rebuild for audioModels!
class MusicBrowserViewModel(application: Application) : BaseViewModel(application) {

    private var adapter =
        MusicBrowserAdapter(this::onSelectedItemsChanged, this::onItemClicked)

    var selectButtonText = ObservableField(getString(R.string.btn_select_all))
    var emptyFolderLayoutVisibility = ObservableField<Int>(View.INVISIBLE)
    var bottomBarVisibility = ObservableInt(View.INVISIBLE)

    lateinit var listener: OnItemClickListener
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

    fun onSelectButtonClicked(view: View) {
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
        DataRepository.onFileSelectionUpdated(selectedItems)
    }

    private fun onItemClicked(audioModel: AudioModel) {
        //TODO do something
    }

    interface OnItemClickListener {
        fun onFolderClicked(fileModel: FileModel)
    }
}