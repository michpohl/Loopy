package de.michaelpohl.loopy.ui.main.player

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
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
    val loopsCount = ObservableField<String>("0")
    val loopsCountVisibility = ObservableInt(View.INVISIBLE)
    val name = audioModel.name
    var selectedState = NOT_SELECTED
    val canSeekAudio = ObservableBoolean(false)

    fun onItemClicked(view: View) {
        //TODO add ability to remove preselection again
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

    fun updateLoopsCount(count: Int) {
        if (!DataRepository.settings.showLoopCount) {
            loopsCountVisibility.set(View.INVISIBLE)
            return
        }

        if (selectedState != SELECTED) {
            loopsCountVisibility.set(View.INVISIBLE)
        } else {
        loopsCount.set(count.toString())
            if (selectedState == SELECTED) {
                loopsCountVisibility.set(View.VISIBLE)
            }
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