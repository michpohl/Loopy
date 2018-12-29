package de.michaelpohl.loopy.ui.main.player

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import de.michaelpohl.loopy.model.LoopsRepository
import kotlinx.android.synthetic.main.item_loop.view.*
import rm.com.audiowave.AudioWaveView
import timber.log.Timber

class PlayerItem(val context: Context,
                 var binding: ItemLoopBinding
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener,
    View.OnLongClickListener {

    var selected = false

    init {
        binding.root.setOnClickListener(this)
//        itemView.setOnLongClickListener(this)
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
        binding.model = model
        inflateWave(itemView.wave, FileHelper.getSingleFile(model.fileModel.path).readBytes())
        binding.executePendingBindings()
    }


    private fun inflateWave(view: AudioWaveView, bytes: ByteArray) {
        view.setRawData(bytes)
    }
}