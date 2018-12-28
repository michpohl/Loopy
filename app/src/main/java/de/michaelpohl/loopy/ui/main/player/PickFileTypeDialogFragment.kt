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
import hugo.weaving.DebugLog
import kotlinx.android.synthetic.main.dialog_pick_filetypes.*
import timber.log.Timber

@DebugLog
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

        view.findViewById<CheckBox>(R.id.cb_check_wav).setOnClickListener { onCheckBoxWavClicked() }
        view.findViewById<CheckBox>(R.id.cb_check_mp3).setOnClickListener { onCheckBoxMp3Clicked() }
        view.findViewById<CheckBox>(R.id.cb_check_ogg).setOnClickListener { onCheckBoxOggClicked() }
        view.findViewById<Button>(R.id.btn_ok).setOnClickListener { onOkClicked() }
        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener { onCancelClicked() }



        return view
    }

    fun setCurrentSettings(currentSettings: Settings) {
        this.settings = currentSettings
        this.allowedFileTypes = currentSettings.allowedFileTypes.toMutableList()
    }

    //TODO this whole class is spaghetti. Maybe it should have a viewModel to make it nicer. If you feel like it

    private fun onCheckBoxWavClicked() {
        val fileType = ValidAudioFileType.WAVE
        if (cb_check_wav.isChecked) {
            allow(fileType)
        } else {
            forbid(fileType)
        }
    }

    private fun onCheckBoxMp3Clicked() {
        val fileType = ValidAudioFileType.MP3
        if (cb_check_mp3.isChecked) {
            allow(fileType)
        } else {
            forbid(fileType)
        }
    }

    private fun onCheckBoxOggClicked() {
        val fileType = ValidAudioFileType.OGG

        if (cb_check_ogg.isChecked) {
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
        Timber.d("Filetype I try to allow: %s Allowed right now:", fileType.suffix)
        allowedFileTypes.forEach { Timber.d("%s", it.suffix) }

        if (!allowedFileTypes.contains(fileType)) {
            Timber.d("Allowing: %s", fileType.suffix)
            allowedFileTypes.add(fileType)
        }
    }

    private fun forbid(fileType: ValidAudioFileType) {
        Timber.d("Filetype I try to forbid: %s Allowed right now:", fileType.suffix)

        allowedFileTypes.forEach { Timber.d("%s", it.suffix) }

        if (allowedFileTypes.contains(fileType)) {
            Timber.d("Forbidding: %s", fileType.suffix)
            allowedFileTypes.remove(fileType)
        }
    }
}