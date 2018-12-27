package de.michaelpohl.loopy.ui.main.player

import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import de.michaelpohl.loopy.R

class PickFileTypeDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Do all the stuff to initialize your custom view

        return inflater.inflate(R.layout.dialog_pick_filetypes, container, false)
    }
}