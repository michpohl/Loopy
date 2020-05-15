package de.michaelpohl.loopy.ui.main.player

import androidx.recyclerview.widget.RecyclerView
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import kotlinx.android.synthetic.main.item_loop.view.*
import rm.com.audiowave.AudioWaveView
import timber.log.Timber

class NewPlayerItemHolder(
    var binding: ItemLoopBinding
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var viewModel: NewPlayerItemViewModel


    fun bind(model: NewPlayerItemViewModel) {
        Timber.d("Bind is called")
        viewModel = model
        binding.model = viewModel
        //        TODO inflate wave from audio model
        //prevent directorys from trying to get rendered should they show up here.
        if (!FileHelper.getSingleFile(model.audioModel.path).isDirectory) {
            inflateWave(itemView.wave, FileHelper.getSingleFile(model.audioModel.path).readBytes())
        }
        binding.executePendingBindings()
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