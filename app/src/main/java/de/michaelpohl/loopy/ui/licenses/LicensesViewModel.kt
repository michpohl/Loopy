package de.michaelpohl.loopy.ui.licenses

import de.michaelpohl.loopy.model.FilesRepository
import de.michaelpohl.loopy.ui.base.BaseUIState
import de.michaelpohl.loopy.ui.base.BaseViewModel

class LicensesViewModel(private val repo: FilesRepository) : BaseViewModel<BaseUIState>() {

    val licenses = repo.getLicenses()

    override fun initUIState(): BaseUIState {
        return object : BaseUIState() {}
    }

}
