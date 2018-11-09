package de.michaelpohl.loopy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileModelsList
import de.michaelpohl.loopy.common.FileType
import de.michaelpohl.loopy.ui.main.FileBrowserFragment
import de.michaelpohl.loopy.ui.main.PlayerFragment
import de.michaelpohl.loopy.ui.main.PlayerViewModel
import timber.log.Timber

class MainActivity : AppCompatActivity(), FileBrowserFragment.OnItemClickListener,
    PlayerViewModel.OnSelectFolderClickedListener {

    private val defaultFilesPath = Environment.getExternalStorageDirectory().toString()
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Timber logging on for Debugging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("it works")
        }


        sharedPrefs = getSharedPreferences(
            resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            addPlayerFragment(loadSavedLoopsList().models)
        }
        setSupportActionBar(findViewById(R.id.my_toolbar))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_help -> {
            true
        }

        R.id.action_gear -> {

            true
        }

        R.id.action_browser -> {
            addFileFragment()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onFolderClicked(fileModel: FileModel) {

        if (fileModel.fileType == FileType.FOLDER) {
            addFileFragment(fileModel.path)
        }
    }

    override fun onFolderSelected(fileModel: FileModel) {
        if (fileModel.fileType == FileType.FOLDER) {
            if (fileModel.containsAudioFiles()) {
                addPlayerFragment(FileHelper.getFileModelsFromFiles(fileModel.getSubFiles()))
            }
        }
    }

    override fun onSelectFolderClicked() {
        addFileFragment()
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PlayerFragment.newInstance(loops), "player")
            .commit()
    }

    private fun addFileFragment(path: String = defaultFilesPath) {
        val filesListFragment = FileBrowserFragment.newInstance(path)
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.container, filesListFragment)
        fragmentTransaction.addToBackStack(path)
        fragmentTransaction.commit()
    }

    private fun clearBackStack() {
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
    }
}
