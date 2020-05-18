package de.michaelpohl.loopy.ui.main.player

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState.NOT_SELECTED
import kotlinx.android.synthetic.main.item_loop.view.*
import rm.com.audiowave.AudioWaveView
import timber.log.Timber

class NewPlayerItemHolder(
    var binding: ItemLoopBinding
) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {

    private lateinit var viewModel: NewPlayerItemViewModel

    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    // TODO do I need a more complex life cycle?
    fun onAppear() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    fun onDisappear() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    fun bind(model: NewPlayerItemViewModel) {
        Timber.d("Bind is called")
        viewModel = model
        binding.model = viewModel
        binding.lifecycleOwner = this
        //        TODO inflate wave from audio model
        //prevent directories from trying to get rendered should they show up here.
        if (!FileHelper.getSingleFile(model.audioModel.path).isDirectory) {
            inflateWave(itemView.wave, FileHelper.getSingleFile(model.audioModel.path).readBytes())
        }
        binding.executePendingBindings()
    }

    fun getName(): String {
        return viewModel.name
    }

    var state: SelectionState = NOT_SELECTED
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