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
    private val position: Int,
    val fileModel: FileModel,
    private val onItemClickedListener: (Int, SelectionState) -> Unit
) : ViewModel() {

    var backgroundColor : Int = 0
    val progress = ObservableField<Float>(0F)
    val name = fileModel.name
    var selectedState = NOT_SELECTED

    fun onItemClicked(view: View) {

        if (isWaitingMode()) {
            if (selectedState != PRESELECTED) selectedState = PRESELECTED
        } else {
            selectedState = SELECTED
        }
        onItemClickedListener.invoke(position,selectedState)

    }

    fun updateProgress(progress: Float) {
        if (selectedState == NOT_SELECTED) {
            this.progress.set(0F)
            return
        }
        this.progress.set(progress)
    }

    private fun isWaitingMode(): Boolean {
        return LoopsRepository.settings.switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT
    }

    enum class SelectionState {
        NOT_SELECTED, PRESELECTED, SELECTED
    }
}