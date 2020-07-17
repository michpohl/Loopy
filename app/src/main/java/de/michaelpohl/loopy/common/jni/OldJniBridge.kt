package de.michaelpohl.loopy.common.jni

import android.content.res.AssetManager
import timber.log.Timber

object OldJniBridge {
    init {
        System.loadLibrary("native-lib")
        Timber.d("Native Lib loaded!")
    }

    var progressListener: ((Int) -> Unit)? = null
    var fileSelectedListener: ((String) -> Unit)? = null
    var filePreselectedListener: ((String) -> Unit)? = null

    fun load(fileName: String, isWaitMode: Boolean) {
        selectNative(fileName, isWaitMode)
    }

    fun play() {
        startPlaybackNative()
    }

    fun pause(): Boolean {
        return pausePlaybackNative()
    }

    fun stop() {
        stopPlaybackNative()
    }

    fun onPlaybackProgressChanged(value: Int) {
        progressListener?.invoke(value)
    }

    fun onFileSelected(value: String) {
        Timber.d("name: $value")
        fileSelectedListener?.invoke(value)
    }

    fun onFilePreselected(value: String) {
        Timber.d("preselected: name: $value")
        fileSelectedListener?.invoke(value)
        filePreselectedListener?.invoke(value)

    }

    /* end subscription test */
    private external fun selectNative(fileName: String, isWaitMode: Boolean)
    private external fun startPlaybackNative()
    private external fun stopPlaybackNative()
    private external fun pausePlaybackNative(): Boolean
}