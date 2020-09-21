package yb.com.vocieRecoder.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import yb.com.vocieRecoder.base.SingleLiveEvent
import yb.com.vocieRecoder.model.entity.TrainingEntity
import yb.com.vocieRecoder.util.viewmodels.TrainingViewModel

class AdapterRepository {
    private val _trainigItem: MutableLiveData<TrainingEntity> = MutableLiveData<TrainingEntity>()

    val trainigItem: LiveData<TrainingEntity> get() = _trainigItem

    private val _packageEvent = SingleLiveEvent<String>()
    private val _teacherEvent = SingleLiveEvent<String>()
    private val _lectureEvent = SingleLiveEvent<String>()
    private val _freeStudyEvent = SingleLiveEvent<String>()
    private val _trainingMoveEvent = SingleLiveEvent<TrainingEntity>()
    private val _trainingSpeakEvent = SingleLiveEvent<TrainingViewModel.SPEAK_TYPE>()
    private val _trainingRetryEvent = SingleLiveEvent<TrainingEntity>()
    private val _linkEvent = SingleLiveEvent<String> ()

    val linkEvent : LiveData<String> get() = _linkEvent
    val trainingRetryEvent: LiveData<TrainingEntity> get() = _trainingRetryEvent
    val trainingSpeakEvent: LiveData<TrainingViewModel.SPEAK_TYPE> get() = _trainingSpeakEvent
    val trainingMoveEvent: LiveData<TrainingEntity> get() = _trainingMoveEvent
    val packageEvent: LiveData<String> get() = _packageEvent
    val teacherEvent: LiveData<String> get() = _teacherEvent
    val lectureEvent: LiveData<String> get() = _lectureEvent
    val freeStudyEvent: LiveData<String> get() = _freeStudyEvent


    fun packageEvent(url: String) {
        _packageEvent.value = url
    }

    fun teacherEvent(url: String) {
        _teacherEvent.value = url
    }

    fun lectureEvent(url: String) {
        _lectureEvent.value = url
    }

    fun linkEvent(url: String) {
        _linkEvent.value = url
    }

    fun freeStudyEvent(code: String) {
        _freeStudyEvent.value = code
    }

    fun trainingMoveEvent(item: TrainingEntity) {
        _trainingMoveEvent.postValue(item)
    }

    fun trainingRetryEvent(item: TrainingEntity) {
        _trainingRetryEvent.postValue(item)
    }

    fun mediaPlayEvent(item: TrainingEntity, speakType: TrainingViewModel.SPEAK_TYPE) {
        _trainigItem.value = item
        _trainingSpeakEvent.postValue(speakType)
    }
}