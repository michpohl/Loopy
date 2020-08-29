package de.michaelpohl.loopy.ui.main.player

import android.view.View
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.player.adapter.PlayerDelegationAdapter
import timber.log.Timber
import kotlin.reflect.KFunction0

class PlayerItemViewModel(
    private val audioModel: AudioModel,
    private val onItemClickedListener: (AudioModel) -> Unit,
    private val onProgressChangedByUserTouchListener: (Float) -> Unit,
    private val onRemoveItemClickedListener: (AudioModel) -> Unit
) : BaseViewModel() {

    private val _progress = MutableLiveData(0F)
    val progress = _progress.immutable()

    private val _backgroundColor = MutableLiveData(resources.getColor(R.color.content_background))
    val backgroundColor = _backgroundColor.immutable()

    private val _loopsCount = MutableLiveData("0")
    val loopsCount = _loopsCount.immutable()

    private val _loopsCountVisibility = MutableLiveData(View.INVISIBLE)
    val loopsCountVisibility = _loopsCountVisibility.immutable()

    private val _removeButtonVisibility = MutableLiveData(View.VISIBLE)
    val removeButtonVisibility = _removeButtonVisibility.immutable()

    val displayName = getFilenameFromFullPath(audioModel.name)
    val fullPath = audioModel.name
    val canSeekAudio = false

    var selectionState = PlayerDelegationAdapter.Companion.SelectionState.NOT_SELECTED
        set(state) {
                Timber.d("State: $state")
            when (state) {
                PlayerDelegationAdapter.Companion.SelectionState.NOT_SELECTED -> {
                    _backgroundColor.postValue(resources.getColor(R.color.content_background))
                    _progress.postValue(0F)
                    _removeButtonVisibility.postValue(View.VISIBLE)
                }
                PlayerDelegationAdapter.Companion.SelectionState.PRESELECTED -> {
                    _removeButtonVisibility.postValue(View.GONE)
                    _backgroundColor.postValue(resources.getColor(R.color.item_selected_background))
                    _progress.postValue(0F)
                }
                PlayerDelegationAdapter.Companion.SelectionState.PLAYING -> {
                    _removeButtonVisibility.postValue(View.GONE)
                    _backgroundColor.postValue(getColor(resources, R.color.active, null))

                }
            }
            field = state
        }

    fun onItemClicked(view: View) {
        onItemClickedListener.invoke((audioModel))
    }

    fun onDeleteIconClicked(view: View) {
        onRemoveItemClickedListener(audioModel)
    }

    private fun updateLoopsCount(count: Int) {
        Timber.d("updating count with: $count")
        _loopsCount.postValue(count.toString())
    }

    fun updateProgress(newProgress: Int) {
        // safeguarding crazy values so we stay between 0 and 100
        if (newProgress < progress.value?: 0F) updateLoopsCount((loopsCount.value?.toInt() ?: 0) + 1)

        _progress.postValue(
            when (newProgress) {
                in Float.MIN_VALUE..0F -> 0F
                in 99F..Float.MAX_VALUE -> 100F
                else -> newProgress.toFloat()
            }
        )
    }

    private fun getFilenameFromFullPath(path: String) : String {
        val filename = path.subSequence(path.lastIndexOf("/") +1, path.lastIndex)
        return filename.substring(0, filename.lastIndexOf("."))
    }
}
