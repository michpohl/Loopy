package de.michaelpohl.loopy.ui.main.filebrowser.adapter

import com.example.adapter.adapter.Sorting
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.ui.main.filebrowser.FileBrowserViewModel
import timber.log.Timber

class FileBrowserSorting : Sorting.Custom<FileModel, FileBrowserViewModel.UIState>() {

    override fun sort(input: FileBrowserViewModel.UIState): List<FileModel> {
        val folders = input.filesToDisplay
            .filterIsInstance<FileModel.Folder>()
            .sortedBy { it.name }

        val rest = input.filesToDisplay
            .filter { it !is FileModel.Folder }
            .sortedBy { it.name }
            .map {
                if (it is FileModel.AudioFile) {
                    input.selectedFiles?.let { selected ->
                        it.copy(isSelected = selected.any { file -> file.path == it.path })
                    } ?: it
                } else it
            }

        val result = folders + rest
        return result
    }
}
