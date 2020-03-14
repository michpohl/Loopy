package de.michaelpohl.loopy.common.jni

import android.content.res.AssetManager
import timber.log.Timber

object JniBridge {
    init {
        System.loadLibrary("native-lib")
        Timber.d("Native Lib loaded!")
    }

    lateinit var assets: AssetManager

    fun play(fileName: String) {
        playFromJNI(fileName)
    }

    fun stop() {
        stopJNIPlayback()
    }

    fun testIt() {
        test()
    }

    private external fun playFromJNI(fileName: String)
    private external fun stopJNIPlayback()
    private external fun test()

    fun testCallBack(message: String) {
        Timber.d("This is the string: $message")
    }
}