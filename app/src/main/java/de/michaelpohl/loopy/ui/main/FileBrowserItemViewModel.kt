package de.michaelpohl.loopy.ui.main

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.view.View
import android.view.View.*
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import hugo.weaving.DebugLog
import timber.log.Timber

@DebugLog
class FileBrowserItemViewModel : ViewModel() {

    val folderLabelVisibility = ObservableField(INVISIBLE)
    val sizeLabelVisibility = ObservableField(INVISIBLE)
    val subFolderIndicatorVisibility = ObservableField(INVISIBLE)
    val pickFolderButtonVisibility = ObservableField(INVISIBLE)

    var fileModel = FileModel("", FileType.FILE, "", 0.0)
    var name = ObservableField("name")
    var subFolders = ObservableField("folders")
    var fileSize = ObservableField("filesize")

    fun update() {
        Timber.d("Filemodel: %s", fileModel)
        name.set(fileModel.name)
        //TODO turn this string stuff into something proper and non-hard coded
        subFolders.set("(${fileModel.subFiles} files)")
        fileSize.set("${String.format("%.2f", fileModel.sizeInMB)} mb")


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