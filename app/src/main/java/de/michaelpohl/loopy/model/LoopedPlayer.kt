package de.michaelpohl.loopy.model

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.content.FileProvider
import hugo.weaving.DebugLog
import timber.log.Timber
import java.io.File

/*
this thing comes from here: https://stackoverflow.com/questions/26274182/not-able-to-achieve-gapless-audio-looping-so-far-on-android
 */
@DebugLog
class LoopedPlayer private constructor(context: Context) {

    var hasLoopFile = false
    var isPaused = false

    private var mContext: Context? = null
    private var mCounter = 1
    private var shouldBePlaying = false

    private lateinit var mCurrentPlayer: MediaPlayer
    private lateinit var mNextPlayer: MediaPlayer
    private lateinit var loopUri: Uri

    companion object {

        val TAG = LoopedPlayer::class.java.simpleName

        fun create(context: Context): LoopedPlayer {
            return LoopedPlayer(context)
        }
    }

    private val onCompletionListener = MediaPlayer.OnCompletionListener { mediaPlayer ->
        mediaPlayer.release()
        mCurrentPlayer = mNextPlayer

        createNextMediaPlayer()

        Timber.d(TAG, String.format("Loop #%d", ++mCounter))
    }

    init {
        mContext = context
    }

    private fun initPlayer() {
        mCurrentPlayer = MediaPlayer.create(mContext, loopUri)
        mCurrentPlayer.setOnPreparedListener {

            if (shouldBePlaying) mCurrentPlayer.start()
        }

        createNextMediaPlayer()
    }

    private fun createNextMediaPlayer() {
        mNextPlayer = MediaPlayer.create(mContext, loopUri)
        mCurrentPlayer.setNextMediaPlayer(mNextPlayer)
        mCurrentPlayer.setOnCompletionListener(onCompletionListener)
    }

    fun start() {
        //TODO show user if no file is selected yet
        shouldBePlaying = true
        isPaused = false
        mCurrentPlayer.start()
    }

    fun stop() {
        shouldBePlaying = false
        mCurrentPlayer.stop()
        mNextPlayer.stop()
        initPlayer()
    }

    fun pause() {
        mCurrentPlayer.pause()
        isPaused = true
    }

    fun isPlaying(): Boolean {
        return mCurrentPlayer.isPlaying
    }

    fun setLoop(context: Context, loop: File) {
        if (hasLoopFile) stop()
        loopUri = FileProvider.getUriForFile(context, "com.de.michaelpohl.loopy", loop)
        Timber.d("This is my path: %s", loopUri.toString())
        initPlayer()
        hasLoopFile = true
    }
}