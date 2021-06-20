package com.michaelpohl.loopyplayer2.ui.player.adapter

import com.michaelpohl.delegationadapter.Sorting
import com.michaelpohl.loopyplayer2.common.AudioModel

class PlayerItemSorting : Sorting.Basic<AudioModel>() {

    override fun sort(input: List<AudioModel>): List<AudioModel> {
        return input.distinct().sortedBy { it.displayName }
    }
}
