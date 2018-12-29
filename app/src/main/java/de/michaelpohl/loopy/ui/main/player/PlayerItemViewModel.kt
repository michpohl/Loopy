package de.michaelpohl.loopy.ui.main.player

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.view.View
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import de.michaelpohl.loopy.model.LoopsRepository
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState.*
import timber.log.Timber

class PlayerItemViewModel(
    val position: Int,
    val fileModel: FileModel,
    private val onItemClickedListener: (Int, SelectionState) -> Unit
) : ViewModel() {

    var onProgressUpdatedListener: ((Float) -> Unit)? = null
    var backgroundColor : Int = 0
    val progress = ObservableField<Float>(0F)
    val name = fileModel.name
    var state = NOT_SELECTED

    fun onItemClicked(view: View) {

        if (isWaitingMode()) {
            if (state != PRESELECTED) state = PRESELECTED else state = NOT_SELECTED

            Timber.d("Mode: WAIT, position: %s, state %s", position, state)
        } else {
            state = SELECTED
            Timber.d("Mode: SWITCH, position: %s, state: %s", position, state)
            onItemClickedListener.invoke(position,state)
        }
    }

    fun update() {
    }

    fun updateProgress(progress: Float) {

        if (state == NOT_SELECTED) {
            Timber.d("not selected, position: %s", position)
            this.progress.set(0F)
            return
        }
        this.progress.set(progress)
    }

//    fun initializeOnProgressUpdatedListener() {
//        onProgressUpdatedListener = { it -> updateProgress(it) }
//    }

    private fun isWaitingMode(): Boolean {
        return LoopsRepository.settings.switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT
    }

    enum class SelectionState {
        NOT_SELECTED, PRESELECTED, SELECTED
    }
}