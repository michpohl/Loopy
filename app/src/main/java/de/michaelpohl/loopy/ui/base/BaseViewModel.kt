package de.michaelpohl.loopy.ui.base

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.michaelpohl.loopy.common.immutable
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseViewModel : ViewModel(), KoinComponent {

    protected val resources: Resources by inject()
    protected fun getString(stringID: Int): String {
        return resources.getString(stringID)
    }

    open fun onFragmentResumed() {
    }

    open fun onFragmentPaused() {
    }
}
