package com.michaelpohl.loopyplayer2.ui.help

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.michaelpohl.loopyplayer2.common.toVisibility
import com.michaelpohl.loopyplayer2.model.FilesRepository
import com.michaelpohl.loopyplayer2.ui.base.BaseViewModel

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
