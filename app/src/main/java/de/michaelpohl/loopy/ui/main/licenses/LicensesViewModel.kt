package de.michaelpohl.loopy.ui.main.licenses

import de.michaelpohl.loopy.model.FilesRepository
import de.michaelpohl.loopy.ui.main.base.BaseUIState
import de.michaelpohl.loopy.ui.main.base.BaseViewModel

class LicensesViewModel(private val repo: FilesRepository) : BaseViewModel<BaseUIState>() {

    val licenses = repo.getLicenses()

    override fun initUIState(): BaseUIState {
        return object : BaseUIState() {}
    }

}
