package com.michaelpohl.loopyplayer2.ui.filebrowser

import com.michaelpohl.loopyplayer2.common.FileModel
import com.michaelpohl.loopyplayer2.ui.base.BaseUIState
import com.michaelpohl.loopyplayer2.ui.base.UIStateViewModel

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
