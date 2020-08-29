package de.michaelpohl.loopy.ui.main.player.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.adapter.adapter.AdapterItemDelegate
import com.example.adapter.adapter.ClickableAdapterItemDelegate
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import de.michaelpohl.loopy.ui.main.player.PlayerAdapter
import timber.log.Timber

class PlayerItemDelegate(
    private val clickReceiver: (AudioModel) -> Unit, // use the standard auto click receiver feature
    private val deleteReceiver: (AudioModel) -> Unit // additional custom receiver bound here
) : AdapterItemDelegate<AudioModel, PlayerItemHolder>() {

    private val holders = mutableListOf<PlayerItemHolder>()

    override fun createViewHolder(parent: ViewGroup): PlayerItemHolder {
        val binding: ItemLoopBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_loop, parent, false)
        return PlayerItemHolder(binding).also {
            it.clickReceiver = clickReceiver
            it.deleteReceiver = deleteReceiver
            holders.add(it)
        }
    }

    override fun isForItemType(item: Any): Boolean {
        return item is AudioModel
    }

    fun updateFileCurrentlyPlayed(name: String) {
        holders.forEach {
            it.state = if (it.getName() == name) PlayerAdapter.Companion.SelectionState.PLAYING else PlayerAdapter.Companion.SelectionState.NOT_SELECTED
        }
    }

    fun updateFilePreselected(name: String) {
        holders.filter { it.state != PlayerAdapter.Companion.SelectionState.PLAYING }.forEach {
            it.state = if (it.getName() == name) PlayerAdapter.Companion.SelectionState.PRESELECTED else PlayerAdapter.Companion.SelectionState.NOT_SELECTED
            Timber.d("Setting ${it.getName()} to ${it.state}")
        }
    }

    fun updatePlaybackProgress(payload: Pair<String, Int>) {
        payload?.let { payload ->
            holders.find { it.getName() == payload.first }?.updateProgress(payload.second)
        }
    }
}
