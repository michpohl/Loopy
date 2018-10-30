package de.michaelpohl.loopy.model

import android.content.Context
import android.media.MediaPlayer
import hugo.weaving.DebugLog
import timber.log.Timber
import java.io.File

/*
this thing comes from here: https://stackoverflow.com/questions/26274182/not-able-to-achieve-gapless-audio-looping-so-far-on-android
 */
@DebugLog
class LoopedPlayer private constructor(context: Context, resId: Int) {

    private var mContext: Context? = null
    private var mResId = 0
    private var mCounter = 1
    private var shouldBePlaying = false

    private var mCurrentPlayer: MediaPlayer
    private lateinit var mNextPlayer: MediaPlayer

    private val onCompletionListener = MediaPlayer.OnCompletionListener { mediaPlayer ->
        mediaPlayer.release()
        mCurrentPlayer = mNextPlayer

        createNextMediaPlayer()

        Timber.d(TAG, String.format("Loop #%d", ++mCounter))
    }

    init {
        mContext = context
        mResId = resId

        mCurrentPlayer = MediaPlayer.create(mContext, mResId)
        mCurrentPlayer.setOnPreparedListener {

            if (shouldBePlaying) mCurrentPlayer.start()
        }

        createNextMediaPlayer()
    }

    // repeats the necessary parts of init() so the player starts immediately again when start() is called
    private fun reInit() {
        mCurrentPlayer = MediaPlayer.create(mContext, mResId)
        mCurrentPlayer.setOnPreparedListener {

            if (shouldBePlaying) mCurrentPlayer.start()
        }

        createNextMediaPlayer()
    }

    private fun createNextMediaPlayer() {
        mNextPlayer = MediaPlayer.create(mContext, mResId)
        mCurrentPlayer.setNextMediaPlayer(mNextPlayer)
        mCurrentPlayer.setOnCompletionListener(onCompletionListener)
    }

    fun start() {
        shouldBePlaying = true
        mCurrentPlayer.start()
    }

    fun stop() {
        shouldBePlaying = false
        mCurrentPlayer.stop()
        reInit()
    }

    fun pause() {
        mCurrentPlayer.pause()
    }

    fun isPlaying(): Boolean {
        return mCurrentPlayer.isPlaying
    }

    fun setLoop(loop: File) {
        Timber.d("Did I get it? %s, %s", loop.absolutePath, loop.absoluteFile.name)
    }

    companion object {

        val TAG = LoopedPlayer::class.java.simpleName

        fun create(context: Context, resId: Int): LoopedPlayer {
            return LoopedPlayer(context, resId)
        }
    }
}