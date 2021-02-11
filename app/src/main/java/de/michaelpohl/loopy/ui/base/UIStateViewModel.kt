package de.michaelpohl.loopy.ui.base

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.michaelpohl.loopy.common.immutable
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class UIStateViewModel<T : BaseUIState> : BaseViewModel() {

    protected val _state = MutableLiveData<T>()
    open val state: LiveData<T> = _state.immutable()

    protected val currentState: T
        get() {
            return state.value ?: initUIState()
        }

    abstract fun initUIState(): T

}

abstract class BaseUIState : Any()
