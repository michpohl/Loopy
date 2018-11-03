package de.michaelpohl.loopy.ui.main

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.view.View.*
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import timber.log.Timber

class FileBrowserItemViewModel(fileModel: FileModel): ViewModel() {


    val folderLabelVisibility = ObservableField(INVISIBLE)
    val sizeLabelVisibility = ObservableField(INVISIBLE)
    val subFolderIndicatorVisibility = ObservableField(INVISIBLE)
    val pickFolderButtonVisibility = ObservableField(INVISIBLE)

    val name: ObservableField<String> = ObservableField(fileModel.name)

    //TODO turn this string stuff into something proper and non-hard coded
    val subFolders: ObservableField<String> = ObservableField("(${fileModel.subFiles} files)")
    val fileSize: ObservableField<String> = ObservableField("${String.format("%.2f", fileModel.sizeInMB)} mb")

    init {
        if (fileModel.fileType == FileType.FOLDER) {
            Timber.d("it's a folder")
            folderLabelVisibility.set(VISIBLE)
            sizeLabelVisibility.set(INVISIBLE)
        } else {
            Timber.d("it's not a folder")

            folderLabelVisibility.set(INVISIBLE)
            sizeLabelVisibility.set(VISIBLE)
        }
        if (FileHelper.hasSubFolders(fileModel.path)) {
            Timber.d("has subfolders")

            subFolderIndicatorVisibility.set(VISIBLE)
        } else {
            Timber.d("has no subfolders")

            subFolderIndicatorVisibility.set(INVISIBLE)
        }
        if (FileHelper.containsAudioFilesInAnySubFolders(fileModel.path)) {
            Timber.d("contains audio")

            pickFolderButtonVisibility.set(VISIBLE)
        } else {
            Timber.d("contains no audio")

            pickFolderButtonVisibility.set(GONE)
        }

    }
}