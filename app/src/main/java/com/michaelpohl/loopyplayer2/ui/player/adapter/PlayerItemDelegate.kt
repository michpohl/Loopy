package com.michaelpohl.loopyplayer2.ui.player.adapter

import android.view.ViewGroup
import com.michaelpohl.delegationadapter.AdapterItemDelegate
import com.michaelpohl.delegationadapter.inflateLayout
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.AudioModel
import com.michaelpohl.loopyplayer2.ui.player.adapter.PlayerDelegationAdapter.Companion.SelectionState

class PlayerItemDelegate(
    private val clickReceiver: (AudioModel) -> Unit, // use the standard auto click receiver feature
    private val deleteReceiver: (AudioModel) -> Unit // additional custom receiver bound here
) : AdapterItemDelegate<AudioModel, PlayerItemHolder>() {

    private val holders = mutableListOf<PlayerItemHolder>()
    override fun createViewHolder(parent: ViewGroup): PlayerItemHolder {
        return PlayerItemHolder(inflateLayout(R.layout.item_loop, parent)).also {
            it.clickListener = clickReceiver
            it.deleteListener = deleteReceiver
            holders.add(it)
        }
    }

    override fun isForItemType(item: Any): Boolean {
        return item is AudioModel
    }

    fun updateFileCurrentlyPlayed(name: String) {
        holders.forEach {
            it.state =
                if (it.getName() == name) SelectionState.PLAYING else SelectionState.NOT_SELECTED
        }
    }

    fun updateFilePreselected(name: String) {
        holders.filter { it.state != SelectionState.PLAYING }.forEach {
            it.state =
                if (it.getName() == name) SelectionState.PRESELECTED else SelectionState.NOT_SELECTED
        }
    }

    fun updatePlaybackProgress(payload: Pair<String, Int>, showLoopCount: Boolean) {
        val targetHolder = holders.find { it.getName() == payload.first }
        targetHolder?.showLoopCount(showLoopCount)

        payload.let { payload ->
            targetHolder?.updateProgress(payload.second)
        }
    }
}
