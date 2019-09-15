package de.michaelpohl.loopy

import de.michaelpohl.loopy.ui.main.filebrowser.AlbumBrowserViewModel
import de.michaelpohl.loopy.ui.main.filebrowser.FileBrowserViewModel
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
    }

    private val viewModelModule = module {
        viewModel { FileBrowserViewModel() }
        viewModel { PlayerViewModel() }
        viewModel { AlbumBrowserViewModel() }
        viewModel { MusicBrowserViewModel()}
    }
}