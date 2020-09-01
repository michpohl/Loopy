package de.michaelpohl.loopy.ui.main.player.adapter

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.deutschebahn.streckenagent2.ui.common.recycler.DelegationAdapterItemHolder
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.*
import de.michaelpohl.loopy.ui.main.player.adapter.PlayerDelegationAdapter.Companion.SelectionState
import rm.com.audiowave.AudioWaveView
import timber.log.Timber

class PlayerItemHolder(
    itemView: View
) : DelegationAdapterItemHolder<AudioModel>(itemView) {

    private var backgroundColor: Int = R.color.content_background
        set(value) {
            field = value
            this.itemView.background = getDrawable(value)
        }

    private var loopsCount: Int = 0
        set(value) {
            Timber.d("updating count with: $value")
            loopCounter.text = if (value != 0) {
                getString(R.string.loop_count_prefix) + " ${value} " + getString(R.string.loop_count_postfix)
            } else ""
            field = value
        }

    var progress: Float = 0F
        set(value) {
            field = value
            wave.progress = if (state == SelectionState.PLAYING) value else 0F
        }

    private val label: TextView = itemView.find(R.id.tv_label)
    private val loopCounter: TextView = itemView.find(R.id.tv_loop_count)
    private val wave: AudioWaveView = itemView.find(R.id.wave)
    private val deleteIcon: ImageButton = itemView.find(R.id.btn_remove)

    lateinit var model: AudioModel
    lateinit var clickReceiver: (AudioModel) -> Unit
    lateinit var deleteReceiver: (AudioModel) -> Unit

    override fun bind(item: AudioModel) {
        model = item
        label.text = model.displayName
        this.itemView.setOnClickListener { clickReceiver(model) }
        deleteIcon.setOnClickListener { deleteReceiver(model) }
    }

    fun getName(): String {
        return model.name
    }

    fun updateProgress(percentage: Int) {
        if (percentage < progress ?: 0F) loopsCount += 1

        progress = when (percentage.toFloat()) {
            in Float.MIN_VALUE..0F -> 0F
            in 99F..Float.MAX_VALUE -> 100F
            else -> percentage.toFloat()
        }
    }

    var state: SelectionState =
        SelectionState.NOT_SELECTED
        set(value) {
            Timber.d("State: $state")
            when (value) {
                SelectionState.NOT_SELECTED -> {
                    backgroundColor = R.color.content_background
                    progress = 0F
                    deleteIcon.show()
                }
                SelectionState.PRESELECTED -> {
                    backgroundColor = R.color.item_selected_background
                    progress = 0F
                }
                SelectionState.PLAYING -> {
                    deleteIcon.gone()
                    backgroundColor = R.color.active
                }
            }
            field = value
        }


    private fun inflateWave(view: AudioWaveView, bytes: ByteArray) {

        //TODO revisit if this is all cool
        //        view.setRawData(bytes)
        //        view.onStopTracking = {
        //            viewModel.onProgressChangedByUserTouch(it)
        //            viewModel.blockUpdatesFromPlayer.set(false)
        //        }
        //
        //        view.onStartTracking = {
        //            viewModel.blockUpdatesFromPlayer.set(true)
        //        }
        //
        //        view.onProgressChanged = { progress, byUser ->
        //        }
    }

}
