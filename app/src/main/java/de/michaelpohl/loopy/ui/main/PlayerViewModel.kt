package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.databinding.ObservableField
import android.view.View
import com.google.gson.Gson
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileModelsList
import de.michaelpohl.loopy.model.LoopedPlayer

class PlayerViewModel(application: Application) : BaseViewModel(application) {

    var looper: LoopedPlayer = LoopedPlayer.create(application)
    private var adapter = LoopsAdapter(application)
    lateinit var selectFolderListener: OnSelectFolderClickedListener
    lateinit var loopsList: List<FileModel>
    lateinit var sharedPrefs: SharedPreferences
    lateinit var context: Context
    var emptyMessageVisibility = ObservableField(View.VISIBLE)

    fun getAdapter(): LoopsAdapter {
        return adapter
    }

    //TODO move this methods to an appropriate place

    fun saveLoops() {
        val gson = Gson()
        val jsonString = gson.toJson(FileModelsList(loopsList))
//        Assert.assertEquals(jsonString, """{"id":1,"description":"Test"}""")

        val sharedPref = sharedPrefs?: return
        with (sharedPref.edit()) {
            putString(context.getString(R.string.prefs_loops_key), jsonString)
            commit()
        }
    }

    fun loadSavedLoops() {

        //warnString gets put as the defaultValue and is given if there's nothing to return from sharedPrefs
        //this is not the most sexy way to do it, butI'll go with it for now
        val warnString = "warning"
        val jsonString = sharedPrefs.getString(context.getString(R.string.prefs_loops_key), warnString)
        if (jsonString != "warning") {
            loopsList = loopsListFromJson(jsonString)
        }
    }

    fun loopsListFromJson(jsonString: String): List<FileModel> {
        val gson = Gson()
        val fileModelsList = gson.fromJson(jsonString, FileModelsList::class.java)
        return fileModelsList.models
    }


    fun onStartClicked(view: View) {
        if (looper.hasLoopFile) looper.start()
    }

    fun onStopClicked(view: View) {
        looper.stop()
    }

    fun onPauseClicked(view: View) {
        if (looper.isPlaying()) looper.pause() else if (looper.isPaused) looper.start()
    }

    fun onSelectFolderClicked(view: View) {
        selectFolderListener.onSelectFolderClicked()
    }

    interface OnSelectFolderClickedListener {
        fun onSelectFolderClicked()
    }

    fun updateData() {
        adapter.updateData(loopsList)
        if (adapter.itemCount != 0) {
            emptyMessageVisibility.set(View.INVISIBLE)
        } else {
            emptyMessageVisibility.set(View.VISIBLE)
        }
        saveLoops()
    }

    fun onItemSelected(fm: FileModel, position: Int) {
        looper.setLoop(getApplication(), FileHelper.getSingleFile(fm.path))
        adapter.selectedPosition = position
        adapter.updateData(adapter.loopsList)
        looper.start()
    }
}
