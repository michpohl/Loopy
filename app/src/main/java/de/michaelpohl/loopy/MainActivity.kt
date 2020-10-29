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
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import de.michaelpohl.loopy.common.*
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.model.AudioFilesRepository
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.model.SharedPreferencesManager
import de.michaelpohl.loopy.ui.main.base.BaseFragment
import de.michaelpohl.loopy.ui.main.player.PlayerFragment
import de.michaelpohl.loopy.ui.main.player.PlayerViewModel
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class MainActivity : AppCompatActivity(), PlayerViewModel.PlayerActionsListener,
    NavigationView.OnNavigationItemSelectedListener, KoinComponent {

    private val audioFilesRepo: AudioFilesRepository by inject()
    private val prefs: SharedPreferencesManager by inject()
    private val appState: AppStateRepository by inject()

    private val defaultFilesPath = Environment.getExternalStorageDirectory().toString()
    private var menuResourceID = R.menu.menu_main

    private lateinit var drawer: DrawerLayout
    private lateinit var currentFragment: BaseFragment
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        container = findViewById(R.id.outer_layout)
        if (savedInstanceState == null) {
            val permissionHelper = PermissionHelper(this)
            permissionHelper.checkPermissions()
        }
        setupAppData()
        setupNavigation()
        initDataRepository()
        handlePossibleIntents()
        setupDrawer()
        keepScreenOnIfDesired(appState.settings)
    }

    private fun setupAppData() {
        Timber.d("is App set up? ${appState.isSetupComplete}")
        // if the standard folder has not been created yet, we do so, and on success set isAppSetup to true
        // on Failure, whatever the reason might be, it stays false and will run again next startup
        if (!appState.isSetupComplete) {
            val setupComplete = audioFilesRepo.autoCreateStandardLoopSet()
            Timber.d("Setup complete? $setupComplete")
            appState.isSetupComplete = setupComplete
            Handler().postDelayed({
                Timber.d("is App setup now? ${appState.isSetupComplete}")
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

    fun setupActionBar(
        withBackButton: Boolean = false,
        titleString: String = getString(R.string.appbar_title_player)
    ) {
        val toolBar = findViewById<Toolbar>(R.id.tb_toolbar)
        toolBar.apply {
            title = titleString
            setSupportActionBar(toolBar)
            navigationIcon =
                getDrawable(if (withBackButton) R.drawable.ic_back else R.drawable.ic_settings)
            setNavigationOnClickListener {
                if (withBackButton) {
                    onBackPressed()
                } else {
                    drawer.openDrawer(GravityCompat.START)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume in activity")
//        if (!::currentFragment.isInitialized) {
//            currentFragment = StateHelper.currentFragment!! // TODO what is this for, do we need it?
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOpenFileBrowserClicked() {
        showFileBrowserFragment()
    }

    override fun onBrowseMediaStoreClicked() {
        showMediaStoreBrowserFragment()
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
                if (isPermitted()) showMediaStoreBrowserFragment() else PermissionHelper(this).checkPermissions()
            }
            R.id.nav_browse_storage -> {
                clearBackStack()
                if (isPermitted()) showFileBrowserFragment() else PermissionHelper(this).checkPermissions()
            }
            R.id.nav_open_settings -> showSettings()
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
        currentFragment = nav_host_fragment.childFragmentManager.fragments[0] as BaseFragment

        // returning false suppresses the visual checking of clicked items. We don't need it so we return false
        return false
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

    private fun handleIntent() {
//        Timber.d("Handling intent, maybe...")
//        val uri = intent.data
//        Timber.d("My intent's Uri: %s", uri)
//        Timber.d("My intent's path: %s", uri?.path)
//
//        val inputStream = contentResolver.openInputStream(uri)
//        val outputFile = File(this.cacheDir, "output.wav")
//        val outputStream = FileOutputStream(outputFile)
//        inputStream.use { input ->
//            outputStream.use { output ->
//                input.copyTo(output)
//            }
//        }
//        DataRepository.onAudioFileSelectionUpdated(
//            DataRepository.getMediaStoreEntries(this).filter { it.path == uri.path })
//
//        showPlayerFragmentWithFreshSelection()
    }

    private fun showFileBrowserFragment(path: String = defaultFilesPath) {
        nav_host_fragment.findNavController().navigate(
            R.id.action_playerFragment_to_fileBrowserFragment, buildStringArgs(path)
        )
    }

    private fun showMediaStoreBrowserFragment() {
        nav_host_fragment.findNavController().navigate(
            R.id.action_playerFragment_to_mediaStoreBrowserFragment
        )
    }

    private fun showMarkupViewerFragment(markupFileName: String) {
        nav_host_fragment.findNavController().navigate(
            R.id.markupViewerFragment, buildStringArgs(markupFileName)
        )
    }

    private fun showSettings() {
        nav_host_fragment.findNavController().navigate(
            R.id.action_playerFragment_to_settingsFragment
        )

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
            }
        } else {

            showSnackbar(container, R.string.snackbar_cant_clear_loops)
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

    private fun keepScreenOnIfDesired(settings: Settings) {
        if (settings.keepScreenOn) {
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


