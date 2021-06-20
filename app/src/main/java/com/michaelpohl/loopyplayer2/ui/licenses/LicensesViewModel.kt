package com.michaelpohl.loopyplayer2.ui.licenses

import com.michaelpohl.loopyplayer2.model.FilesRepository
import com.michaelpohl.loopyplayer2.ui.base.BaseUIState
import com.michaelpohl.loopyplayer2.ui.base.UIStateViewModel

class LicensesViewModel(private val repo: FilesRepository) : UIStateViewModel<BaseUIState>() {

    val licenses = repo.getLicenses()

    override fun initUIState(): BaseUIState {
        return object : BaseUIState() {}
    }

}
