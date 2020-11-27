package de.michaelpohl.loopy.ui.main.filebrowser

import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.model.AudioFilesRepository
import de.michaelpohl.loopy.ui.main.base.BaseUIState
import de.michaelpohl.loopy.ui.main.base.BaseViewModel
import org.koin.core.inject

abstract class BrowserViewModel() : BaseViewModel<BaseUIState>() {

    protected val audioRepo: AudioFilesRepository by inject()

    protected abstract val selectedFiles: MutableLiveData<List<FileModel.AudioFile>>

    lateinit var onSelectionSubmittedListener: (List<FileModel.AudioFile>) -> Unit

    protected fun submitSelection(selection: List<FileModel.AudioFile>) {
        // TODO remove I guess
//        audioRepo.addLoopsToSet(selection)
    }

    abstract fun selectAll()
}
