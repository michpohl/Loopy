package de.michaelpohl.loopy

import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.model.AudioFilesRepository
import de.michaelpohl.loopy.model.ExternalStorageManager
import de.michaelpohl.loopy.model.JniPlayer
import de.michaelpohl.loopy.model.PlayerServiceBinder
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
        single { androidApplication().assets }
        single { SharedPreferencesManager(get()) }
        single { AudioFilesRepository(get(), get()) }
        single { ExternalStorageManager(get()) }
        single { AppStateRepository(get()) }
    }

    private val viewModelModule = module {
        viewModel { FileBrowserViewModel() }
        viewModel { PlayerViewModel(get(), get()) }
        viewModel { AlbumBrowserViewModel() }
        viewModel { MusicBrowserViewModel() }
        viewModel { MarkupViewerViewModel() }
    }
}
