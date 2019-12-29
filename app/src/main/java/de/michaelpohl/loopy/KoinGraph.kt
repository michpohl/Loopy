package de.michaelpohl.loopy

import de.michaelpohl.loopy.model.ExternalStorageManager
import de.michaelpohl.loopy.model.InternalDataRepository
import de.michaelpohl.loopy.model.SharedPreferencesManager
import de.michaelpohl.loopy.ui.main.filebrowser.AlbumBrowserViewModel
import de.michaelpohl.loopy.ui.main.filebrowser.FileBrowserViewModel
import de.michaelpohl.loopy.ui.main.help.MarkupViewerViewModel
import de.michaelpohl.loopy.ui.main.mediabrowser.MusicBrowserViewModel
import de.michaelpohl.loopy.ui.main.player.PlayerViewModel
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
        single { SharedPreferencesManager(get()) }
        single { InternalDataRepository(get(), get()) }
        single { ExternalStorageManager(get()) }
    }

    private val viewModelModule = module {
        viewModel { FileBrowserViewModel() }
        viewModel { PlayerViewModel() }
        viewModel { AlbumBrowserViewModel() }
        viewModel { MusicBrowserViewModel() }
        viewModel { MarkupViewerViewModel() }
    }
}