package de.michaelpohl.loopy.ui.main.player.adapter

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.example.adapter.adapter.DelegationAdapterItemHolder
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.*
import de.michaelpohl.loopy.ui.main.player.adapter.PlayerDelegationAdapter.Companion.SelectionState
import rm.com.audiowave.AudioWaveView
import java.io.File

class PlayerItemHolder(
    itemView: View
) : DelegationAdapterItemHolder<AudioModel>(itemView) {

    private var backgroundDrawable: Int = R.drawable.background_item_rounded_stroke
        set(value) {
            field = value
            this.itemView.background = getDrawable(value)
        }

    private var loopsCount: Int = 0
        set(value) {
            loopCounter.text = if (value != 0) {
                getString(R.string.loop_count_prefix) + " $value " + getString(R.string.loop_count_postfix) // TODO this can be one string
            } else ""
            field = value
        }

    private var progress: Float = 0F
        set(value) {
            field = value
            wave.progress = if (state == SelectionState.PLAYING) value else 0F
        }

    private val label: TextView = itemView.find(R.id.tv_label)
    private val loopCounter: TextView = itemView.find(R.id.tv_loop_count)
    private val wave: AudioWaveView = itemView.find(R.id.wave)
    private val deleteIcon: ImageButton = itemView.find(R.id.btn_remove)
    private val waveBlocker: View = itemView.find(R.id.wave_blocker)

    lateinit var model: AudioModel
    lateinit var clickListener: (AudioModel) -> Unit
    lateinit var deleteListener: (AudioModel) -> Unit

    override fun bind(item: AudioModel) {
        model = item
        label.text = model.displayName
        itemView.setOnClickListener { clickListener(model) }
        waveBlocker.setOnClickListener {clickListener(model)}
        deleteIcon.setOnClickListener { deleteListener(model) }
        inflateWave()
    }

    fun getName(): String {
        return model.name
    }

    fun updateProgress(percentage: Int, showLoopCount: Boolean) {
        if (showLoopCount && percentage < progress) loopsCount += 1

        progress = when (percentage.toFloat()) {
            in Float.MIN_VALUE..0F -> 0F
            in 99F..Float.MAX_VALUE -> 100F
            else -> percentage.toFloat()
        }
    }

    var state: SelectionState =
        SelectionState.NOT_SELECTED
        set(value) {
            when (value) {
                SelectionState.NOT_SELECTED -> {
                    backgroundDrawable = R.drawable.background_item_rounded_stroke
                    deleteIcon.show()
                    loopsCount = 0
                    progress = 0F
                    waveBlocker.show()
                }
                SelectionState.PRESELECTED -> {
                    backgroundDrawable = R.drawable.background_item_rounded_filled_green
                    progress = 0F
                    deleteIcon.gone()
                    waveBlocker.show()
                }
                SelectionState.PLAYING -> {
                    deleteIcon.gone()
                    backgroundDrawable = R.drawable.background_item_rounded_filled
                    waveBlocker.gone()
                }
            }
            field = value
        }


    private fun inflateWave() {
        val file = File(model.path)
        val bytes = file.readBytes()
        wave.setRawData(bytes)
//        view.onStopTracking = {
////                    viewModel.onProgressChangedByUserTouch(it)
////                    viewModel.blockUpdatesFromPlayer.set(false)
//        }
//
//        view.onStartTracking = {
////                    viewModel.blockUpdatesFromPlayer.set(true)
//        }
//
//        view.onProgressChanged = { progress, byUser ->
//        }
    }

}
