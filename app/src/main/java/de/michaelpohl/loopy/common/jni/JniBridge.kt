package de.michaelpohl.loopy.common.jni

import android.content.res.AssetManager

object JniBridge {
    init {
        System.loadLibrary("native-lib")
    }
    lateinit var assets :  AssetManager

    fun getThatString(): String {
        return stringFromJNI()
    }

    fun play(fileName: String) {
        playFromJNI(assets, fileName)
    }

    private external fun stringFromJNI(): String
    private external fun playFromJNI(assetManager: AssetManager, fileName: String)
}