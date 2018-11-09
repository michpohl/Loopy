package de.michaelpohl.loopy.ui.main

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.view.View.*
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import hugo.weaving.DebugLog

@DebugLog
class FileBrowserItemViewModel : ViewModel() {

    val folderLabelVisibility = ObservableField(INVISIBLE)
    val sizeLabelVisibility = ObservableField(INVISIBLE)
    val subFolderIndicatorVisibility = ObservableField(INVISIBLE)
    val forbiddenSignVisibility = ObservableField(INVISIBLE)
    val pickFolderButtonVisibility = ObservableField(INVISIBLE)

    var fileModel = FileModel("", FileType.FILE, "", 0.0)
    var name = ObservableField("name")
    var subFolders = ObservableField("folders")
    var fileSize = ObservableField("filesize")
    lateinit var context: Context

    fun update() {

        //TODO turn this whole method into something more beautiful
        name.set(fileModel.name)
        subFolders.set("(${fileModel.subFiles} files)")
        fileSize.set("${String.format("%.2f", fileModel.sizeInMB)} mb")


        if (fileModel.fileType == FileType.FOLDER) {

            folderLabelVisibility.set(VISIBLE)
            sizeLabelVisibility.set(INVISIBLE)
        } else {

            folderLabelVisibility.set(INVISIBLE)
            sizeLabelVisibility.set(VISIBLE)
        }
        if (fileModel.hasSubFolders()) {

            if (FileHelper.isExcludedFolderName(fileModel.path)) {
                subFolderIndicatorVisibility.set(INVISIBLE)
                forbiddenSignVisibility.set(VISIBLE)
                } else {
                subFolderIndicatorVisibility.set(VISIBLE)
                forbiddenSignVisibility.set(INVISIBLE)

            }
        } else {
        }

        if (FileHelper.containsAudioFilesInAnySubFolders(fileModel.path)) {

            pickFolderButtonVisibility.set(VISIBLE)
        } else {

            pickFolderButtonVisibility.set(GONE)
        }
    }
}