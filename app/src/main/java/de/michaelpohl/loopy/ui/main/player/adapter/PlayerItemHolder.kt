package de.michaelpohl.loopy.ui.main.player.adapter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.deutschebahn.streckenagent2.ui.common.recycler.DelegationAdapterItemHolder
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel
import rm.com.audiowave.AudioWaveView

class PlayerItemHolder(
    var binding: ItemLoopBinding
) : DelegationAdapterItemHolder<AudioModel>(binding.root), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private lateinit var viewModel: PlayerItemViewModel

    lateinit var clickReceiver: (AudioModel) -> Unit
    lateinit var deleteReceiver: (AudioModel) -> Unit

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    override fun bind(item: AudioModel) {
        viewModel = PlayerItemViewModel(
            item,
            clickReceiver,
            {}, // TODO onProgressChangedByUser
            deleteReceiver
        )
        binding.model = viewModel
        binding.lifecycleOwner = this

        //        TODO inflate wave from audio model
        //prevent directories from trying to get rendered should they show up here.
//        if (!StorageRepository.getSingleFile(model.audioModel.path).isDirectory) {
//            inflateWave(itemView.wave, StorageRepository.getSingleFile(model.audioModel.path).readBytes())
//        }
        binding.executePendingBindings()
    }

    fun onAppear() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    fun onDisappear() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    fun getName(): String {
        return viewModel.fullPath
    }

    fun updateProgress(percentage: Int) {
        viewModel.updateProgress(percentage)
    }

    var state: PlayerDelegationAdapter.Companion.SelectionState =
        PlayerDelegationAdapter.Companion.SelectionState.NOT_SELECTED
        set(value) {
            viewModel.selectionState = value
            field = value
        }

    private fun inflateWave(view: AudioWaveView, bytes: ByteArray) {

        //TODO revisit if this is all cool
        //        view.setRawData(bytes)
        //        view.onStopTracking = {
        //            viewModel.onProgressChangedByUserTouch(it)
        //            viewModel.blockUpdatesFromPlayer.set(false)
        //        }
        //
        //        view.onStartTracking = {
        //            viewModel.blockUpdatesFromPlayer.set(true)
        //        }
        //
        //        view.onProgressChanged = { progress, byUser ->
        //        }
    }

}
