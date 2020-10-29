package de.michaelpohl.loopy.ui.main.player.adapter

import android.view.ViewGroup
import com.example.adapter.adapter.AdapterItemDelegate
import com.example.adapter.adapter.inflateLayout
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.ui.main.player.adapter.PlayerDelegationAdapter.Companion.SelectionState
import timber.log.Timber

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
            Timber.d("Setting ${it.getName()} to ${it.state}")
        }
    }

    fun updatePlaybackProgress(payload: Pair<String, Int>, showLoopCount: Boolean) {
        payload?.let { payload ->
            holders.find { it.getName() == payload.first }?.updateProgress(payload.second, showLoopCount)
        }
    }
}
