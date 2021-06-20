package com.michaelpohl.loopyplayer2

import android.Manifest
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
import com.michaelpohl.loopyplayer2.common.*
import com.michaelpohl.loopyplayer2.model.AppStateRepository
import com.michaelpohl.loopyplayer2.model.FilesRepository
import com.michaelpohl.loopyplayer2.ui.base.BaseFragment
import com.michaelpohl.loopyplayer2.ui.player.PlayerFragment
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener, KoinComponent {

    private val audioFilesRepo: FilesRepository by inject()
    private val appState: AppStateRepository by inject()

    private val defaultFilesPath = Environment.getExternalStorageDirectory().toString()

    private lateinit var drawer: DrawerLayout
    var currentFragment: BaseFragment? = null
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
            showMarkupViewerFragment(MarkDownFiles.getWhatsNewFileName(), R.string.title_whatsnew)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START)
            true
        }
        else super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // stop player when navigating away saves a lot of headaches
        if (currentFragment is PlayerFragment) (currentFragment as PlayerFragment).viewModel.onStopPlaybackClicked()

        when (item.itemId) {
            R.id.nav_browse_media -> {
                if (isPermitted()) showMediaStoreBrowserFragment() else PermissionHelper(this).checkPermissions()
            }
            R.id.nav_browse_storage -> {
                if (isPermitted()) showFileBrowserFragment() else PermissionHelper(this).checkPermissions()
            }
            R.id.nav_open_settings -> showSettings()
            R.id.nav_clear_player -> clearLoopsList()
            R.id.nav_help -> {
                showMarkupViewerFragment(MarkDownFiles.getHelpTextFileName(), R.string.title_help)
            }
            R.id.nav_about -> {
                showMarkupViewerFragment(MarkDownFiles.getAboutFileName(), R.string.title_about)
            }
            R.id.nav_whatsnew -> {
                showMarkupViewerFragment(MarkDownFiles.getWhatsNewFileName(), R.string.title_whatsnew)
            }
            R.id.nav_contact -> {
                DialogHelper(this).requestConfirmation(
                    getString(R.string.feedback_alert_title),
                    getString(R.string.feedback_alert_text)
                ) {

                    startActivity(Intent(Intent.ACTION_SEND).apply {
                        type = "application/octet-stream"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("google@michaelpohl.de"))
                    })
                }
            }
            else -> {
            } // do nothing
        }
        drawer.closeDrawers()

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
        // TODO
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

    private fun showMarkupViewerFragment(markupFileName: String, titleResource: Int) {
        val bundle = buildStringArgs(markupFileName).apply {
            putInt("title", titleResource)
        }

        nav_host_fragment.findNavController().navigate(
            R.id.markupViewerFragment, bundle
        )
    }

    private fun showSettings() {
        nav_host_fragment.findNavController().navigate(
            R.id.action_playerFragment_to_settingsFragment
        )
    }

    private fun clearLoopsList() {
        currentFragment?.let {
            if (currentFragment is PlayerFragment) {
                (currentFragment as PlayerFragment).clearLoops()
            } else {
                showSnackbar(container, R.string.snackbar_cant_clear_loops)
            }
            return
        } ?: showSnackbar(container, R.string.snackbar_error_message)
    }

    private fun keepScreenOnIfDesired(settings: Settings) {
        if (settings.keepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
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


