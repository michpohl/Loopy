package de.michaelpohl.loopy.ui.main.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView.Adapter
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.DialogHelper
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import de.michaelpohl.loopy.ui.main.player.adapter.PlayerDelegationAdapter
import de.michaelpohl.loopy.ui.main.player.adapter.PlayerItemHolder

import timber.log.Timber

//class PlayerAdapter(
//    private val onProgressChangedByUserListener: (Float) -> Unit,
//    private val onLoopClickedListener: (AudioModel) -> Unit
//
//) : Adapter<PlayerItemHolder>() {
//
//    private val holders = mutableListOf<PlayerItemHolder>()
//    var items = listOf<AudioModel>()
//    lateinit var dialogHelper: DialogHelper
//
//    private val _selected = MutableLiveData<String>()
//    val selected = _selected.immutable()
//
//    fun updateFileCurrentlyPlayed(filename: String) {
//        holders.forEach {
//            it.state = if (it.getName() == filename) PLAYING else PlayerDelegationAdapter.Companion.SelectionState.NOT_SELECTED
//        }
//    }
//
//    fun updateFilePreselected(filename: String) {
//        holders.filter { it.state != PLAYING }.forEach {
//            it.state = if (it.getName() == filename) PlayerDelegationAdapter.Companion.SelectionState.PRESELECTED else PlayerDelegationAdapter.Companion.SelectionState.NOT_SELECTED
//            Timber.d("Setting ${it.getName()} to ${it.state}")
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerItemHolder {
//        val binding: ItemLoopBinding =
//            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_loop, parent, false)
//        val item = PlayerItemHolder(binding)
//        holders.add(item)
//        return item
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    override fun onBindViewHolder(holder: PlayerItemHolder, position: Int) {
//        val itemViewModel = PlayerItemViewModel(
//            items[position],
//            this::onItemClicked,
//            this::onProgressChangedByUserListener.invoke(),
//            this::onRemoveItemClicked
//        )
//        holder.bind(itemViewModel)
//    }
//
//    override fun onViewAttachedToWindow(holder: PlayerItemHolder) {
//        super.onViewAttachedToWindow(holder)
//        holder.onAppear()
//    }
//
//    override fun onViewDetachedFromWindow(holder: PlayerItemHolder) {
//        super.onViewDetachedFromWindow(holder)
//        holder.onDisappear()
//    }
//
//    private fun onItemClicked(model: AudioModel) {
//        onLoopClickedListener.invoke(model)
//    }
//
//    private fun onRemoveItemClicked(audioModel: AudioModel) {
//        Timber.d("OnRemoveClicked")
//    }
//
//    fun updatePlaybackProgress(data: Pair<String, Int>?) {
//        data?.let { data ->
//            holders.find { it.getName() == data.first }?.updateProgress(data.second)
//        }
//    }
//
//
//}
