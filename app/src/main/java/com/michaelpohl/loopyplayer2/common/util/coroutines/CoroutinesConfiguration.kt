package com.michaelpohl.loopyplayer2.common.util.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CoroutinesConfiguration {

    companion object {
        var uiDispatcher: CoroutineDispatcher = Dispatchers.Main
        var backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default
        var ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    }
}
