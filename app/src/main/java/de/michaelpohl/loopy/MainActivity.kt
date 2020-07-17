package de.michaelpohl.loopy

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import de.michaelpohl.loopy.common.*
import de.michaelpohl.loopy.model.AudioFilesRepository
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.model.SharedPreferencesManager
import de.michaelpohl.loopy.ui.main.BaseFragment
import de.michaelpohl.loopy.ui.main.filebrowser.BrowserViewModel
import de.michaelpohl.loopy.ui.main.player.PlayerFragment
import de.michaelpohl.loopy.ui.main.player.PlayerViewModel
import de.michaelpohl.loopy.ui.main.player.SettingsDialogFragment
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), PlayerViewModel.PlayerActionsListener,
    BrowserViewModel.OnBrowserActionListener,
    NavigationView.OnNavigationItemSelectedListener, KoinComponent {

    private val audioFilesRepo: AudioFilesRepository by inject()
    private val prefs: SharedPreferencesManager by inject()

    private val defaultFilesPath = Environment.getExternalStorageDirectory().toString()
    private var menuResourceID = R.menu.menu_main

    private lateinit var drawer: DrawerLayout
    private lateinit var currentFragment: BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            val permissionHelper = PermissionHelper(this)
            permissionHelper.checkPermissions()
        }
        setupTimber()
        setupAppData()
        setupNavigation()
        initDataRepository()
        handlePossibleIntents()
        setupActionBar()
        setupDrawer()
        keepScreenOnIfDesired()
    }

    private fun setupAppData() {
        Timber.d("is App setup? ${prefs.setupComplete}")
        // if the standard folder has not been created yet, we do so, and on success set isAppSetup to true
        // on Failure, whatever the reason might be, it stays false and will run again next startup
        if (!prefs.setupComplete) {
            val setupComplete = audioFilesRepo.autoCreateStandardLoopSet()
            Timber.d("Setup complete? $setupComplete")
            prefs.setupComplete = setupComplete
            Handler().postDelayed({
                Timber.d("is App setup now? ${prefs.setupComplete}")
            }, 500)
        }
    }

    private fun handlePossibleIntents() {
        if (Intent.ACTION_VIEW == intent.action) {
            Timber.d("Intent")
            handleIntent()
        }
    }

    private fun initDataRepository() {
        DataRepository.init(
            getSharedPreferences(
                resources.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
        )
    }

    private fun setupNavigation() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navigationView.setupWithNavController(navController)
    }

    private fun setupDrawer() {
        drawer = drawer_layout as DrawerLayout
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupActionBar() {
        val toolBar = tb_toolbar
        setSupportActionBar(toolBar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_settings)
        }
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume in activity")
        if (!::currentFragment.isInitialized) {
            currentFragment = StateHelper.currentFragment ?: BaseFragment() //why this?
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
        supportFragmentManager.popBackStack()
        if (supportFragmentManager.backStackEntryCount == 0) {
            //            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_browse_media -> {
                clearBackStack()
                if (isPermitted()) showAlbumBrowserFragment() else PermissionHelper(this).checkPermissions()
            }
            R.id.nav_browse_storage -> {
                clearBackStack()
                if (isPermitted()) showFileBrowserFragment() else PermissionHelper(this).checkPermissions()
            }
            R.id.nav_open_settings -> showSettingsDialog()
            R.id.nav_clear_player -> clearLoopsList()
            R.id.nav_help -> {
                val am = AssetManager()
                showMarkupViewerFragment(am.getHelpTextResource())
            }
            R.id.nav_about -> {
                val am = AssetManager()
                showMarkupViewerFragment(am.getAboutTextResource())
            }
            else -> {
            } // do nothing
        }
        drawer.closeDrawers()

        // returning false suppresses the visual checking of clicked items. We don't need it so we return false
        return false
    }

    override fun acceptSubmittedSelection() {
        showPlayerFragmentWithFreshSelection()
    }

    fun showSnackbar(
        view: View,
        messageResource: Int,
        lenght: Int = Snackbar.LENGTH_SHORT,
        backgroundColorResource: Int? = R.color.action

    ) {
        val snackbar = Snackbar.make(
            view,
            getString(messageResource),
            lenght
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, backgroundColorResource!!))
        snackbar.show()
    }

    /**
     * this gets called in PlayerFragment's onResume() to make sure it always is the currentFragment
     * when it is open. That is necessary for some functionality and this way is a convenient shortcut
     */
    fun updateCurrentFragment(fragment: PlayerFragment) {
        Timber.d("Putting Playerfragment back into place")
        currentFragment = fragment
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

    private fun showPlayerFragment(
        loops: List<AudioModel> = emptyList(),
        settings: Settings = Settings()
    ) {

        //        //TODO this method can be better - handling what's in AppData should completely move into DataRepository
        //        val appData = AppData(audioModels = loops, settings = settings)
        //        val playerFragment = PlayerFragment.newInstance(appData)
        //        Timber.d("currentFragment should get assigned")
        //        currentFragment = playerFragment
        //        StateHelper.currentFragment = currentFragment
        //        playerFragment.onResumeListener = this::updateCurrentFragment
        //        supportFragmentManager.beginTransaction()
        //            .replace(R.id.container, playerFragment, "player")
        //            .commit()
    }

    private fun showPlayerFragmentWithFreshSelection() {
        val didUpdate = DataRepository.updateAndSaveFileSelection()
        if (didUpdate) {
            clearBackStack()
            showPlayerFragment(DataRepository.currentSelectedAudioModels, DataRepository.settings)
            true
        } else {
            showSnackbar(container, R.string.snackbar_text_no_new_files_selected)
        }
    }
    //    TODO refactor all the show() methods into something generic

    private fun showFileBrowserFragment(path: String = defaultFilesPath) {
        nav_host_fragment.findNavController().navigate(
            R.id.fileBrowserFragment, buildStringArgs(path)
        )
    }

    private fun showAlbumBrowserFragment() {
        nav_host_fragment.findNavController().navigate(
            R.id.albumBrowserFragment
        )
    }

    private fun showMusicBrowserFragment(albumTitle: String) {
        nav_host_fragment.findNavController().navigate(
            R.id.musicBrowserFragment, buildStringArgs(albumTitle)
        )
    }

    private fun showMarkupViewerFragment(markupFileName: String) {
        nav_host_fragment.findNavController().navigate(
            R.id.markupViewerFragment, buildStringArgs(markupFileName)
        )
    }

    private fun showSettingsDialog() {
        val dialog = SettingsDialogFragment()
        dialog.setCurrentSettings(DataRepository.settings)
        dialog.resultListener = {
            Timber.d("Resultlistener was invoked")
            DataRepository.settings = it
            DataRepository.saveCurrentState()
            keepScreenOnIfDesired()

            // if we're currently in the player we need to update
            // immediately for the settings to take effect
            updatePlayerIfCurrentlyShowing()
        }
        dialog.show(supportFragmentManager, "settings-dialog")
    }

    private fun clearLoopsList() {
        if (!::currentFragment.isInitialized) {
            showSnackbar(container, R.string.snackbar_error_message)
            return
        }
        if (currentFragment is PlayerFragment) {
            Timber.d("We say it's initialized")
            val dialogHelper = DialogHelper(this)
            dialogHelper.requestConfirmation(
                getString(R.string.dialog_clear_list_header),
                getString(R.string.dialog_clear_list_content)
            ) {
                DataRepository.clearLoopsList()
                updatePlayerIfCurrentlyShowing()
            }
        } else {

            showSnackbar(container, R.string.snackbar_cant_clear_loops)
        }
    }

    private fun updatePlayerIfCurrentlyShowing() {
        if (currentFragment is PlayerFragment) {
            (currentFragment as PlayerFragment).updateViewModel()
        }
    }

    private fun clearBackStack() {
        if (currentFragment is PlayerFragment) stopPlaybackIfDesired(currentFragment as PlayerFragment)
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
    }

    private fun changeActionBar(resourceID: Int) {
        menuResourceID = resourceID
        invalidateOptionsMenu()
    }

    private fun keepScreenOnIfDesired() {
        if (DataRepository.settings.keepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun stopPlaybackIfDesired(playerFragment: PlayerFragment) {
        playerFragment.pausePlayback()
    }

    private fun isPermitted(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun buildStringArgs(string: String): Bundle {
        return Bundle().apply {
            putString("string", string)
        }
    }
}


