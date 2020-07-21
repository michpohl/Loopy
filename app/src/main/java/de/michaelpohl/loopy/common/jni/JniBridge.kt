package de.michaelpohl.loopy.common.jni

import timber.log.Timber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object JniBridge {

    private var startJob: Continuation<JniResult<String>>? = null

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

    suspend fun select(filename: String): JniResult<Nothing> = suspendCoroutine {
        it.resume(selectNative(filename, waitMode).toJniResult())
    }

    suspend fun start(): JniResult<String> = suspendCoroutine {
        startJob = it
        startPlaybackNative()
    }

    suspend fun pause(): JniResult<Nothing> {
        return successResult()
    }

    suspend fun stop(): JniResult<Nothing> {
        TODO("Crash my friend")
//        return successResult()
    }

    fun onSelected(filename: String) {
    }

    fun onStarted(filename: String) {
        Timber.d("Calls the callback: $filename")
        Timber.d("Is Startjob null: ${startJob == null}")
    }

    fun onPaused(filename: String) {
    }

    fun onStopped(filename: String) {
    }

    fun onPlaybackProgressChanged(filename: String, percentage: Int) {
        Timber.d("progress: $percentage, filename: $filename")
        startJob?.let {
            it.resume(successResult(filename)) ?: error("Continuation to resume is null!")
            startJob = null
        }
        progressListener?.invoke(percentage)
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

    private external fun selectNative(filename: String, isWaitMode: Boolean): Boolean // TODO factor out the wait mode
    private external fun startPlaybackNative()
    private external fun stopPlaybackNative()
    private external fun pausePlaybackNative(): Boolean
}

