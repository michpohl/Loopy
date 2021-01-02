package de.michaelpohl.loopy.ui.main.filebrowser

import androidx.lifecycle.LiveData
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.ui.main.base.BaseUIState
import de.michaelpohl.loopy.ui.main.base.BaseViewModel

abstract class BrowserViewModel<T : BrowserViewModel.BrowserUIState> : BaseViewModel<T>() {

    lateinit var onSelectionSubmittedListener: (List<FileModel.AudioFile>) -> Unit

    abstract fun selectAll()

    abstract class BrowserUIState() : BaseUIState() {
        abstract val itemsToDisplay : List<*>
        abstract val lastDisplayedItems: List<List<*>>?
        abstract val selectedItems: List<*>?
        abstract val shouldShowEmptyMessage: Int
        abstract val shouldShowSubmitButton: Int
        abstract val shouldShowSelectAllButton: Int
    }

}
