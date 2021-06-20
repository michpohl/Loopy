package com.michaelpohl.loopyplayer2.ui.player.adapter

import androidx.recyclerview.widget.DiffUtil
import com.michaelpohl.loopyplayer2.common.AudioModel

class PlayerItemDiffCallback : DiffUtil.ItemCallback<AudioModel>() {

    override fun areItemsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
        return oldItem.path == newItem.path
    }
    override fun areContentsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
        return oldItem.path == newItem.path
    }
}
