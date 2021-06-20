package com.michaelpohl.loopyplayer2.ui.player.adapter

import com.michaelpohl.delegationadapter.DelegationAdapter
import com.michaelpohl.loopyplayer2.common.AudioModel

// TODO refactor with custom Sorting
class PlayerDelegationAdapter(private val delegate: PlayerItemDelegate) :
    DelegationAdapter<AudioModel>(
        PlayerItemDiffCallback(), PlayerItemSorting(),
        listOf(delegate)
    ) {

    fun updateFileCurrentlyPlayed(name: String) {
        delegate.updateFileCurrentlyPlayed(name)
    }

    fun updateFilePreselected(name: String) {
        delegate.updateFilePreselected(name)
    }

    fun updatePlaybackProgress(payload: Pair<String, Int>, showLoopCount: Boolean) {
        delegate.updatePlaybackProgress(payload, showLoopCount)
    }

    companion object {
        enum class SelectionState { NOT_SELECTED, PRESELECTED, PLAYING }
    }
}
