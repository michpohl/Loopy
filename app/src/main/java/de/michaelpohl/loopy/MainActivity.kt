package de.michaelpohl.loopy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileModelsList
import de.michaelpohl.loopy.common.FileType
import de.michaelpohl.loopy.ui.main.FileBrowserFragment
import de.michaelpohl.loopy.ui.main.FileBrowserViewModel
import de.michaelpohl.loopy.ui.main.PlayerFragment
import de.michaelpohl.loopy.ui.main.PlayerViewModel
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), FileBrowserViewModel.OnItemClickListener,
    PlayerViewModel.PlayerActionsListener {

    private val defaultFilesPath = Environment.getExternalStorageDirectory().toString()
    private lateinit var sharedPrefs: SharedPreferences
    private var menuResourceID = R.menu.menu_main
    private val currentSelectedFileModels = mutableListOf<FileModel>()
    private val newSelectedFileModels = mutableListOf<FileModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Timber logging on for Debugging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }


        sharedPrefs = getSharedPreferences(
            resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        currentSelectedFileModels.addAll(loadSavedLoopsList().models)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            addPlayerFragment(currentSelectedFileModels)
        }
        setSupportActionBar(findViewById(R.id.my_toolbar))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_help -> {
            //TODO show help fragment
            true
        }

        R.id.action_gear -> {
            //handled in PlayerFragment
            false
        }

        R.id.action_browser -> {
            //handled in PlayerFragment
            false
        }

        R.id.action_submit -> {
            val didUpdate = updateAndSaveFileSelection()
            if (didUpdate) {
                clearBackStack()
                addPlayerFragment(currentSelectedFileModels)
                true
            } else {
                val snackbar = Snackbar.make(
                    container,
                    getString(R.string.snackbar_text_no_new_files_selected),
                    Snackbar.LENGTH_LONG
                )
                snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.action))
                snackbar.show()
                false
            }
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(menuResourceID, menu)
        return true
    }

    override fun onFolderClicked(fileModel: FileModel) {
        if (fileModel.fileType == FileType.FOLDER) {
            addFileFragment(fileModel.path)
        }
    }

    override fun onFileSelectionUpdated(newSelection: List<FileModel>) {
        //clear list to prevent adding doubles or unwanted items (since this gets updated with every click)
        newSelectedFileModels.clear()
        //only add the ones that are not already selected
        newSelectedFileModels.addAll(newSelection.filter { it -> !currentSelectedFileModels.contains(it) })
    }

    override fun onOpenFileBrowserClicked() {
        addFileFragment()
    }

    override fun onLoopsListCleared() {
        currentSelectedFileModels.clear()
        saveLoops(FileModelsList(currentSelectedFileModels))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0) {
//            finish()
        }
    }

    fun saveLoops(list: FileModelsList) {
        val jsonString = Gson().toJson(list)

//        TODO put fitting assertion
//        Assert.assertEquals(jsonString, """{"id":1,"description":"Test"}""")

        with(sharedPrefs.edit()) {
            putString(resources.getString(de.michaelpohl.loopy.R.string.prefs_loops_key), jsonString)
            commit()
        }
    }

    private fun loadSavedLoopsList(): FileModelsList {

        //warnString is put as the defaultValue and is given if there's nothing to return from sharedPrefs
        //this is not the most sexy way to do it, butI'll go with it for now
        //TODO improve this
        val warnString = "warning"
        val jsonString = sharedPrefs.getString(getString(R.string.prefs_loops_key), warnString)

        //TODO take the FileModelList and test its integrity (do the files still exist?)

        return if (jsonString != "warning") {
            fileModelsListFromJson(jsonString)
        } else FileModelsList(arrayListOf())
    }

    private fun fileModelsListFromJson(jsonString: String): FileModelsList {
        return Gson().fromJson(jsonString, FileModelsList::class.java)
    }

    private fun addPlayerFragment(loops: List<FileModel> = emptyList()) {
        if (!loops.isEmpty()) {
            saveLoops(FileModelsList(loops))
        }
        clearBackStack()
        val playerFragment = PlayerFragment.newInstance(loops)
        playerFragment.changeActionBarLayoutCallBack = { it -> changeActionBar(it) }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, playerFragment, "player")
            .commit()
    }

    private fun addFileFragment(path: String = defaultFilesPath) {
        val filesListFragment = FileBrowserFragment.newInstance(path)
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.container, filesListFragment)
        fragmentTransaction.addToBackStack(path)
        fragmentTransaction.commit()
        changeActionBar(R.menu.menu_browser)
    }

    private fun clearBackStack() {
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
    }

    private fun changeActionBar(resourceID: Int) {
        menuResourceID = resourceID
        invalidateOptionsMenu()
    }

    private fun updateAndSaveFileSelection(): Boolean {
        if (!newSelectedFileModels.isEmpty()) {
            currentSelectedFileModels.addAll(newSelectedFileModels)
            saveLoops(FileModelsList(currentSelectedFileModels))
            return true
        } else return false
    }
}
