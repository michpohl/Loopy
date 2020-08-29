package de.michaelpohl.loopy.ui.main.player.adapter

import com.deutschebahn.streckenagent2.ui.common.recycler.AnyDiffCallback
import com.deutschebahn.streckenagent2.ui.common.recycler.DelegationAdapter
import de.michaelpohl.loopy.common.AudioModel

class PlayerDelegationAdapter(private val delegate: PlayerItemDelegate) :
    DelegationAdapter<AudioModel>(AnyDiffCallback(), delegate) {
    fun updateFileCurrentlyPlayed(name: String) {
        delegate.updateFileCurrentlyPlayed(name)
    }

    fun updateFilePreselected(name: String) {
        delegate.updateFilePreselected(name)
    }

    fun updatePlaybackProgress(payload: Pair<String, Int>) {
        delegate.updatePlaybackProgress(payload)
    }

    companion object {
        enum class SelectionState { NOT_SELECTED, PRESELECTED, PLAYING }
    }
}
