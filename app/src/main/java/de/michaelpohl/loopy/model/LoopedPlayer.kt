package de.michaelpohl.loopy.model

import android.content.Context
import android.media.MediaPlayer
import timber.log.Timber


/*
this thing comes from here: https://stackoverflow.com/questions/26274182/not-able-to-achieve-gapless-audio-looping-so-far-on-android
 */

class LoopedPlayer private constructor(context: Context, resId: Int) {

    private var mContext: Context? = null
    private var mResId = 0
    private var mCounter = 1

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
        mCurrentPlayer!!.setOnPreparedListener { mCurrentPlayer!!.start() }

        createNextMediaPlayer()
    }

    private fun createNextMediaPlayer() {
        mNextPlayer = MediaPlayer.create(mContext, mResId)
        mCurrentPlayer!!.setNextMediaPlayer(mNextPlayer)
        mCurrentPlayer!!.setOnCompletionListener(onCompletionListener)
    }

    companion object {

        val TAG = LoopedPlayer::class.java.simpleName

        fun create(context: Context, resId: Int): LoopedPlayer {
            return LoopedPlayer(context, resId)
        }
    }
}