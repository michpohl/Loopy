package de.michaelpohl.loopy.ui.main.filebrowser.adapter

import com.example.adapter.adapter.Sorting
import de.michaelpohl.loopy.common.FileModel

class FileBrowserSorting : Sorting<FileModel>() {
    override fun sort(input: List<FileModel>): List<FileModel> {
        val folders = input
            .filterIsInstance<FileModel.Folder>()
            .sortedBy { it.name }

        val rest = input
            .filter { it !is FileModel.Folder }
            .sortedBy { it.name }
        return folders + rest
    }
}
