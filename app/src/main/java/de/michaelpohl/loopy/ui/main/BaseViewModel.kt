package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.support.v4.content.ContextCompat
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.model.LoopedPlayer
import timber.log.Timber

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected  val resources = application.resources

    protected fun getString(stringID : Int) : String {
        return resources.getString(stringID)
    }
}