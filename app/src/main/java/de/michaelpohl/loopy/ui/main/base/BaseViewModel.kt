package de.michaelpohl.loopy.ui.main.base


import android.content.res.Resources
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
    val state = _state.immutable()

    protected val currentState: T
        get() {
            return state.value ?: initUIState()
        }

    abstract fun initUIState(): T

}

abstract class BaseUIState : Any()
