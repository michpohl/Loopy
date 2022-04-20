package com.michaelpohl.loopyplayer2.ui.player.adapter

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.michaelpohl.delegationadapter.DelegationAdapterItemHolder
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.*
import com.michaelpohl.loopyplayer2.ui.player.adapter.PlayerDelegationAdapter.Companion.SelectionState
import com.michaelpohl.shared.AudioModel
import rm.com.audiowave.AudioWaveView
import timber.log.Timber
import java.io.File

class PlayerItemHolder(
    itemView: View
) : DelegationAdapterItemHolder<AudioModel>(itemView) {

    var renderWaveform: Boolean = false
        set(value) {
            field = value
            if (value) inflateWave()
        }

    private var backgroundDrawable: Int = R.drawable.background_item_rounded_stroke
        set(value) {
            field = value
            this.itemView.background = getDrawable(value)
        }

    private var loopsCount: Int = 0
        set(value) {
            loopCounter.text = if (value != 0) {
                // TODO replace with placeholder string
                getString(R.string.loop_count_prefix) + " $value " + getString(R.string.loop_count_postfix)
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

        // although state should be set properly, we have to set it when binding, to avoid glitches
        // caused by how RecyclerView recycles. Works for now.
        // TODO implement a more elegant solution
        state = SelectionState.NOT_SELECTED
        itemView.setOnClickListener { clickListener(model) }

        /* TODO
        Currently the wave blocker blocks clicking the wave, so users cannot skip to a certain
        position in the audio. In the future, this should be possible.
         */
        waveBlocker.setOnClickListener { itemView.performClick() }
        deleteIcon.setOnClickListener { deleteListener(model) }

        // TODO remove this setting again once rendering is foolproof
//        if (renderWaveform)
        inflateWave()
    }

    fun getName(): String {
        return if (::model.isInitialized) model.name else "" // hack
    }

    fun showLoopCount(shouldShow: Boolean) {
        loopCounter.visibility = shouldShow.toVisibility()
    }

    fun updateProgress(percentage: Int) {
        if (percentage < progress) loopsCount += 1

        progress = when (percentage.toFloat()) {
            in Float.MIN_VALUE..0F -> 0F
            in 99F..Float.MAX_VALUE -> 100F
            else -> percentage.toFloat()
        }
    }

    var state: SelectionState =
        SelectionState.UNKNOWN
        set(value) {
            when (value) {
                SelectionState.NOT_SELECTED, SelectionState.UNKNOWN -> {
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
                    waveBlocker.show()
                }
            }
            field = value
        }

    private fun inflateWave() {
        val file = File(model.path)
        val length = file.length()
        val rawData = if (length > SIMPLE_RENDER_THRESHOLD) {
            Timber.i("Large file found. Using simplified waveform rendering.")
            val bytes = mutableListOf<Byte>()
            file.forEachBlock((length / 1000).toInt()) { buffer, _ ->
                buffer.forEachIndexed { index, byte ->
                    if (index.mod(50) == 0) {
                        bytes.add(byte)
                    }
                }
            }
            bytes.toByteArray()
        } else file.readBytes()
        wave.setRawData(rawData)
    }

    companion object {

        private const val SIMPLE_RENDER_THRESHOLD = 60000000
    }
}
