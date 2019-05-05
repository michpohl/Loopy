package de.michaelpohl.loopy.model

import android.content.Context
import android.net.Uri
import android.os.Binder
import android.os.Handler
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import de.michaelpohl.loopy.ui.main.player.PlayerViewModel
import timber.log.Timber

class PlayerServiceBinder(serviceContext: Context) : Binder(),
    ILooper {

    private var looper = LoopedPlayer.create(serviceContext)

    // This Handler object is a reference to the caller activity's Handler.
    // In the caller activity's handler, it will update the audio start progress.
    var audioProgressUpdateHandler: Handler? = null

    // This is the message signal that inform audio progress updater to update audio progress.
    val UPDATE_AUDIO_PROGRESS_BAR = 1

//    // Return current audio start position.
//    val currentAudioPosition: Float
//        get() {
//            var ret = 0
//            ret = looper!!.getCurrentPosition()
//            return ret
//        }
//
//    // Return total audio file duration.
//    val totalAudioDuration: Int
//        get() {
//            var ret = 0
//            ret = looper!!.duration
//            return ret
//        }
//
//    // Return current audio player progress value.
//    val audioProgress: Int
//        get() {
//            var ret = 0
//            val currAudioPosition = currentAudioPosition
//            val totalAudioDuration = totalAudioDuration
//            if (totalAudioDuration > 0) {
//                ret = currAudioPosition * 100 / totalAudioDuration
//            }
//            return ret
//        }

    override fun start() {
//        initPlayer()
        Timber.d("Start in Binder")
        looper.start()
    }

    override fun pause() {
        looper.pause()
    }

    override fun stop() {
        looper.stop()
        destroyAudioPlayer()
    }

    override fun changePlaybackPosition(newPosition: Float) =
        looper.changePlaybackPosition(newPosition)

    override fun resetPreSelection() = looper.resetPreSelection()

    override fun isReady(): Boolean {
        return looper.isReady
    }

    override fun isPlaying(): Boolean {
        return looper.isPlaying()
    }

    override fun getState(): PlayerState {
        return looper.state
    }

    override fun setHasLoopFile(hasFile: Boolean) {
        looper.hasLoopFile = hasFile
    }

    override fun setOnLoopedListener(receiver: (Int) -> Unit) {
        looper.onLoopedListener = receiver
    }

    override fun setOnLoopSwitchedListener(receiver: () -> Unit) {
        looper.onLoopSwitchedListener = receiver
    }

    override fun setLoopUri(uri: Uri) = looper.setLoopUri(uri)


    override fun setSwitchingLoopsBehaviour(behaviour: SwitchingLoopsBehaviour) {
        looper.switchingLoopsBehaviour = behaviour
    }

    override fun getSwitchingLoopsBehaviour(): SwitchingLoopsBehaviour {
    return looper.switchingLoopsBehaviour
    }

    override fun getCurrentPosition() = looper.getCurrentPosition()

    // Initialise audio player.
    private fun initPlayer() {
//        try {
//            if (looper == null) {
//                looper = MediaPlayer()
//
//                if (!TextUtils.isEmpty(audioFileUrl)) {
//                    if (isStreamAudio) {
//                        looper!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
//                    }
//                    looper!!.setDataSource(audioFileUrl)
//                } else {
//                    looper!!.setDataSource(context!!, audioFileUri!!)
//                }
//
//                looper!!.prepare()
//
//                // This thread object will send update audio progress message to caller activity every 1 second.
//                val updateAudioProgressThread = object : Thread() {
//                    override fun run() {
//                        while (true) {
//                            // Create update audio progress message.
//                            val updateAudioProgressMsg = Message()
//                            updateAudioProgressMsg.what = UPDATE_AUDIO_PROGRESS_BAR
//
//                            // Send the message to caller activity's update audio prgressbar Handler object.
//                            audioProgressUpdateHandler!!.sendMessage(updateAudioProgressMsg)
//
//                            // Sleep one second.
//                            try {
//                                Thread.sleep(1000)
//                            } catch (ex: InterruptedException) {
//                                ex.printStackTrace()
//                            }
//                        }
//                    }
//                }
//                // Run above thread object.
//                updateAudioProgressThread.start()
//            }
//        } catch (ex: IOException) {
//            ex.printStackTrace()
//        }
    }

    override fun preselect() {
    }

    override fun select() {
    }

    // Destroy audio player.
    private fun destroyAudioPlayer() {
        if (looper.state == PlayerState.PLAYING) {
            looper.stop()
        }
//TODO properly release looper
//        looper.release()
    }
}
