package de.michaelpohl.loopy.model

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.content.FileProvider
import hugo.weaving.DebugLog
import timber.log.Timber
import java.io.File

/*
the concept with two media players comes from here: https://stackoverflow.com/questions/26274182/not-able-to-achieve-gapless-audio-looping-so-far-on-android
 */
@DebugLog
class LoopedPlayer private constructor(context: Context) {

    var hasLoopFile = false
    var isPaused = false

    private var mContext: Context? = null
    private var mCounter = 1
    private var shouldBePlaying = false

    lateinit var currentPlayer: MediaPlayer
    private lateinit var nextPlayer: MediaPlayer
    private lateinit var loopUri: Uri

    companion object {

        val TAG = LoopedPlayer::class.java.simpleName

        fun create(context: Context): LoopedPlayer {
            return LoopedPlayer(context)
        }
    }

    private val onCompletionListener = MediaPlayer.OnCompletionListener { mediaPlayer ->
        mediaPlayer.release()
        currentPlayer = nextPlayer

        createNextMediaPlayer()

        Timber.d(TAG, String.format("Loop #%d", ++mCounter))
    }

    init {
        mContext = context
    }

    fun getCurrentPosition(): Float {
        val unit = currentPlayer.duration / 100
        val currentPosition = currentPlayer.currentPosition / unit.toFloat()
        Timber.d("duration: %s, Unit: %s, Current position: %s", currentPlayer.duration, unit, currentPosition)
        return currentPosition
    }

    private fun initPlayer() {
        currentPlayer = createMediaPlayer()
        currentPlayer.setOnPreparedListener {

            if (shouldBePlaying) currentPlayer.start()
        }

        createNextMediaPlayer()
    }

    private fun createNextMediaPlayer() {
        nextPlayer = createMediaPlayer()
        currentPlayer.setNextMediaPlayer(nextPlayer)
        currentPlayer.setOnCompletionListener(onCompletionListener)
    }

    private fun createMediaPlayer(): MediaPlayer {
        val mediaPlayer = MediaPlayer.create(mContext, loopUri)
        return mediaPlayer
    }

    fun start() {
        //TODO show user if no file is selected yet
        shouldBePlaying = true
        isPaused = false
        currentPlayer.start()
    }

    fun stop() {
        shouldBePlaying = false
        currentPlayer.stop()
        nextPlayer.stop()
        initPlayer()
    }

    fun pause() {
        currentPlayer.pause()
        isPaused = true
    }

    fun isPlaying(): Boolean {
        return currentPlayer.isPlaying
    }

    fun setLoop(context: Context, loop: File) {
        if (hasLoopFile) stop()
        loopUri = FileProvider.getUriForFile(context, "com.de.michaelpohl.loopy", loop)
        Timber.d("This is my path: %s", loopUri.toString())
        initPlayer()
        hasLoopFile = true
    }
}