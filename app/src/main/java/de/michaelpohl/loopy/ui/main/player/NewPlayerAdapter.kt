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
import de.michaelpohl.loopy.common.jni.OldJniBridge
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState.NOT_SELECTED
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState.PRESELECTED
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState.SELECTED
import timber.log.Timber

class NewPlayerAdapter(
    private val onProgressChangedByUserListener: (Float) -> Unit,
    private val onLoopClickedListener: (AudioModel) -> Unit

) : Adapter<NewPlayerItemHolder>() {

    private val holders = mutableListOf<NewPlayerItemHolder>()
    var items = listOf<AudioModel>()
    lateinit var dialogHelper: DialogHelper

    private val _selected = MutableLiveData<String>()
    val selected = _selected.immutable()

    private var preselected: String? = null
        set(newValue) {
            newValue?.let { value ->
                field = value
                updatePreselectionState(value)
            }
        }

    private fun updateSelectionState(toBeSelected: String) {
        holders.forEach {
            when {
                toBeSelected == it.getName() -> it.state = SELECTED
                it.state == PRESELECTED -> {  /*do nothing */
                }
                else -> it.state = NOT_SELECTED
            }

        }
    }

    private fun updatePreselectionState(preselected: String) {
        holders.forEach {
            when {
                preselected == it.getName() && selected.value != it.getName() -> it.state = PRESELECTED
                it.state == SELECTED -> { /* do nothting */
                }
                else -> it.state = NOT_SELECTED
            }

        }
    }
    //        holders.find { currentlyPlaying.contains(it.getName()) }?.setState(PlayerItemViewModel.SelectionState.SELECTED)

    init {
        with(OldJniBridge) {
            fileSelectedListener = {
                _selected.postValue(it)
                updateSelectionState(it)
            }
            filePreselectedListener = { preselected = it }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewPlayerItemHolder {
        val binding: ItemLoopBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_loop, parent, false)
        val item = NewPlayerItemHolder(binding)
        holders.add(item)
        return item
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NewPlayerItemHolder, position: Int) {
        val itemViewModel = NewPlayerItemViewModel(
            items[position],
            this::onItemClicked,
            this::onProgressChangedByUserListener.invoke(),
            this::onRemoveItemClicked
        )
        holder.bind(itemViewModel)
    }

    override fun onViewAttachedToWindow(holder: NewPlayerItemHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAppear()
    }

    override fun onViewDetachedFromWindow(holder: NewPlayerItemHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDisappear()
    }

    private fun onItemClicked(model: AudioModel) {
        onLoopClickedListener.invoke(model)
    }

    private fun onRemoveItemClicked() {
        Timber.d("OnRemoveClicked")
    }
}