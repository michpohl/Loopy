package de.michaelpohl.loopy.ui.main.storage_browser

import android.app.Application
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.ui.main.BaseViewModel

open class BrowserViewModel(application: Application) : BaseViewModel(application) {

    var emptyFolderLayoutVisibility = ObservableField<Int>(View.INVISIBLE) //override if interested
    var bottomBarVisibility = ObservableInt(View.INVISIBLE)
    var selectButtonText = ObservableField(getString(R.string.btn_select_all))

    lateinit var listener: OnBrowserActionListener


    open fun onSelectButtonClicked(view: View) {
        //override if action is needed
    }

    open fun onSubmitButtonClicked(view: View) {
        listener.acceptSubmittedSelection()
    }

    interface OnBrowserActionListener {
        fun onFolderClicked(fileModel: FileModel)
        fun onAlbumClicked(albumTitle: String)
        fun acceptSubmittedSelection()
    }
}