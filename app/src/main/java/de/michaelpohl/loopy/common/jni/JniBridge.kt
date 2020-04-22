package de.michaelpohl.loopy.common.jni

import android.content.res.AssetManager
import de.michaelpohl.loopy.JNIListener
import timber.log.Timber

object JniBridge {
    init {
        System.loadLibrary("native-lib")
        Timber.d("Native Lib loaded!")
    }

    var progressListener: ((Int) -> Unit)? = null
    lateinit var assets: AssetManager

    fun play(fileName: String) {
        playFromJNI(fileName)
    }

    fun stop() {
        stopJNIPlayback()
    }

    fun integerCallback(value: Int) {
        progressListener?.invoke(value)
    }

    /* end subscription test */
    private external fun playFromJNI(fileName: String)
    private external fun stopJNIPlayback()

}