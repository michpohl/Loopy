package de.michaelpohl.loopy.common.jni

import timber.log.Timber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object JniBridge {

    private var selectionJob: Continuation<JniResult<String>>? = null
    private var waitMode = false

    var currentlySelectedFile: String? = null

    var progressListener: ((Int) -> Unit)? = null
    var fileSelectedListener: ((String) -> Unit)? = null
    var filePreselectedListener: ((String) -> Unit)? = null


    init {
        System.loadLibrary("native-lib")
        Timber.d("Native Lib loaded!")
    }

    suspend fun setWaitMode(shouldWait: Boolean): JniResult<Boolean> {
        waitMode = shouldWait
        return successResult(waitMode)
    }

    suspend fun select(filename: String): JniResult<String> = suspendCoroutine { job ->
        selectionJob = job
        selectNative(filename, waitMode)
    }

    suspend fun start(filename: String): JniResult<String> {
        // TODO depending on the situation, a file needs to first be loaded
        return successResult(filename)
    }

    suspend fun pause(): JniResult<Nothing> {
        return successResult()
    }

    suspend fun stop(): JniResult<Nothing> {
        return successResult()
    }

    fun onSelected(filename: String) {
        currentlySelectedFile = filename
        selectionJob?.resume(successResult(filename)) ?: error("Continuation to resume does not exist!")
    }

    fun onStarted(filename: String) {
    }

    fun onPaused(filename: String) {
    }

    fun onStopped(filename: String) {
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

    private external fun selectNative(filename: String, isWaitMode: Boolean) // TODO factor out the wait mode
    private external fun startPlaybackNative()
    private external fun stopPlaybackNative()
    private external fun pausePlaybackNative(): Boolean
}