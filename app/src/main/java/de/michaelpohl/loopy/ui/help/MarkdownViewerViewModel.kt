package de.michaelpohl.loopy.ui.help

import de.michaelpohl.loopy.model.FilesRepository
import de.michaelpohl.loopy.ui.base.BaseUIState
import de.michaelpohl.loopy.ui.base.BaseViewModel

class MarkdownViewerViewModel(val repo: FilesRepository) : BaseViewModel<BaseUIState>() {

    override fun initUIState(): BaseUIState {
        // TODO refactor
        return object : BaseUIState() {}
    }

    fun getAssetString(fileName: String) : String? {
        return resources.assets.open(fileName).bufferedReader().use {
            it.readText()
        }
    }

}
