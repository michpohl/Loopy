package de.michaelpohl.loopy.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.michaelpohl.loopy.R
import kotlinx.android.synthetic.*
import timber.log.Timber

open class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addOnBackPressedCallback()
    }

    private fun addOnBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!onBackPressed()) {
                    findNavController().navigateUp()
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getTitle()
    }

    open fun getTitle(): String {
        return getString(R.string.appbar_title_player)
    }

    /**
     * Override and return true if you want to do something else than navigate back
     */
    open fun onBackPressed() : Boolean {
        return false
    }

    /**
     * Clears the findViewById cache when the view is destroyed to make sure we only ever adress valid views
     */
    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
    }


    /**
     * Convenience method for Navigation
     */
    protected open fun navigateTo(destination: Int, arguments: Bundle? = null) {
        if (!isDestinationSameAsCurrentDestination(destination)) {
            findNavController().navigate(destination, arguments)
        }
    }

    /* https://stackoverflow.com/questions/51060762/
 * Added the if check to prevent crashes, especially while testing (e.g. monkey)
 */
    private fun isDestinationSameAsCurrentDestination(destination: Int): Boolean {
        return findNavController().currentDestination?.id == destination
    }
}
