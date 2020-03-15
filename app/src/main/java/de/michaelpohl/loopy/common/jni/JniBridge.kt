package de.michaelpohl.loopy.common.jni

import android.content.res.AssetManager
import de.michaelpohl.loopy.JNIListener
import timber.log.Timber

object JniBridge : JNIListener {
    init {
        System.loadLibrary("native-lib")
        Timber.d("Native Lib loaded!")
        nsubscribeListener(this)
    }

    lateinit var assets: AssetManager

    fun play(fileName: String) {
        playFromJNI(fileName)
    }

    fun stop() {
        stopJNIPlayback()
    }

    fun testIt() {
//        test()
    }

    /* subscription test */

        val nlistener = object : JNIListener {
            override fun onAcceptMessage(string: String) {
               Timber.d("The object says: $string")
            }

            override fun onAcceptMessageVal(messVal: Int) {}
        }
    fun x() {
        Timber.d("Testing the callback")
        testCallBack("Blablabla")
    }
    override fun onAcceptMessage(string: String?) {
        Timber.d("Received this: $string")
    }

    override fun onAcceptMessageVal(messVal: Int) {
        Timber.d("Received this value: $messVal")
    }

    private external fun nsubscribeListener(JNIListener: JNIListener)
    private external fun nonNextListener(message: String)

    fun testCallBack(message: String) {
        Timber.d("This is the string: $message")
        nonNextListener(message)
    }

    fun integerCallback(value: Int) {
        Timber.d("This is my int: $value")
    }

    /* end subscription test */
    private external fun playFromJNI(fileName: String)
    private external fun stopJNIPlayback()

    private external fun test()
}