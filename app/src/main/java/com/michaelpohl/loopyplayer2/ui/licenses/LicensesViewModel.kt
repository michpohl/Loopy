package com.michaelpohl.loopyplayer2.ui.licenses

import com.michaelpohl.loopyplayer2.model.FilesRepository
import com.michaelpohl.loopyplayer2.ui.base.UIStateViewModel

class LicensesViewModel(private val repo: FilesRepository) : UIStateViewModel<Any>() {

    val licenses = repo.getLicenses()
    override fun initUIState(): Any {
        return object {}
    }
}
