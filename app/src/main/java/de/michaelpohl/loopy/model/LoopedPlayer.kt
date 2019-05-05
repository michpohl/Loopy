package de.michaelpohl.loopy.model

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import timber.log.Timber

/*
the concept with two media players comes from here:
https://stackoverflow.com/questions/26274182/not-able-to-achieve-gapless-audio-looping-so-far-on-android
as of 12/2018 this still seems to be the only working way on android
 */
class LoopedPlayer private constructor(context: Context) {

    var hasLoopFile = false
    var isReady = false
        private set

    var state = PlayerState.UNKNOWN
        private set
    var switchingLoopsBehaviour = DataRepository.settings.switchingLoopsBehaviour
    lateinit var onLoopSwitchedListener: () -> Unit
    lateinit var onLoopedListener: (Int) -> Unit

    private var mContext: Context? = null
    private var mCounter = 1
    private var shouldBePlaying = false
    private var loopsElapsed = 0

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
        loopsElapsed += 1
        Timber.v("Loop Audio. Looped this file %s times", loopsElapsed)
        if (::onLoopedListener.isInitialized) onLoopedListener.invoke(loopsElapsed) //only do this if we have one. Is this smart?
        currentPlayer.setOnCompletionListener(onCompletionListener)
    }

    private fun createMediaPlayer(): MediaPlayer {
        return MediaPlayer.create(mContext, loopUri)
    }

    fun start() {
        //TODO show user if no file is selected yet
        shouldBePlaying = true
        loopsElapsed = 0
        currentPlayer.start()
        state = PlayerState.PLAYING
    }

    fun stop() {
        shouldBePlaying = false
        currentPlayer.setOnCompletionListener { null }
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

    fun setLoopUri(loopUri: Uri) {
            refreshSwitchingLoopsBehaviour()
        if (switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT && ::currentPlayer.isInitialized && isPlaying()) {
            currentPlayer.setOnCompletionListener {
                this.loopUri = loopUri
                if (hasLoopFile) stop()
                it.release()
                initPlayer()
                if (::onLoopSwitchedListener.isInitialized &&
                    switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT

                ) {
                    onLoopSwitchedListener.invoke()
                }
                start()
            }
        } else {
            this.loopUri = loopUri


            if (hasLoopFile) stop()
            initPlayer()
        }
        hasLoopFile = true
    }

    /**
     * Resets a set preSelection by setting the onCompletionListener back to its standard value
     */
    fun resetPreSelection() {

        //no need to do anything if currentPlayer is not initialized yet
        if (::currentPlayer.isInitialized) {
            currentPlayer.setOnCompletionListener(onCompletionListener)
        }
    }

    /**
     * Moves the player's current position to
     * @param newPosition
     * Since newPosition is basically a percentage value, we can use it to seek to the new position
     * by multiplying it with 1/100th of the file's duration
     */
    fun changePlaybackPosition(newPosition: Float) {
        val unit = currentPlayer.duration / 100
        currentPlayer.seekTo((newPosition * unit).toInt())
    }

    //this method is necessary so the user can change the behaviour on the fly
    private fun refreshSwitchingLoopsBehaviour() {
        this.switchingLoopsBehaviour = DataRepository.settings.switchingLoopsBehaviour
    }
}
