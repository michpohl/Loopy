package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.ui.main.base.BaseViewModel

open class BrowserViewModel : BaseViewModel() {

    lateinit var onSelectionSubmitted: (List<FileModel>) -> Unit

    protected var _emptyFolderLayoutVisibility = MutableLiveData(View.INVISIBLE) //override if interested
    var emptyFolderLayoutVisibility = _emptyFolderLayoutVisibility.immutable()

    var bottomBarVisibility = MediatorLiveData<Int>()

    var _selectButtonText = MutableLiveData(getString(R.string.btn_select_all))
    var selectButtonText = _selectButtonText.immutable()

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
