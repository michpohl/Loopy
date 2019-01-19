package de.michaelpohl.loopy.ui.main.player

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState.*

class PlayerItemViewModel(
    private val position: Int,
    val audioModel: AudioModel,
    private val onItemClickedListener: (Int, SelectionState) -> Unit,
    private val onProgressChangedByUserTouchListener: (Float) -> Unit
) : ViewModel() {

    val blockUpdatesFromPlayer = ObservableBoolean(false)
    var backgroundColor : Int = 0
    val progress = ObservableField<Float>(0F)
    val name = audioModel.name
    var selectedState = NOT_SELECTED
    val canSeekAudio = ObservableBoolean(false)

    fun onItemClicked(view: View) {

        if (isWaitingMode()) {
            if (selectedState != PRESELECTED) selectedState = PRESELECTED
        } else {
            selectedState = SELECTED
            canSeekAudio.set(true)
        }
        onItemClickedListener.invoke(position,selectedState)

    }

    fun updateProgress(progress: Float) {
        if (selectedState == NOT_SELECTED) {
            this.progress.set(0F)
            return
        }
        if (!blockUpdatesFromPlayer.get()) {
            this.progress.set(progress)
        }
    }

    fun onProgressChangedByUserTouch(progress: Float) {
        onProgressChangedByUserTouchListener.invoke(progress)
    }

    private fun isWaitingMode(): Boolean {
        return DataRepository.settings.switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT
    }


    enum class SelectionState {
        NOT_SELECTED, PRESELECTED, SELECTED
    }
}