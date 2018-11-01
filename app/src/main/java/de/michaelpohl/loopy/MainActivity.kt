package de.michaelpohl.loopy

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import de.michaelpohl.loopy.ui.main.FilesListFragment
import de.michaelpohl.loopy.ui.main.PlayerFragment
import de.michaelpohl.loopy.ui.main.PlayerViewModel
import timber.log.Timber

class MainActivity : AppCompatActivity(), FilesListFragment.OnItemClickListener,
    PlayerViewModel.OnSelectFolderClickedListener {

    //TODO this is a constant one as a starting point. Improve handling this situation, please
    private val defaultFilesPath = Environment.getExternalStorageDirectory().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Timber logging on for Debugging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("it works")
        }


        setContentView(R.layout.main_activity)
        //todo return to this
        if (savedInstanceState == null) {
            addPlayerFragment()
        }
    }

    //TODO these are from audioguide, how did that work again
//    override fun addFragment(fragment: Fragment, targetView: Int, addToBackStack: Boolean, tag: String) {
//        getFragment(addToBackStack, mFragmentManager) { add(targetView, fragment, tag) }
//        Timber.v("added Fragment %s to Backstack. Backstack count is: %s", fragment, mFragmentManager.getBackStackEntryCount())
//    }
//
//    override fun replaceFragment(fragment: Fragment, targetView: Int, addToBackStack: Boolean, tag: String) {
//        getFragment(addToBackStack, mFragmentManager) { replace(targetView, fragment, tag) }
//        Timber.v("added Fragment %s to Backstack. Backstack count is: %s", fragment, mFragmentManager.getBackStackEntryCount())
//    }
//
//    override fun goBack() {
//        onBackPressed()
//    }

    override fun onClick(fileModel: FileModel) {
        Timber.d("onClick")
        Timber.d("Trying to add fragment with this path: %s", fileModel)
        if (fileModel.fileType == FileType.FOLDER) {
            addFileFragment(fileModel.path)
        }
    }

    override fun onSelected(fileModel: FileModel) {
        if (fileModel.fileType == FileType.FOLDER) {
            if (FileHelper.containsAudioFiles(fileModel.path)) {
                addPlayerFragment(FileHelper.getFileModelsFromFiles(FileHelper.getFilesFromPath(fileModel.path)))
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

    private fun addPlayerFragment(loops: List<FileModel> = emptyList()) {
        Timber.d("AddingPlayerFragment with loopsList: %s", loops)
        clearBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PlayerFragment.newInstance(loops), "player")
            .commit()
    }

    private fun addFileFragment(path: String = defaultFilesPath) {
        Timber.d("adding File Fragment: %s", path)
        val filesListFragment = FilesListFragment.newInstance(path)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, filesListFragment)
        fragmentTransaction.addToBackStack(path)
        fragmentTransaction.commit()
    }

    private fun clearBackStack() {
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
            Timber.d("popping backStack")
        }
    }
}
