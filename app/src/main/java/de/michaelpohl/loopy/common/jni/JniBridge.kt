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
        playFromJNI(assets, fileName)
    }

    private external fun stringFromJNI(): String
    private external fun playFromJNI(assetManager: AssetManager, fileName: String)
    private external fun playFromJNI2(fileName: String)

}