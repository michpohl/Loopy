package de.michaelpohl.loopy.ui.main.player

import android.view.View
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState.*
import kotlin.reflect.KFunction0

class NewPlayerItemViewModel(
    val audioModel: AudioModel,
    private val onItemClickedListener: (AudioModel) -> Unit,
    private val onProgressChangedByUserTouchListener: (Float) -> Unit,
    private val onRemoveItemClickedListener: KFunction0<Unit>
) : BaseViewModel() {

    private val _progress = MutableLiveData(0F)
    val progress = _progress.immutable()

    private val _backgroundColor = MutableLiveData(0)
    val backgroundColor = _backgroundColor.immutable()

    private val _loopsCount = MutableLiveData("0")
    val loopsCount = _loopsCount.immutable()

    private val _loopsCountVisibility = MutableLiveData(View.INVISIBLE)
    val loopsCountVisibility = _loopsCountVisibility.immutable()

    private val _removeButtonVisibility = MutableLiveData(View.INVISIBLE)
    val removeButtonVisibility = _removeButtonVisibility.immutable()

    val name = audioModel.name
    val canSeekAudio = false

    var selectedState = NOT_SELECTED
        set(state) {
            //TODO change background colors here
            if (state == NOT_SELECTED) {
                _removeButtonVisibility.postValue(View.VISIBLE)
            } else {
                _removeButtonVisibility.postValue(View.GONE)
            }
            field = state
        }

    fun onItemClicked(view: View) {
        onItemClickedListener.invoke((audioModel))
    }

    fun onRemoveItemClicked(view: View) {
    }

    fun updateLoopsCount(count: Int) {
        _loopsCount.postValue(count.toString())
    }

    fun updateProgress(newProgress: Float) {

        // if we update the progress on a non-selected item, we want 0 instead
        if (selectedState == NOT_SELECTED && _progress.value != 0F) {
           _progress.postValue(0F)
            return
        }

        // safeguarding crazy values so we stay between 0 and 100
        _progress.postValue(
            when (newProgress) {
                in Float.MIN_VALUE..0F -> 0F
                in 100F..Float.MAX_VALUE -> 100F
                else -> newProgress
            }
        )
    }
}