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
import timber.log.Timber

class PlayerItemViewModel(
    private val position: Int,
    val audioModel: AudioModel,
    private val onItemClickedListener: (Int, SelectionState) -> Unit,
    private val onProgressChangedByUserTouchListener: (Float) -> Unit,
    private val onRemoveItemClickedListener: (Int) -> Unit
) : ViewModel() {

    val blockUpdatesFromPlayer = ObservableBoolean(false)
    var backgroundColor: Int = 0
    val progress = ObservableField<Float>(0F)
    val loopsCount = ObservableField<String>("0")
    val loopsCountVisibility = ObservableInt(View.VISIBLE)
    val name = audioModel.name

    var selectedState = NOT_SELECTED
        set (state) {
            if (state == NOT_SELECTED) {
                removeButtonVisibility.set(View.VISIBLE)
            } else {
                removeButtonVisibility.set(View.INVISIBLE)
            }
            field = state
        }

    val canSeekAudio = ObservableBoolean(false)
    val removeButtonVisibility = ObservableInt(View.VISIBLE)

    fun onItemClicked(view: View) {
        //TODO add ability to remove preselection again
        if (isWaitingMode()) {
            if (selectedState != PRESELECTED) selectedState = PRESELECTED
        } else {
            selectedState = SELECTED
            canSeekAudio.set(true)
        }
        onItemClickedListener.invoke(position, selectedState)
    }

    fun onRemoveItemClicked(view: View) {
        onRemoveItemClickedListener.invoke(position)
    }

    fun updateProgress(newProgress: Float) {
        var safeProgress = newProgress
//        since we only want values in between 0 and 100, we're safeguarding the value here
        Timber.d("gress: $newProgress")
        when (newProgress) {
            in Float.MIN_VALUE..0F -> safeProgress = 0F
            in 100F..Float.MAX_VALUE -> safeProgress = 100F
        }

        if (selectedState == NOT_SELECTED) {
            this.progress.set(0F)
            return
        }
        if (!blockUpdatesFromPlayer.get()) {
            this.progress.set(safeProgress)
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
