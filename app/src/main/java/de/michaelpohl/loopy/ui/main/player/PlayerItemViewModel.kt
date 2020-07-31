package de.michaelpohl.loopy.ui.main.player

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState.*
import timber.log.Timber

class PlayerItemViewModel(
    private val position: Int,
    val audioModel: AudioModel,
    private val onItemClickedListener: (Int, SelectionState) -> Unit,
    private val onProgressChangedByUserTouchListener: (Float) -> Unit,
    private val onRemoveItemClickedListener: (Int) -> Unit
) : BaseViewModel() {

    val blockUpdatesFromPlayer = ObservableBoolean(false)
    var backgroundColor: Int = 0
    val progress = ObservableField<Float>(0F)
    val loopsCount = ObservableField<String>("0")
    val loopsCountVisibility = ObservableInt(View.VISIBLE)
    val name = audioModel.name

    var selectedState = NOT_SELECTED
        set (state) {
            //TODO change background colors here
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
            Timber.d("Are we in waiting mode? ${isWaitingMode()}")
        if (isWaitingMode()) {
            if (selectedState != PRESELECTED) selectedState = PRESELECTED
        } else {
            selectedState = PLAYING
            canSeekAudio.set(true)
        }
        Timber.d("Selected state is now: $selectedState")
        onItemClickedListener.invoke(position, selectedState)
    }

    fun onRemoveItemClicked(view: View) {
        onRemoveItemClickedListener.invoke(position)
    }

    fun updateProgress(newProgress: Float) {
        var safeProgress = newProgress
//        since we only want values in between 0 and 100, we're safeguarding the value here
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

        if (selectedState != PLAYING) {
            loopsCountVisibility.set(View.INVISIBLE)
        } else {
            loopsCount.set(count.toString())
            if (selectedState == PLAYING) {
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
        NOT_SELECTED, PRESELECTED, PLAYING
    }
}
