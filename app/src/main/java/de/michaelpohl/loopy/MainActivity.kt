package de.michaelpohl.loopy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import de.michaelpohl.loopy.common.*
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.BaseFragment
import de.michaelpohl.loopy.ui.main.browser.AlbumBrowserFragment
import de.michaelpohl.loopy.ui.main.browser.AlbumBrowserViewModel
import de.michaelpohl.loopy.ui.main.browser.FileBrowserFragment
import de.michaelpohl.loopy.ui.main.browser.FileBrowserViewModel
import de.michaelpohl.loopy.ui.main.help.HelpFragment
import de.michaelpohl.loopy.ui.main.mediabrowser.MusicBrowserFragment
import de.michaelpohl.loopy.ui.main.player.PlayerFragment
import de.michaelpohl.loopy.ui.main.player.PlayerViewModel
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), PlayerViewModel.PlayerActionsListener, AlbumBrowserViewModel.OnItemClickListener, FileBrowserViewModel.OnItemClickListener {


    private val defaultFilesPath = Environment.getExternalStorageDirectory().toString()
    private var menuResourceID = R.menu.menu_main
    private lateinit var currentFragment: BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Timber logging on when Debugging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        DataRepository.init(
            getSharedPreferences(resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        )

        setContentView(R.layout.main_activity)
        if (Intent.ACTION_VIEW == intent.action) {
            Timber.d("Intent")
            handleIntent()
        }

        if (savedInstanceState == null) {
            val permissionHelper = PermissionHelper(this)
            permissionHelper.checkPermissions()
            showPlayerFragment(DataRepository.currentSelectedAudioModels)
        }
        setSupportActionBar(findViewById(R.id.my_toolbar))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_help -> {
            if (currentFragment.tag == "help") {
                onBackPressed()
            } else {
                showHelpFragment()
            }
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
            // context for this action is the MusicBrowserFragment, but we handle it here because we need activity methods
            showPlayerFragmentWithFreshSelection()
            false
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(menuResourceID, menu)
        return true
    }

    override fun onFolderClicked(fileModel: FileModel) {
        if (fileModel.fileType == FileType.FOLDER) {
            showFileBrowserFragment(fileModel.path)
        }
    }

    override fun onOpenFileBrowserClicked() {
        showFileBrowserFragment()
    }

    override fun onBrowseMediaStoreClicked() {
        Timber.d("Browsing media store...")
        val mediaStoreItems = DataRepository.getMediaStoreEntries(this)
        mediaStoreItems.forEach { it -> Timber.d("Item: %s", it.name) }
        showAlbumBrowserFragment()
    }

    override fun onAlbumClicked(albumTitle: String) {
        Timber.d("Clicked on this one: %s", albumTitle)
        showMusicBrowserFragment(albumTitle)
    }

    override fun onBackPressed() {

        //apparently it is possible to come by here with currentFragment not being initialized
        //TODO: investigate
        if (::currentFragment.isInitialized && !currentFragment.onBackPressed()) {
            super.onBackPressed()
        }
        if (supportFragmentManager.backStackEntryCount == 0) {
//            finish()
        }
    }

    private fun handleIntent() {

        val uri = intent.data
        val inputStream = contentResolver.openInputStream(uri)
        val outputFile = File(this.cacheDir, "output.wav")
        val outputStream = FileOutputStream(outputFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        DataRepository.handleFileFromIntent(outputFile)
        showPlayerFragmentWithFreshSelection()
    }

    private fun showPlayerFragment(loops: List<AudioModel> = emptyList(), settings: Settings = Settings()) {

        //TODO this method can be better - handling what's in AppData should completely move into DataRepository
        val appData = AppData(audioModels = loops, settings = settings)
        val playerFragment = PlayerFragment.newInstance(appData)
        currentFragment = playerFragment
        playerFragment.changeActionBarLayoutCallBack = { it -> changeActionBar(it) }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, playerFragment, "player")
            .commit()
    }

    private fun showPlayerFragmentWithFreshSelection() {
        val didUpdate = DataRepository.updateAndSaveFileSelection()
        if (didUpdate) {
            clearBackStack()
            showPlayerFragment(DataRepository.currentSelectedAudioModels, DataRepository.settings)
            true
        } else {
            val snackbar = Snackbar.make(
                container,
                getString(R.string.snackbar_text_no_new_files_selected),
                Snackbar.LENGTH_LONG
            )
            snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.action))
            snackbar.show()
        }
    }

    private fun showFileBrowserFragment(path: String = defaultFilesPath) {
        val filesListFragment = FileBrowserFragment.newInstance(path)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        currentFragment = filesListFragment
        fragmentTransaction.replace(R.id.container, filesListFragment)
        fragmentTransaction.addToBackStack(path)
        fragmentTransaction.commit()
        changeActionBar(R.menu.menu_file_browser)
    }

    private fun showAlbumBrowserFragment() {
        val fragment = AlbumBrowserFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        currentFragment = fragment
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
        changeActionBar(R.menu.menu_file_browser)
    }

    private fun showMusicBrowserFragment(albumTitle: String) {
        val fragment = MusicBrowserFragment.newInstance(albumTitle)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        currentFragment = fragment
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
        changeActionBar(R.menu.menu_file_browser)
    }

    private fun showHelpFragment() {
        val helpFragment = HelpFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        currentFragment = helpFragment
        fragmentTransaction.replace(R.id.container, helpFragment, "help")
        fragmentTransaction.addToBackStack("help")
        fragmentTransaction.commit()
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
}


