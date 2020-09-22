package yb.com.vocieRecoder.util.viewmodels

import android.view.View
import androidx.lifecycle.*
import yb.com.vocieRecoder.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import yb.com.vocieRecoder.AppDatabase
import yb.com.vocieRecoder.RecoderApplication
import yb.com.vocieRecoder.RecoderApplication.Companion.getGlobalContext
import yb.com.vocieRecoder.base.BaseViewModel
import yb.com.vocieRecoder.base.SingleLiveEvent
import yb.com.vocieRecoder.model.entity.TrainingEntity
import yb.com.vocieRecoder.model.repository.PlayerRepository
import yb.com.vocieRecoder.model.repository.PlayerRepository.PLAYER_STATE
import yb.com.vocieRecoder.model.repository.RecorderRepository
import yb.com.vocieRecoder.model.repository.RecorderRepository.RECORDER_STATE
import yb.com.vocieRecoder.util.CommonUtil

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
    private val _speakType: MutableLiveData<SPEAK_TYPE> = MutableLiveData<SPEAK_TYPE>()
    private val _bottomSheetShow: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val bottomSheetShow: LiveData<Boolean> get() = _bottomSheetShow
    val speakType: LiveData<SPEAK_TYPE> = _speakType
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
    private val _selfRecordEmptyEvent = SingleLiveEvent<Any>()

    val selfRecordEmptyEvent: LiveData<Any> get() = _selfRecordEmptyEvent
    val sdCardStorageErrorEvent: LiveData<Any> get() = _sdCardStorageErrorEvent
    val checkMicPermissionEvent: LiveData<Any> = _checkMicPermissionEvent
    val lastPageEvent: LiveData<Any> get() = _lastPageEvent

    val currentPositionObserver = Observer<Int> { currentPosition ->
        recordCancel()
        mediaStop()
        _isFirstPage.value = currentPosition == 0
        _isLastPage.value = currentPosition == trainingData.value?.size?.minus(1)
    }

    val recordStateObserver = Observer<RECORDER_STATE> {
        when (it) {
            RECORDER_STATE.CANCEL -> {

            }
            RECORDER_STATE.END -> {
                trainingData.value?.get(currentPosition.value ?: 0)?.isRecord = true

                AppDatabase.getInstance(getGlobalContext()).apply {
                    trainingDao().insertMedia(trainingData.value ?: return@apply)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { _, _ ->

                        }
                }

            }
        }
    }

    init {
        title.value = getGlobalContext().resources.getString(R.string.app_name)
        recordState.observeForever(recordStateObserver)
        currentPosition.observeForever(currentPositionObserver)

        AppDatabase.getInstance(getGlobalContext()).apply {
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

    fun checkMic() {
        _checkMicPermissionEvent.call()
    }

    fun mediaPlay(position: Int, type: SPEAK_TYPE) {
        mediaStop()
        _speakType.postValue(type)
        val mediaFile = trainingData.value?.get(position)?.mediaFile ?: return
        when (type) {
            SPEAK_TYPE.NATIVE -> {
                playerRepository.play(getGlobalContext().assets.openFd(mediaFile))
            }
            SPEAK_TYPE.SELF -> {
                if (trainingData.value?.get(position)?.isRecord == true) {
                    playerRepository.play(
                        CommonUtil.getPrivateMusicStorageDir(
                            getGlobalContext(),
                            mediaFile
                        )?.absolutePath
                            ?: return
                    )
                } else {
                    _selfRecordEmptyEvent.call()
                    return
                }
            }
        }
    }

    fun mediaStop() {
        playerRepository.stop()
    }

    fun record() {
        playerRepository.stop()

        when (recordState.value) {
            RECORDER_STATE.IDLE, RECORDER_STATE.CANCEL, RECORDER_STATE.END -> {
                trainingData.value?.get(currentPosition.value ?: 0)?.apply {
                    recorderRepository.startRecord(mediaFile, recordTime)
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

    fun stopRecord(path: String) {
        recorderRepository.stopRecord(path)
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
        closeBottomSheetIndex()
        currentPosition.removeObserver(currentPositionObserver)
        recordState.removeObserver(recordStateObserver)

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