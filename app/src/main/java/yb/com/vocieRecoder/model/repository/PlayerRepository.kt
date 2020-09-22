package yb.com.vocieRecoder.model.repository

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException

class PlayerRepository {
    private val _playerState: MutableLiveData<PLAYER_STATE> =
        MutableLiveData<PLAYER_STATE>().apply { postValue(PLAYER_STATE.IDLE) }

    val playerState: LiveData<PLAYER_STATE> get() = _playerState

    private var player: MediaPlayer? = null

    fun play(path: String) {
        _playerState.postValue(PLAYER_STATE.PLAY)
        player = MediaPlayer().apply {
            try {
                setDataSource(path)
                setOnCompletionListener {
                    this@PlayerRepository.stop()
                }
                prepare()
                start()
            } catch (it: IOException) {
                it.printStackTrace()
            }
        }
    }

    fun play(afd: AssetFileDescriptor) {
        _playerState.postValue(PLAYER_STATE.PLAY)
        player = MediaPlayer().apply {
            try {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                setOnCompletionListener {
                    this@PlayerRepository.stop()
                }
                prepare()
                start()
            } catch (it: IOException) {
                it.printStackTrace()
            }
        }
    }

    fun stop() {
        _playerState.postValue(PLAYER_STATE.END)
        player?.release()
        player = null
    }

    fun onCleared() {
        stop()
    }

    companion object {
        // For Singleton instantiation.
        @Volatile
        private var instance: PlayerRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: PlayerRepository()
                        .also { instance = it }
            }
    }


    enum class PLAYER_STATE {
        IDLE, PLAY, END
    }
}