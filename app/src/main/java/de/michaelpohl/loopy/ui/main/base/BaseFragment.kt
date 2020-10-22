package de.michaelpohl.loopy.ui.main.base

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import de.michaelpohl.loopy.MainActivity
import de.michaelpohl.loopy.R
import kotlinx.android.synthetic.*

open class BaseFragment : Fragment() {

    open val showOptionsMenu = false
    open val screenTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addOnBackPressedCallback()
        setHasOptionsMenu(showOptionsMenu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setupActionBar(
            !showOptionsMenu,
            screenTitle ?: getString(R.string.appbar_title_player)
        )

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
    open fun onBackPressed(): Boolean {
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

    /**
     * Observe with Kotlin higher order functions and let the framework do lifecycle management
     */
    protected infix fun <T> LiveData<T>.observeWith(callback: (T) -> Unit) {
        observe(viewLifecycleOwner, Observer {
            callback(it)
        })
    }

    /* https://stackoverflow.com/questions/51060762/
 * Added the if check to prevent crashes, especially while testing (e.g. monkey)
 */
    private fun isDestinationSameAsCurrentDestination(destination: Int): Boolean {
        return findNavController().currentDestination?.id == destination
    }
}
