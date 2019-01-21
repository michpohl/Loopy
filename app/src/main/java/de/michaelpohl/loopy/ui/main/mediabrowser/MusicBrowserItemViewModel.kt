package de.michaelpohl.loopy.ui.main.mediabrowser

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.view.View
import android.view.View.INVISIBLE
import de.michaelpohl.loopy.common.AudioModel

class MusicBrowserItemViewModel(
    private val position: Int,
    private val audioModel: AudioModel,
    private val selectedListener: ((Boolean, Int) -> Unit)
    ) : ViewModel() {

    val sizeLabelVisibility = ObservableField(INVISIBLE)
    val checkBoxVisibility = ObservableField(INVISIBLE)
    val selected = ObservableField(false)

    var name = ObservableField("name")
    var fileSize = ObservableField("filesize")

    fun update() {
        name.set(audioModel.name)

//        fileSize.set("${String.format("%.2f", audioModel.sizeInMB)} mb")
        fileSize.set("99") //TODO real value or remove
        selectedListener.invoke(selected.get()!!, position)
    }

    fun onItemClicked(view: View) {
        //TODO if nothing else needs clicking this an be simplified
        onCheckBoxClicked(view)
    }

    fun onCheckBoxClicked(view: View) {
        selected.set(selected.get()?.not()) //why this ugly nullability?
        selectedListener.invoke(selected.get()!!, position)
    }
}