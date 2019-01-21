package de.michaelpohl.loopy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import de.michaelpohl.loopy.common.*
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.BaseFragment
import de.michaelpohl.loopy.ui.main.help.MarkupViewerFragment
import de.michaelpohl.loopy.ui.main.media_browser.MusicBrowserFragment
import de.michaelpohl.loopy.ui.main.player.SettingsDialogFragment
import de.michaelpohl.loopy.ui.main.player.PlayerFragment
import de.michaelpohl.loopy.ui.main.player.PlayerViewModel
import de.michaelpohl.loopy.ui.main.storage_browser.AlbumBrowserFragment
import de.michaelpohl.loopy.ui.main.storage_browser.BrowserViewModel
import de.michaelpohl.loopy.ui.main.storage_browser.FileBrowserFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), PlayerViewModel.PlayerActionsListener,
    BrowserViewModel.OnBrowserActionListener,
    NavigationView.OnNavigationItemSelectedListener {


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
        setSupportActionBar(toolbar)

        //drawer
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.app_name, R.string.app_name
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            val permissionHelper = PermissionHelper(this)
            permissionHelper.checkPermissions()
            showPlayerFragment(DataRepository.currentSelectedAudioModels)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_browse_media -> {
                clearBackStack()
                showAlbumBrowserFragment()
            }
            R.id.nav_browse_storage -> {
                clearBackStack()
                showFileBrowserFragment()
            }
            R.id.nav_open_settings -> showPickFileTypesDialog()
            R.id.nav_help -> showMarkupViewerFragment("help.md")
            R.id.nav_about -> showMarkupViewerFragment("about.md")
            else -> {
            } // do nothing
        }
        drawer_layout.closeDrawers()

        // returning false suppresses the visual checking of clicked items. We don't need it so we return false
        return false
    }

    override fun acceptSubmittedSelection() {
        showPlayerFragmentWithFreshSelection()
    }

    private fun handleIntent() {
        Timber.d("Handling intent, maybe...")
        val uri = intent.data
        Timber.d("My intent's Uri: %s", uri)
        Timber.d("My intent's path: %s", uri.path)

        val inputStream = contentResolver.openInputStream(uri)
        val outputFile = File(this.cacheDir, "output.wav")
        val outputStream = FileOutputStream(outputFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        DataRepository.onAudioFileSelectionUpdated(
            DataRepository.getMediaStoreEntries(this).filter { it.path == uri.path })

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
//    TODO refactor all the show() methods into something generic

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
        fragmentTransaction.addToBackStack("album_browser")
        fragmentTransaction.commit()
        changeActionBar(R.menu.menu_file_browser)
    }

    private fun showMusicBrowserFragment(albumTitle: String) {
        val fragment = MusicBrowserFragment.newInstance(albumTitle)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        currentFragment = fragment
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack("music_browser")
        fragmentTransaction.commit()
        changeActionBar(R.menu.menu_file_browser)
    }

    private fun showMarkupViewerFragment(markupFileName: String) {
        val helpFragment = MarkupViewerFragment.newInstance(markupFileName)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        currentFragment = helpFragment
        fragmentTransaction.replace(R.id.container, helpFragment, "markup")
        fragmentTransaction.addToBackStack("markup")
        fragmentTransaction.commit()
    }

    private fun showPickFileTypesDialog() {
        val dialog = SettingsDialogFragment()
        dialog.setCurrentSettings(DataRepository.settings)
        dialog.resultListener = {
            DataRepository.settings = it
            DataRepository.saveCurrentState()
//            viewModel.updateData()
        }
        dialog.show(supportFragmentManager, "pick-filetypes")
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


