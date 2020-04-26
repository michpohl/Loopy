package de.michaelpohl.loopy.common.jni

import android.content.res.AssetManager
import timber.log.Timber

object JniBridge {
    init {
        System.loadLibrary("native-lib")
        Timber.d("Native Lib loaded!")
    }

    var progressListener: ((Int) -> Unit)? = null
    lateinit var assets: AssetManager

    fun load(fileName: String) {
        loadNative(fileName)
    }

    fun play() {
        startPlaybackNative()
    }

    fun stop() {
        stopPlaybackNative()
    }

    fun integerCallback(value: Int) {
        progressListener?.invoke(value)
    }

    /* end subscription test */
    private external fun loadNative(fileName: String)
    private external fun startPlaybackNative()
    private external fun stopPlaybackNative()
}