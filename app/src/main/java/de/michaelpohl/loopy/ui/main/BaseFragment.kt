package de.michaelpohl.loopy.ui.main

import android.support.v4.app.Fragment

open class BaseFragment : Fragment() {
    var changeActionBarLayoutCallBack: ((Int) -> Unit)? = null

    fun changeActionBarLayout(resourceID: Int) {

        // I have to do it this way because apparently this field is not necessarily initialized at onResume()
        //This is all I can think of right now to solve this
        // I am sure there's a smarter solution
        if (changeActionBarLayoutCallBack != null) {
            changeActionBarLayoutCallBack!!.invoke(resourceID)
        }
    }
}