package com.michaelpohl.loopyplayer2.ui.base

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.michaelpohl.loopyplayer2.MainActivity
import com.michaelpohl.loopyplayer2.R
import kotlinx.android.synthetic.*

abstract class BaseFragment : Fragment() {

    private val onBackPressedCallback = object :
        OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!onBackPressed()) {
                findNavController().navigateUp()
            }
        }
    }

    open val showOptionsMenu = false
    open val titleResource: Int? = null

    abstract val viewModel: BaseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addOnBackPressedCallback()
        setHasOptionsMenu(showOptionsMenu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun addOnBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as MainActivity).currentFragment = this
            (it as MainActivity).setupActionBar(
                !showOptionsMenu,
                getString(titleResource ?: R.string.appbar_title_player))
        }
        viewModel.onFragmentResumed()
    }

    open fun getTitle(): String {
        return getString(titleResource ?: R.string.appbar_title_player)
    }

    /**
     * Override and return true if you want to do something else than navigate back
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    override fun onPause() {
        super.onPause()
        viewModel.onFragmentPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
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

    private fun isDestinationSameAsCurrentDestination(destination: Int): Boolean {
        return findNavController().currentDestination?.id == destination
    }
}
