package de.michaelpohl.loopy.ui.main

import android.arch.lifecycle.ViewModel
import android.databinding.BaseObservable
import android.view.View
import timber.log.Timber

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var testLabel = "hohoho"

    fun testMethod(view: View) {
        Timber.d("Hi!")
    }

}
