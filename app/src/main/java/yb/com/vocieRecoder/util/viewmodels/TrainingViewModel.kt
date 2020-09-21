package yb.com.vocieRecoder.util.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import yb.com.vocieRecoder.AppDatabase
import yb.com.vocieRecoder.RecoderApplication
import yb.com.vocieRecoder.base.BaseViewModel
import yb.com.vocieRecoder.base.SingleLiveEvent
import yb.com.vocieRecoder.model.entity.TrainingEntity
import yb.com.vocieRecoder.model.repository.PlayerRepository
import yb.com.vocieRecoder.model.repository.PlayerRepository.PLAYER_STATE
import yb.com.vocieRecoder.model.repository.RecorderRepository
import yb.com.vocieRecoder.model.repository.RecorderRepository.RECORDER_STATE

class TrainingViewModel(
    val playerRepository: PlayerRepository,
    val recorderRepository: RecorderRepository
) : BaseViewModel() {
    val title = MutableLiveData<String>()

    var trainingMap = HashMap<String, List<TrainingEntity>>()
    private val _trainingData: MutableLiveData<List<TrainingEntity>> =
        MutableLiveData<List<TrainingEntity>>()

    private val _currentPosition: MutableLiveData<Int> =
        MutableLiveData<Int>().apply { postValue(0) }
    private val _isFirstPage = MutableLiveData<Boolean>().apply { postValue(true) }
    private val _isLastPage = MutableLiveData<Boolean>().apply { postValue(false) }
    private val _bottomSheetShow: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val bottomSheetShow: LiveData<Boolean> get() = _bottomSheetShow
    val trainingData: LiveData<List<TrainingEntity>> get() = _trainingData
    val isFirstPage: LiveData<Boolean> get() = _isFirstPage
    val isLastPage: LiveData<Boolean> get() = _isLastPage
    val currentPosition: LiveData<Int> get() = _currentPosition
    val playerState: LiveData<PLAYER_STATE> get() = playerRepository.playerState
    val recordState: LiveData<RECORDER_STATE> get() = recorderRepository.recordState
    val recordTime: LiveData<Int> get() = recorderRepository.recordTime

    private val _lastPageEvent = SingleLiveEvent<Any>()
    private val _checkMicPermissionEvent = SingleLiveEvent<Any>()
    private val _sdCardStorageErrorEvent = SingleLiveEvent<Any>()

    val sdCardStorageErrorEvent: LiveData<Any> get() = _sdCardStorageErrorEvent
    val checkMicPermissionEvent: LiveData<Any> = _checkMicPermissionEvent
    val lastPageEvent: LiveData<Any> get() = _lastPageEvent

    val currentPositionObserver = Observer<Int> {

    }

    init {
        currentPosition.observeForever(currentPositionObserver)

        AppDatabase.getInstance(RecoderApplication.getGlobalContext()).apply {
            trainingDao().getDatas()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    trainingMap = it.groupBy { it.category } as HashMap
                    _trainingData.postValue(it)
                    closeBottomSheetIndex()
                }
        }
    }

    fun record() {
        playerRepository.stop()

        when (recordState.value) {
            RECORDER_STATE.IDLE, RECORDER_STATE.CANCEL, RECORDER_STATE.END -> {
                trainingData.value?.get(currentPosition.value ?: 0)?.apply {
                    recorderRepository.startRecord(
                        mediaFile,
                        recordTime
                    )
                }
            }

            RECORDER_STATE.RECORDING -> {
                recorderRepository.cancelRecord()
            }
        }
    }

    fun recordCancel() {
        recorderRepository.cancelRecord()
    }

    fun checkMic() {
        _checkMicPermissionEvent.call()
    }

    fun changePosition(position: Int) {
        _currentPosition.postValue(position)
        closeBottomSheetIndex()
    }

    fun showBottomSheetIndex() {
        _bottomSheetShow.postValue(true)
    }

    fun closeBottomSheetIndex() {
        _bottomSheetShow.postValue(false)
    }

    fun getTrainingMapIndex(position: Int): Int {
        return trainingData.value?.get(position)?.let {
            trainingMap[it.category]?.indexOf(it)
        } ?: 0
    }

    fun isFileSystemAvailable(): Boolean {
        return recorderRepository.isFileSystemAvailable().also {
            if (it == false) {
                _sdCardStorageErrorEvent.call()
            }
        }
    }

    var pageChangeButtonListener: View.OnClickListener = View.OnClickListener {
        // -1 : 왼쪽 방향
        // 1 : 오른쪽 방향
        val tag = Integer.valueOf(it.tag.toString())

        _currentPosition.value?.plus(tag)?.let { position ->
            if (isLastPage.value!! && tag > 0) {

            } else {
                _currentPosition.value = _currentPosition.value?.plus(tag)
            }

            _isFirstPage.value = position == 0
            _isLastPage.value = position == trainingData.value?.size?.minus(1)
        }
    }


    override fun onCleared() {
        super.onCleared()
        currentPosition.removeObserver(currentPositionObserver)

        // TODO END 처리 요청 하거나 onCleared()호출로 하던가
        playerRepository.onCleared()
        recorderRepository.onCleared()
    }


    class Factory(
        private val playerRepository: PlayerRepository,
        private val recorderRepository: RecorderRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TrainingViewModel(playerRepository, recorderRepository) as T
        }
    }

    enum class SPEAK_TYPE {
        SELF, NATIVE
    }
}