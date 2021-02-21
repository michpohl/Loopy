package de.michaelpohl.loopy.ui.help

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.common.toVisibility
import de.michaelpohl.loopy.model.FilesRepository
import de.michaelpohl.loopy.ui.base.BaseViewModel

class MarkdownViewerViewModel(val repo: FilesRepository) : BaseViewModel() {

    val docType = MutableLiveData<MarkdownViewerFragment.DocumentType>()

    lateinit var showInfoListener: () -> Unit
    lateinit var showUsedLibrariesListener: () -> Unit
    lateinit var goBackListener: () -> Unit

    val showInfoButtons = MediatorLiveData<Int>().apply {
        addSource(docType) {
            this.value = (it == MarkdownViewerFragment.DocumentType.ABOUT).toVisibility()
        }
    }

    val showCloseButton = MediatorLiveData<Int>().apply {
        addSource(docType) {
            this.value = (it == MarkdownViewerFragment.DocumentType.WHATSNEW).toVisibility()
        }
    }

    fun getAssetString(fileName: String): String? {
        return repo.getStringAsset(fileName)
    }
}
