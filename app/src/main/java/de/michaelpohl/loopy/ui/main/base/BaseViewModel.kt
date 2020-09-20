package de.michaelpohl.loopy.ui.main.base

import android.content.res.Resources
import androidx.lifecycle.ViewModel

import org.koin.core.KoinComponent
import org.koin.core.inject

open class BaseViewModel : ViewModel(), KoinComponent {

    protected val resources: Resources by inject()

    protected fun getString(stringID : Int) : String {
        return resources.getString(stringID)
    }
}
