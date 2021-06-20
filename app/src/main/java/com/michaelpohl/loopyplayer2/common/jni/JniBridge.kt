package com.michaelpohl.loopyplayer2.common.jni

import com.michaelpohl.loopyplayer2.common.FileModel
import timber.log.Timber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("EmptyFunctionBlock")
object JniBridge {

    private var startJob: Continuation<JniResult<String>>? = null

    var waitMode = false // TODO later the waitmode should come from settings
        private set

    var filePreselectedListener: ((String) -> Unit)? = null
    lateinit var fileStartedByPlayerListener: ((String) -> Unit)
    lateinit var playbackProgressListener: (String, Int) -> Unit
    var conversionProgressListener: ((String, Int) -> Unit)? = null

    init {
        System.loadLibrary("native-lib")
        Timber.d("Native Lib loaded!")
    }

    suspend fun setWaitMode(shouldWait: Boolean): JniResult<Boolean> = suspendCoroutine { job ->
        if (setWaitModeNative(shouldWait)) {
            waitMode = shouldWait
            Timber.d("Resuming with waitMode: $waitMode")
            job.resume(successResult(shouldWait))
        } else {
            job.resume(errorResult()) // shouldn't ever happen, let's see
        }
    }

    suspend fun setSampleRate(sampleRate: Int): JniResult<Int> = suspendCoroutine { job ->
        if (setSampleRateNative(sampleRate)) {
            Timber.d("Resuming with sampleRate: $sampleRate")
            job.resume(successResult(sampleRate))
        } else {
            job.resume(errorResult()) // shouldn't ever happen, let's see
        }
    }

    suspend fun select(filename: String): JniResult<String> = suspendCoroutine { job ->
        with(selectNative(filename)) {
            job.resume(if (this) successResult(filename) else errorResult())
        }
    }

    suspend fun start(): JniResult<String> = suspendCoroutine {
        startJob = it
        startPlaybackNative()
    }

    suspend fun pause(): JniResult<Nothing> = suspendCoroutine { job ->
        with(pausePlaybackNative()) {
            job.resume(this.toJniResult())
        }
    }

    suspend fun resume(): JniResult<Nothing> = suspendCoroutine { job ->
        with(resumePlaybackNative()) {
            job.resume(this.toJniResult())
        }
    }

    suspend fun stop(): JniResult<Nothing> = suspendCoroutine { job ->
        with(stopPlaybackNative()) {
            job.resume(this.toJniResult())
        }
    }

    fun onSelected(filename: String) {
    }

    fun onStarted(filename: String) {
        startJob?.let {
            it.resume(JniResult.Success(filename))
            startJob = null
        } ?: run {
            fileStartedByPlayerListener(filename)
        }
    }

    fun onPaused(filename: String) {
    }

    fun onStopped(filename: String) {
    }

    fun onPlaybackProgressChanged(filename: String, percentage: Int) {
        playbackProgressListener(filename, percentage)
    }

    fun onConversionProgressChanged(filename: String, steps: Int) {
        conversionProgressListener?.invoke(filename, steps)
    }

    fun onFileSelected(value: String) {
        Timber.d("name: $value")
        fileStartedByPlayerListener.invoke(value)
    }

    fun onFilePreselected(value: String) {
        Timber.d("preselected: name: $value")
        fileStartedByPlayerListener.invoke(value)
        filePreselectedListener?.invoke(value)
    }

    fun convertFilesInFolder(folderName: String): Boolean {
        Timber.d("JniBridge -> converting")
        return convertFolder(folderName)
    }

    suspend fun convertAndAddToSet(
        fileNames: List<FileModel.AudioFile>,
        setPath: String
    ): ConversionResult = suspendCoroutine { job ->
        // TODO it would be nice to know which failed
        Timber.d("Starting")
        val results = mutableListOf<Boolean>()
        fileNames.forEach {
            results.add(convertSingleFile(it.name, it.path, setPath))

            /* test line to save to external storage for audio file examination */
//            results.add(
//                convertSingleFile(
//                    it.name,
//                    it.path,
//                    Environment.getExternalStorageDirectory().path
//                )
//            )
            if (results.size == fileNames.size) {
                val result = when {
                    results.contains(false) && results.contains(true) -> ConversionResult.SOME_SUCCESS
                    !results.contains(false) && results.contains(true) -> ConversionResult.All_SUCCESS
                    else -> ConversionResult.ALL_FAILED
                }
                Timber.d("resuming")
                job.resume(result)
            }
        }
//       job.resume(ConversionResult.ALL_FAILED)
    }

    private external fun setWaitModeNative(shouldWait: Boolean): Boolean
    private external fun selectNative(filename: String): Boolean // TODO factor out the wait mode
    private external fun startPlaybackNative()
    private external fun stopPlaybackNative(): Boolean
    private external fun pausePlaybackNative(): Boolean
    private external fun resumePlaybackNative(): Boolean
    private external fun convertFolder(folderName: String): Boolean
    private external fun convertSingleFile(
        fileName: String,
        filePath: String,
        setPath: String
    ): Boolean

    private external fun setSampleRateNative(sampleRate: Int): Boolean
    enum class ConversionResult {
        All_SUCCESS, SOME_SUCCESS, ALL_FAILED
    }
}

