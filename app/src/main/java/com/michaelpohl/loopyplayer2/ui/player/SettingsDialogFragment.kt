package com.michaelpohl.loopyplayer2.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.FileType
import com.michaelpohl.loopyplayer2.common.Settings
import com.michaelpohl.loopyplayer2.model.AppStateRepository
import com.michaelpohl.loopyplayer2.model.AppStateRepository.Companion.AudioFileType
import kotlinx.android.synthetic.main.dialog_settings.*
import kotlinx.android.synthetic.main.dialog_settings.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

@Deprecated("Obsolete")
class SettingsDialogFragment : DialogFragment(), KoinComponent {

    private val appState: AppStateRepository by inject()

    private lateinit var settings: Settings
    private lateinit var allowedFileTypes: MutableList<FileType>
    private var showLoopCount: Boolean = false

    lateinit var resultListener: (Settings) -> Unit
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        settings = appState.settings

        val view: View = inflater.inflate(R.layout.dialog_settings, container, false)

        view.rb_switch_immediately.isChecked = settings.isWaitMode.not()
        view.rb_wait_until_finished.isChecked = settings.isWaitMode


        view.cb_check_wav.isChecked = settings.acceptedFileTypes.contains(AudioFileType.WAVE)
        view.cb_check_mp3.isChecked = settings.acceptedFileTypes.contains(AudioFileType.MP3)
        view.cb_check_ogg.isChecked = settings.acceptedFileTypes.contains(AudioFileType.OGG)

        view.cb_check_show_loop_count.isChecked = settings.showLoopCount
        view.cb_check_keep_screen_on.isChecked = settings.keepScreenOn
        view.cb_check_play_in_background.isChecked = settings.playInBackground

        view.rb_switch_immediately.setOnClickListener { onSwitchImmediatelyClicked() }
        view.rb_wait_until_finished.setOnClickListener { onWaitUntilFinishedClicked() }

        view.cb_check_wav.setOnClickListener { onCheckBoxWavClicked() }
        view.cb_check_mp3.setOnClickListener { onCheckBoxMp3Clicked() }
        view.cb_check_ogg.setOnClickListener { onCheckBoxOggClicked() }

        view.cb_check_show_loop_count.setOnClickListener { onToggleShowLoopCountCLicked() }
        view.cb_check_keep_screen_on.setOnClickListener { onToggleKeepScreenOnClicked() }
        view.cb_check_play_in_background.setOnClickListener { onTogglePlayInBackgroundClicked() }

        view.btn_ok.setOnClickListener { onOkClicked() }
        view.btn_cancel.setOnClickListener { onCancelClicked() }

        return view
    }

    private fun onCheckBoxWavClicked() {
        val fileType = AudioFileType.WAVE
        if (cb_check_wav.isChecked) {
            allow(fileType)
        } else {
            forbid(fileType)
        }
    }

    private fun onCheckBoxMp3Clicked() {
        val fileType = AudioFileType.MP3
        if (cb_check_mp3.isChecked) {
            allow(fileType)
        } else {
            forbid(fileType)
        }
    }

    private fun onCheckBoxOggClicked() {
        val fileType = AudioFileType.OGG

        if (cb_check_ogg.isChecked) {
            allow(fileType)
        } else {
            forbid(fileType)
        }
    }

    private fun onSwitchImmediatelyClicked() {
//        settings.isWaitMode = false
//        view?.rb_switch_immediately?.isChecked = true
//        view?.rb_wait_until_finished?.isChecked = false
    }

    private fun onWaitUntilFinishedClicked() {
//        settings.isWaitMode = true
//        view?.rb_switch_immediately?.isChecked = false
//        view?.rb_wait_until_finished?.isChecked = true
    }

    private fun onToggleShowLoopCountCLicked() {
        showLoopCount = !showLoopCount

        view?.cb_check_show_loop_count?.isChecked = showLoopCount //does this need error handling?
    }

    private fun onToggleKeepScreenOnClicked() {
//        settings.keepScreenOn = !settings.keepScreenOn
    }

    private fun onTogglePlayInBackgroundClicked() {
//        settings.playInBackground = !settings.playInBackground
    }

    private fun onOkClicked() {

        appState.settings = this.settings
//        resultListener.invoke(settings)
        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }

    private fun allow(fileType: AudioFileType) {
        if (!settings.acceptedFileTypes.contains(fileType)) {
            settings.acceptedFileTypes.add(fileType)
        }
    }

    private fun forbid(fileType: AudioFileType) {
        if (settings.acceptedFileTypes.contains(fileType)) {
            settings.acceptedFileTypes.remove(fileType)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("invoking result listener")
    }
}
