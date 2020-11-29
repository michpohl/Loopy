package de.michaelpohl.loopy

import de.michaelpohl.loopy.common.MediaStoreRepository
import de.michaelpohl.loopy.common.StorageRepository
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.model.AudioFilesRepository
import de.michaelpohl.loopy.model.ExternalStorageManager
import de.michaelpohl.loopy.model.SharedPreferencesManager
import de.michaelpohl.loopy.ui.main.filebrowser.FileBrowserViewModel
import de.michaelpohl.loopy.ui.main.help.MarkupViewerViewModel
import de.michaelpohl.loopy.ui.main.mediastorebrowser.MediaStoreBrowserViewModel
import de.michaelpohl.loopy.ui.main.player.PlayerViewModel
import de.michaelpohl.loopy.ui.main.settings.SettingsViewModel
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
        single { AudioFilesRepository(get(), get()) }
        single { ExternalStorageManager(get()) }
        single { AppStateRepository(get()) }
        single { StorageRepository(get()) }
        single { MediaStoreRepository(androidApplication()) }
    }

    private val viewModelModule = module {
        viewModel { FileBrowserViewModel(get(), get()) }
        viewModel { MediaStoreBrowserViewModel(get(), get()) }
        viewModel { PlayerViewModel(get(), get()) }
        viewModel { MarkupViewerViewModel() }
        viewModel { SettingsViewModel(get()) }
    }
}
