package de.michaelpohl.loopy.ui.main.player

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.Settings
import de.michaelpohl.loopy.common.ValidAudioFileType
import kotlinx.android.synthetic.main.dialog_pick_filetypes.*
import timber.log.Timber

class PickFileTypeDialogFragment : DialogFragment() {

    private lateinit var settings: Settings
    private lateinit var allowedFileTypes: MutableList<ValidAudioFileType>
    lateinit var resultListener: (Settings) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.dialog_pick_filetypes, container, false)

        view.findViewById<CheckBox>(R.id.cb_check_wav).isChecked =
                allowedFileTypes.contains(ValidAudioFileType.WAVE)
        view.findViewById<CheckBox>(R.id.cb_check_mp3).isChecked =
                allowedFileTypes.contains(ValidAudioFileType.MP3)
        view.findViewById<CheckBox>(R.id.cb_check_ogg).isChecked =
                allowedFileTypes.contains(ValidAudioFileType.OGG)

        view.findViewById<CheckBox>(R.id.cb_check_wav).setOnClickListener { onCheckBoxClicked(ValidAudioFileType.WAVE) }
        view.findViewById<CheckBox>(R.id.cb_check_mp3).setOnClickListener { onCheckBoxClicked(ValidAudioFileType.MP3) }
        view.findViewById<CheckBox>(R.id.cb_check_ogg).setOnClickListener { onCheckBoxClicked(ValidAudioFileType.OGG) }
        view.findViewById<Button>(R.id.btn_ok).setOnClickListener { onOkClicked() }
        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener { onCancelClicked() }


        return view
    }

    fun setCurrentSettings(currentSettings: Settings) {
        this.settings = currentSettings
        this.allowedFileTypes = currentSettings.allowedFileTypes.toMutableList()
    }

    private fun onCheckBoxClicked(fileType: ValidAudioFileType) {
        if (cb_check_wav.isChecked) {
            allow(fileType)
        } else {
            forbid(fileType)
        }
    }

    private fun onOkClicked() {
        Timber.d("Clicked on OK. These are my picked allowed types:")
        settings.allowedFileTypes = allowedFileTypes.toTypedArray()
        settings.allowedFileTypes.forEach { Timber.d("%s", it.suffix) }
        resultListener.invoke(settings)
        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }

    private fun allow(fileType: ValidAudioFileType) {
        if (!allowedFileTypes.contains(fileType)) {
            allowedFileTypes.add(fileType)
        }
    }

    private fun forbid(fileType: ValidAudioFileType) {
        if (allowedFileTypes.contains(fileType)) {
            allowedFileTypes.remove(fileType)
        }
    }
}