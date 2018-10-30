package de.michaelpohl.loopy.ui.main
import android.app.Application
import android.databinding.ObservableField
import android.view.View
import de.michaelpohl.loopy.model.LoopedPlayer
import timber.log.Timber

class MainViewModel(application: Application) : BaseViewModel(application) {

    lateinit var looper : LoopedPlayer

    var testLabel= ObservableField<String>("hohoho")

    fun testMethod(view: View) {
        Timber.d("Hi!")
        testLabel.set("hahaha")

    }


}
