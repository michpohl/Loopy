package de.michaelpohl.loopy.ui.help

import de.michaelpohl.loopy.ui.base.BaseUIState
import de.michaelpohl.loopy.ui.base.BaseViewModel

class MarkupViewerViewModel() : BaseViewModel<BaseUIState>() {

    override fun initUIState(): BaseUIState {
        // TODO refactor
        return object : BaseUIState() {}
    }

}
