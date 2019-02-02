package de.michaelpohl.loopy.common

import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import de.michaelpohl.loopy.R
import timber.log.Timber

class DialogHelper(private val activity: FragmentActivity) {

    /**
     * Clumsy method to show a Dialog where needed. Note that this uses a custom layout
     */
    fun requestConfirmation(header: String, content: String, positiveAction: () -> Unit) {
        val alertDialog = AlertDialog.Builder(activity, R.style.Dialog)
            .setTitle(header)
            .setMessage(content)
            .setPositiveButton(activity.getString(R.string.ok)) { dialog, i ->
                positiveAction()
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { dialogInterface, i ->
                Timber.d("negative")
            }
        alertDialog.show()
    }
}