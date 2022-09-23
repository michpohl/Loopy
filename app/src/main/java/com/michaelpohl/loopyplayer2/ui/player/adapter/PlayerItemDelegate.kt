package com.michaelpohl.loopyplayer2.ui.player.adapter

import android.view.ViewGroup
import com.michaelpohl.delegationadapter.AdapterItemDelegate
import com.michaelpohl.delegationadapter.inflateLayout
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.ui.player.adapter.PlayerDelegationAdapter.Companion.SelectionState
import com.michaelpohl.shared.AudioModel
import timber.log.Timber

class PlayerItemDelegate(
    private val clickReceiver: (AudioModel) -> Unit, // use the standard auto click receiver feature
    private val deleteReceiver: (AudioModel) -> Unit, // additional custom receiver bound here
) : AdapterItemDelegate<AudioModel, PlayerItemHolder>() {

    private val holders = mutableListOf<PlayerItemHolder>()

    private var renderWaveform = false
        set(value) {
            if (renderWaveform != value) {
                holders.forEach { it.renderWaveform = value }
                field = value
            }
        }

    override fun createViewHolder(parent: ViewGroup): PlayerItemHolder {
        return PlayerItemHolder(inflateLayout(R.layout.item_loop, parent)).also {
            it.clickListener = clickReceiver
            it.deleteListener = deleteReceiver
            holders.add(it)
        }
    }

    override fun bindViewHolder(item: AudioModel, holder: PlayerItemHolder) {
        super.bindViewHolder(item, holder)
        holder.renderWaveform = renderWaveform
    }

    override fun isForItemType(item: Any): Boolean {
        return item is AudioModel
    }

    fun updateFileCurrentlyPlayed(name: String) {
        holders.filter { it.state != SelectionState.UNKNOWN }.forEach {
            it.state =
                if (it.getName() == name) SelectionState.PLAYING else SelectionState.NOT_SELECTED
        }
    }

    fun updateFilePreselected(name: String) {
        holders.filter { it.state != SelectionState.PLAYING && it.state != SelectionState.UNKNOWN }.forEach {
            it.state =
                if (it.getName() == name) SelectionState.PRESELECTED else SelectionState.NOT_SELECTED
        }
    }

    fun updatePlaybackProgress(payload: Pair<String, Int>, showLoopCount: Boolean) {
        holders.find { it.getName() == payload.first }?.let { itemHolder ->
            itemHolder.showLoopCount(showLoopCount)
            itemHolder.updateProgress(payload.second)
        }
    }

    fun updateRenderWaveform(shouldRender: Boolean) {
        renderWaveform = shouldRender
    }
}
