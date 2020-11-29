package de.michaelpohl.loopy.ui.main.help

import de.michaelpohl.loopy.ui.main.base.BaseUIState
import de.michaelpohl.loopy.ui.main.base.BaseViewModel

class MarkupViewerViewModel : BaseViewModel<BaseUIState>() {

    override fun initUIState(): BaseUIState {
        // TODO refactor
        return object : BaseUIState() {}
    }
}
