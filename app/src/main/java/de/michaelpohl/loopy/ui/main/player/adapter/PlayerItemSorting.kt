package de.michaelpohl.loopy.ui.main.player.adapter

import com.example.adapter.adapter.Sorting
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.FileModel

class PlayerItemSorting : Sorting.Basic<AudioModel>() {
    override fun sort(input: List<AudioModel>): List<AudioModel> {
        return input.distinct()
    }

}
