package de.michaelpohl.loopy.model

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.content.FileProvider
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import timber.log.Timber
import java.io.File

/*
the concept with two media players comes from here:
https://stackoverflow.com/questions/26274182/not-able-to-achieve-gapless-audio-looping-so-far-on-android
as of 12/2018 this still seems to be the only working way on android
 */
class LoopedPlayer private constructor(context: Context) {

    var hasLoopFile = false
    var isReady = false
        private set
    //TODO change playing, paused into status enum: PAYING,PAUSED,,STOPPED,UNKNOWN

    var state = PlayerState.UNKNOWN
        private set
    var switchingLoopsBehaviour = LoopsRepository.settings.switchingLoopsBehaviour
    lateinit var onLoopSwitchedListener: () -> Unit

    private var mContext: Context? = null
    private var mCounter = 1
    private var shouldBePlaying = false
    private var loops = 0

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

    /**
    returns the percentage of the current file that's been played already
     */
    fun getCurrentPosition(): Float {
        val unit = currentPlayer.duration / 100
        return currentPlayer.currentPosition / unit.toFloat()
    }

    private fun initPlayer() {
        currentPlayer = createMediaPlayer()
        currentPlayer.setOnPreparedListener {

            if (shouldBePlaying) currentPlayer.start()
        }
        isReady = true
        createNextMediaPlayer()
    }

    private fun createNextMediaPlayer() {
        nextPlayer = createMediaPlayer()
        currentPlayer.setNextMediaPlayer(nextPlayer)
        loops += 1
        Timber.v("Loop Audio. Looped this file %s times", loops)
        currentPlayer.setOnCompletionListener(onCompletionListener)
    }

    private fun createMediaPlayer(): MediaPlayer {
        val mediaPlayer = MediaPlayer.create(mContext, loopUri)
        return mediaPlayer
    }

    fun start() {
        //TODO show user if no file is selected yet
        shouldBePlaying = true
        loops = 0
        currentPlayer.start()
        if (::onLoopSwitchedListener.isInitialized &&
            switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT
        ) {
            onLoopSwitchedListener.invoke()
        }
        state = PlayerState.PLAYING
    }

    fun stop() {
        shouldBePlaying = false
        currentPlayer.stop()
        nextPlayer.stop()
        state = PlayerState.STOPPED
        initPlayer()
    }

    fun pause() {
        currentPlayer.pause()
        state = PlayerState.PAUSED
    }


    fun isPlaying(): Boolean {
        return currentPlayer.isPlaying
    }

    fun setLoop(context: Context, loop: File) {
        loopUri = FileProvider.getUriForFile(context, "com.de.michaelpohl.loopy", loop)

        if (switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT && ::currentPlayer.isInitialized) {
            currentPlayer.setOnCompletionListener {
                if (hasLoopFile) stop()
                initPlayer()
                start()
            }
        } else {
            if (hasLoopFile) stop()

            initPlayer()
        }
        hasLoopFile = true
    }
}