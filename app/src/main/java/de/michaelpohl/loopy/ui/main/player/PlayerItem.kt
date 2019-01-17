package de.michaelpohl.loopy.ui.main.player

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import kotlinx.android.synthetic.main.item_loop.view.*
import rm.com.audiowave.AudioWaveView

class PlayerItem(
    val context: Context,
    var binding: ItemLoopBinding
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener,
    View.OnLongClickListener {

    lateinit var  viewModel: PlayerItemViewModel
    var selected = false

    init {
        binding.root.setOnClickListener(this)
//        binding.root.setOnLongClickListener(this)
    }

    override fun onClick(v: View?) {
    }

    override fun onLongClick(v: View?): Boolean {
//            no longClicklistener needed at this point
//            onItemSelectedListener?.invoke(filesList[adapterPosition])
        //TODO show file details on long click
        return true
    }

    fun bind(model: PlayerItemViewModel) {
        viewModel = model
        binding.model = viewModel
        inflateWave(itemView.wave, FileHelper.getSingleFile(model.fileModel.path).readBytes())
        binding.executePendingBindings()
    }

    private fun inflateWave(view: AudioWaveView, bytes: ByteArray) {
        view.setRawData(bytes)
        view.onStopTracking = {
           viewModel.onProgressChangedByUserTouch(it)
            viewModel.blockUpdatesFromPlayer.set(false)
        }

        view.onStartTracking = {
            viewModel.blockUpdatesFromPlayer.set(true)
        }

        view.onProgressChanged = {progress, byUser ->
        }
    }
}