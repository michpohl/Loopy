package com.michaelpohl.loopyplayer2

import com.michaelpohl.loopyplayer2.common.MediaStoreRepository
import com.michaelpohl.loopyplayer2.common.StorageRepository
import com.michaelpohl.loopyplayer2.model.AppStateRepository
import com.michaelpohl.loopyplayer2.model.FilesRepository
import com.michaelpohl.loopyplayer2.model.ExternalStorageManager
import com.michaelpohl.loopyplayer2.model.SharedPreferencesManager
import com.michaelpohl.loopyplayer2.ui.filebrowser.FileBrowserViewModel
import com.michaelpohl.loopyplayer2.ui.help.MarkdownViewerViewModel
import com.michaelpohl.loopyplayer2.ui.licenses.LicensesViewModel
import com.michaelpohl.loopyplayer2.ui.mediastorebrowser.MediaStoreBrowserViewModel
import com.michaelpohl.loopyplayer2.ui.player.PlayerViewModel
import com.michaelpohl.loopyplayer2.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object KoinGraph {

    fun get(): List<Module> {
        return listOf(
            baseModule,
            viewModelModule
        )
    }

    private val baseModule = module {
        single { androidApplication().resources }
        single { androidApplication().assets }
        single { SharedPreferencesManager(get()) }
        single { FilesRepository(get(), get()) }
        single { ExternalStorageManager(get()) }
        single { AppStateRepository(get()) }
        single { StorageRepository(get()) }
        single { MediaStoreRepository(androidApplication()) }
    }

    private val viewModelModule = module {
        viewModel { FileBrowserViewModel(get(), get()) }
        viewModel { MediaStoreBrowserViewModel(get(), get()) }
        viewModel { PlayerViewModel(get(), get()) }
        viewModel { MarkdownViewerViewModel(get()) }
        viewModel { SettingsViewModel(get()) }
        viewModel { LicensesViewModel(get()) }
    }
}
