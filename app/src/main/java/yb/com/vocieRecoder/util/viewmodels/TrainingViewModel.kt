package yb.com.vocieRecoder.util.viewmodels

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import yb.com.vocieRecoder.AppDatabase
import yb.com.vocieRecoder.R
import yb.com.vocieRecoder.RecoderApplication
import yb.com.vocieRecoder.RecoderApplication.Companion.getGlobalContext
import yb.com.vocieRecoder.base.BaseViewModel
import yb.com.vocieRecoder.base.SingleLiveEvent
import yb.com.vocieRecoder.model.TrainingEntity
import yb.com.vocieRecoder.util.repository.PlayerRepository
import yb.com.vocieRecoder.util.repository.PlayerRepository.PLAYER_STATE
import yb.com.vocieRecoder.util.repository.RecorderRepository
import yb.com.vocieRecoder.util.repository.RecorderRepository.RECORDER_STATE

class TrainingViewModel(
    val playerRepository: PlayerRepository,
    val recorderRepository: RecorderRepository
) : BaseViewModel() {
    private val _trainingData: MutableLiveData<List<TrainingEntity>> =
        MutableLiveData<List<TrainingEntity>>()

    private val _currentPosition: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        postValue(0)
    }
    private val _isFirstPage = MutableLiveData<Boolean>().apply {
        postValue(true)
    }
    private val _isLastPage = MutableLiveData<Boolean>().apply {
        postValue(false)
    }

    val trainingData: LiveData<List<TrainingEntity>> get() = _trainingData
    val isFirstPage: LiveData<Boolean> get() = _isFirstPage
    val isLastPage: LiveData<Boolean> get() = _isLastPage
    val currentPosition: LiveData<Int> get() = _currentPosition
    val playerState: LiveData<PLAYER_STATE> get() = playerRepository.playerState
    val recordState: LiveData<RECORDER_STATE> get() = recorderRepository.recordState
    val recordTime: LiveData<Int> get() = recorderRepository.recordTime

    private val _lastPageEvent = SingleLiveEvent<Any>()

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
                    _trainingData.postValue(it)
                }
        }
    }

    fun record() {
        playerRepository.stop()

        when (recordState.value) {
            RECORDER_STATE.IDLE, RECORDER_STATE.CANCEL, RECORDER_STATE.END -> {
                trainingData.value?.get(currentPosition.value ?: 0)?.apply {
                    recorderRepository.startRecord(mediaFile,
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
}