package de.michaelpohl.loopy.ui.main.help

import de.michaelpohl.loopy.model.FilesRepository
import de.michaelpohl.loopy.ui.main.base.BaseUIState
import de.michaelpohl.loopy.ui.main.base.BaseViewModel
import timber.log.Timber

class MarkupViewerViewModel() : BaseViewModel<BaseUIState>() {

    override fun initUIState(): BaseUIState {
        // TODO refactor
        return object : BaseUIState() {}
    }

}
