package yb.com.vocieRecoder.model.repository

import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import yb.com.vocieRecoder.RecoderApplication.Companion.getGlobalContext
import yb.com.vocieRecoder.RecoderConfig
import yb.com.vocieRecoder.util.CommonUtil
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class RecorderRepository {
    protected val disposables: CompositeDisposable = CompositeDisposable()
    private val _recordState: MutableLiveData<RECORDER_STATE> =
        MutableLiveData<RECORDER_STATE>().apply { postValue(RECORDER_STATE.IDLE) }
    private val _recordTime: MutableLiveData<Int> = MutableLiveData<Int>()

    val recordTime: LiveData<Int> get() = _recordTime
    val recordState: LiveData<RECORDER_STATE> get() = _recordState


    private var recorder: MediaRecorder? = null

    val recordStateObserver = Observer<RECORDER_STATE> {
        when (it) {
            RECORDER_STATE.END -> {
                _recordState.postValue(RECORDER_STATE.IDLE)
            }
        }
    }

    init {
        recordState.observeForever(recordStateObserver)
    }


    fun startRecord(path: String, recordTime: Int) {
        disposables.clear()
        _recordTime.postValue(0)
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile("${getGlobalContext().externalCacheDir?.absolutePath}/${path}")
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            } catch (it: IOException) {
                it.printStackTrace()
            }
            start()
            _recordState.postValue(RECORDER_STATE.RECORDING)
        }

        disposables.add(Flowable.interval(1, TimeUnit.SECONDS)
            .takeWhile {
                it <= recordTime
            }
            .observeOn(Schedulers.io())
            .onBackpressureDrop()
            .doOnNext {
                _recordTime.postValue((recordTime.minus(it) * 1000).toInt())
            }.doOnCancel {
                if (recorder != null && recordState.value != (RECORDER_STATE.END)) {
                    _recordState.postValue(RECORDER_STATE.CANCEL)
                    realseRecord()
                }
            }
            .subscribe {
                if (it >= recordTime) {
                    stopRecord(path)
                }
            })
    }

    fun cancelRecord() {
        disposables.clear()
    }

    fun stopRecord(path: String) {
        realseRecord()
        copyRecord(path)
        _recordState.postValue(RECORDER_STATE.END)
        disposables.clear()
    }

    fun realseRecord() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }


    fun copyRecord(path: String) {
        val ori = File("${getGlobalContext().externalCacheDir?.absolutePath}/${path}")
        val copy = CommonUtil.getPrivateMusicStorageDir(getGlobalContext(), path) ?: return
        CommonUtil.copyStreamToFile(copy, ori.inputStream())
    }

    fun isFileSystemAvailable(): Boolean {
        val file = Environment.getExternalStorageDirectory()
        return file.freeSpace > RecoderConfig.MAX_FILE_SIZE
    }


    fun onCleared() {
        cancelRecord()
        recordState.removeObserver(recordStateObserver)
    }

    companion object {
        @Volatile
        private var instance: RecorderRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: RecorderRepository()
                        .also { instance = it }
            }
    }


    enum class RECORDER_STATE {
        IDLE, RECORDING, CANCEL, END
    }
}