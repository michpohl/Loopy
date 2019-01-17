package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val resources = application.resources

    protected fun getString(stringID : Int) : String {
        return resources.getString(stringID)
    }
}