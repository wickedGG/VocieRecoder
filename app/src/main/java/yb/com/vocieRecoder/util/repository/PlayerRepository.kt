package yb.com.vocieRecoder.util.repository

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import yb.com.vocieRecoder.ui.MainActivity
import java.io.IOException

class PlayerRepository {

    private val _playerState: MutableLiveData<PLAYER_STATE> =
        MutableLiveData<PLAYER_STATE>().apply { postValue(PLAYER_STATE.IDLE) }

    val playerState: LiveData<PLAYER_STATE> get() = _playerState

    private var player: MediaPlayer? = null


    fun paly(path: String) {
        player = MediaPlayer().apply {
            try {
                setDataSource(path)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("test", "prepare() failed")
            }
        }
    }

    fun stop() {
        player?.release()
        player = null
    }

    fun onCleared() {

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