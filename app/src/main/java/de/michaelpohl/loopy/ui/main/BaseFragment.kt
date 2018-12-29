package de.michaelpohl.loopy.ui.main

import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import de.michaelpohl.loopy.R
import kotlinx.android.synthetic.*

open class BaseFragment : Fragment() {

    var changeActionBarLayoutCallBack: ((Int) -> Unit)? = null

    /**
     * Changes the layout resource for the action bar
     * @param resourceID
     */
    fun changeActionBarLayout(resourceID: Int) {

        // I have to do it this way because apparently this field is not necessarily initialized at onResume()
        //This is all I can think of right now to solve this
        // I am sure there's a smarter solution
        if (changeActionBarLayoutCallBack != null) {
            changeActionBarLayoutCallBack!!.invoke(resourceID)
        }
    }

    /**
     *do something when the back button is pressed
     *Override if interested
     *Return true to prevent activity from executing its onBackPressed
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    //TODO snackBar maker is WIP
    fun showSnackbar(view: View, message: String, clickListener: View.OnClickListener? = null) {
        //Snackbar(view)
        val snackbar = Snackbar.make(
            view, message,
            Snackbar.LENGTH_LONG
        ).setAction("Action", clickListener)
        snackbar.setActionTextColor(ContextCompat.getColor(context!!, R.color.darkest_green))
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.action))
        val textView =
            snackbarView.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        textView.textSize = 28f
        snackbar.show()
    }

    /**
     * Clears the findViewById cache when the view is destroyed to make sure we only ever adress valid views
     */
    override fun onDestroyView() {
        super.onDestroyView()
        clearFindViewByIdCache()
    }
}