package de.michaelpohl.loopy.ui.main.help

import android.app.Application
import android.databinding.ObservableInt
import android.view.View
import de.michaelpohl.loopy.ui.main.BaseViewModel
import timber.log.Timber

class HelpViewModel(application: Application): BaseViewModel(application) {

    lateinit var onAboutClickedListener: () -> Unit
    val buttonVisibility = ObservableInt(View.VISIBLE)

    fun onAboutClicked(view: View) {
        onAboutClickedListener.invoke()
        buttonVisibility.set(View.GONE)
    }
}