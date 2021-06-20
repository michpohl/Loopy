package com.michaelpohl.loopyplayer2.ui.filebrowser.adapter

import com.michaelpohl.delegationadapter.Sorting
import com.michaelpohl.loopyplayer2.common.FileModel
import com.michaelpohl.loopyplayer2.ui.filebrowser.FileBrowserViewModel

class FileBrowserSorting : Sorting.Custom<FileModel, FileBrowserViewModel.UIState>() {

    override fun sort(input: FileBrowserViewModel.UIState): List<FileModel> {
        val folders = input.itemsToDisplay
            .filterIsInstance<FileModel.Folder>()
            .sortedBy { it.name }

        val rest = input.itemsToDisplay
            .filter { it !is FileModel.Folder }
            .sortedBy { it.name }
            .map {
                if (it is FileModel.AudioFile) {
                    input.selectedItems?.let { selected ->
                        it.copy(isSelected = selected.any { file -> file.path == it.path })
                    } ?: it
                } else it
            }

        return folders + rest
    }
}
