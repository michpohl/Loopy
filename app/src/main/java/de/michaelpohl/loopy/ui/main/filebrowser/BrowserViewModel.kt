package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.ui.main.BaseViewModel

open class BrowserViewModel() : BaseViewModel() {

    lateinit var onSelectionSubmitted: (List<FileModel>) -> Unit

    var emptyFolderLayoutVisibility = ObservableField<Int>(View.INVISIBLE) //override if interested
    var bottomBarVisibility = ObservableInt(View.INVISIBLE)
    var selectButtonText = ObservableField(getString(R.string.btn_select_all))

    lateinit var listener: OnBrowserActionListener


    open fun onSelectButtonClicked(view: View) {
        //override if action is needed
    }

    open fun onSubmitButtonClicked(view: View) {
//       onSelectionSubmitted()
    }

    interface OnBrowserActionListener {
        fun onFolderClicked(fileModel: FileModel)
        fun onAlbumClicked(albumTitle: String)
        fun acceptSubmittedSelection()
    }
}
