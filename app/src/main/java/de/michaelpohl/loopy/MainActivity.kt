package de.michaelpohl.loopy

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import de.michaelpohl.loopy.common.FileHandler
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import de.michaelpohl.loopy.ui.main.FilesListFragment
import de.michaelpohl.loopy.ui.main.PlayerFragment
import timber.log.Timber

class MainActivity : AppCompatActivity(), FilesListFragment.OnItemClickListener {

    //TODO this is a constant one as a starting point. Improve handling this situaltion, please
    private val defaultFilesPath = Environment.getExternalStorageDirectory().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Timber logging on for Debugging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("it works")
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("it works")
        }

        setContentView(R.layout.main_activity)
        //todo return to this
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, PlayerFragment.newInstance())
//                .commitNow()
//        }

        if (savedInstanceState == null) {
            addFileFragment()
        }

        Timber.d("Timber works!")
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

    override fun onLongClick(fileModel: FileModel) {
        val fileHandler= FileHandler()
        if (fileModel.fileType == FileType.FOLDER) {
            if (fileHandler.containsAudioFiles(fileModel.path)) {
                Timber.d("contains wave")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    private fun addFileFragment(path: String = defaultFilesPath) {
        Timber.d("adding File Fragment: %s", path)
        val filesListFragment = FilesListFragment.newInstance(path)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, filesListFragment)
        fragmentTransaction.addToBackStack(path)
        fragmentTransaction.commit()
    }

}
