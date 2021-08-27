package com.michaelpohl.loopyplayer2.ui.player

import android.view.View
import com.michaelpohl.loopyplayer2.common.Settings
import com.michaelpohl.loopyplayer2.common.toVisibility
import com.michaelpohl.loopyplayer2.ui.base.BaseUIState
import com.michaelpohl.shared.AudioModel

data class PlayerUIState(
    val loopsList: List<AudioModel>,
    val isPlaying: Boolean,
    val isWaitMode: Boolean = false,
    val fileInFocus: String? = null,
    val filePreselected: String? = null,
    val playbackProgress: Pair<String, Int>? = null,
    val clearButtonVisibility: Int = View.GONE,
    val settings: Settings,
    val processingOverlayVisibility: Int,
    val conversionProgress: Int? = 0
) : BaseUIState() {

    val emptyMessageVisibility: Int = this.loopsList.isEmpty().toVisibility()
}
