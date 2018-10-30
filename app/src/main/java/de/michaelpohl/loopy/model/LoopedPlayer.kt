package de.michaelpohl.loopy.model

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import hugo.weaving.DebugLog
import timber.log.Timber
import java.io.File

/*
this thing comes from here: https://stackoverflow.com/questions/26274182/not-able-to-achieve-gapless-audio-looping-so-far-on-android
 */
@DebugLog
class LoopedPlayer private constructor(context: Context, resId: Int) {

    var hasLoopFile = false

    private var mContext: Context? = null
    private var mResId = 0
    private var mCounter = 1
    private var shouldBePlaying = false

    private lateinit var mCurrentPlayer: MediaPlayer
    private lateinit var mNextPlayer: MediaPlayer
    private lateinit var loopUri: Uri

    private val onCompletionListener = MediaPlayer.OnCompletionListener { mediaPlayer ->
        mediaPlayer.release()
        mCurrentPlayer = mNextPlayer

        createNextMediaPlayer()

        Timber.d(TAG, String.format("Loop #%d", ++mCounter))
    }

    init {
        mContext = context
        mResId = resId

//        mCurrentPlayer = MediaPlayer.create(mContext, mResId)
//        mCurrentPlayer.setOnPreparedListener {
//
//            if (shouldBePlaying) mCurrentPlayer.start()
//        }
//
//        createNextMediaPlayer()
    }

    // repeats the necessary parts of init() so the player starts immediately again when start() is called
    private fun reInit() {
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
        //TODO show user that no file is selected yet
        shouldBePlaying = true
        mCurrentPlayer.start()
    }

    fun stop() {
        shouldBePlaying = false
        mCurrentPlayer.stop()
        mNextPlayer.stop()
        reInit()
    }

    fun pause() {
        mCurrentPlayer.pause()
    }

    fun isPlaying(): Boolean {
        return mCurrentPlayer.isPlaying
    }

    fun setLoop(context: Context, loop: File) {
        if (hasLoopFile) stop()
        loopUri = FileProvider.getUriForFile(context,"com.de.michaelpohl.loopy", loop)
        Timber.d("This is my path: %s", loopUri.toString())
        reInit()
        hasLoopFile = true
    }

    companion object {

        val TAG = LoopedPlayer::class.java.simpleName

        fun create(context: Context, resId: Int): LoopedPlayer {
            return LoopedPlayer(context, resId)
        }
    }
}