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
//        playFromJNI(assets, fileName)
        playFromJNI(fileName)
    }

    private external fun playFromJNI(fileName: String)

}