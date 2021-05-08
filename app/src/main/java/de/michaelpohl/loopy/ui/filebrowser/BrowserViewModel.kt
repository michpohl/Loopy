package de.michaelpohl.loopy.ui.filebrowser

import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.ui.base.BaseUIState
import de.michaelpohl.loopy.ui.base.UIStateViewModel

abstract class BrowserViewModel<T : BrowserViewModel.BrowserUIState> : UIStateViewModel<T>() {

    lateinit var onSelectionSubmittedListener: (List<FileModel.AudioFile>) -> Unit
    abstract fun selectAll()
    abstract class BrowserUIState : BaseUIState() {

        abstract val itemsToDisplay: List<*>
        abstract val lastDisplayedItems: List<List<*>>?
        abstract val selectedItems: List<*>?
        abstract val shouldShowEmptyMessage: Int
        abstract val shouldShowSubmitButton: Int
        abstract val shouldShowSelectAllButton: Int
    }
}
