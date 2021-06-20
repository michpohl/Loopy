package com.michaelpohl.loopyplayer2.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.michaelpohl.loopyplayer2.common.immutable

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
