package de.michaelpohl.loopy.ui.main.base

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.michaelpohl.loopy.common.immutable
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseViewModel<T : BaseUIState> : ViewModel(), KoinComponent {

    protected val resources: Resources by inject()
    protected fun getString(stringID: Int): String {
        return resources.getString(stringID)
    }

    protected val _state = MutableLiveData<T>()
    open val state: LiveData<T> = _state.immutable()

    protected val currentState: T
        get() {
            return state.value ?: initUIState()
        }

    abstract fun initUIState(): T
    open fun onFragmentResumed() {
    }

    open fun onFragmentPaused() {
    }
}

abstract class BaseUIState : Any()
