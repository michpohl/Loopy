package de.michaelpohl.loopy

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.michaelpohl.loopy.ui.main.MainFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("it works")
        }

        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
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

}
