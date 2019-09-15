package de.michaelpohl.loopy.ui.main

import androidx.fragment.app.Fragment
import de.michaelpohl.loopy.R
import kotlinx.android.synthetic.*
import org.koin.core.KoinComponent

open class BaseFragment : Fragment(), KoinComponent {


    override fun onResume() {
        super.onResume()
        activity?.title = getTitle()
    }

    /**
     *do something when the back button is pressed
     *Override if interested
     *Return true to prevent activity from executing its onBackPressed
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    open fun getTitle(): String {
        return getString(R.string.appbar_title_player)
    }

    /**
     * Clears the findViewById cache when the view is destroyed to make sure we only ever adress valid views
     */
    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
    }
}