package yb.com.vocieRecoder.util.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import yb.com.vocieRecoder.base.BaseViewModel
import yb.com.vocieRecoder.model.entity.TrainingEntity
import yb.com.vocieRecoder.model.repository.AdapterRepository

class AdapterViewModel() : BaseViewModel() {

    private var repository: AdapterRepository? = null

    constructor(_repository: AdapterRepository) : this() {
        repository = _repository
    }

    fun packageEvent(url: String) {
        repository?.packageEvent(url)
    }

    fun teacherEvent(url: String) {
        repository?.teacherEvent(url)
    }

    fun lectureEvent(url: String) {
        repository?.lectureEvent(url)
    }

    fun linkEvent(url: String) {
        repository?.linkEvent(url)
    }

    fun trainingMoveEvent(item: TrainingEntity) {
        repository?.trainingMoveEvent(item)
    }

    fun trainingRetryEvent(item: TrainingEntity) {
        repository?.trainingRetryEvent(item)
    }

    fun mediaPlayEvent(item: TrainingEntity, speakType: TrainingViewModel.SPEAK_TYPE) {
        repository?.mediaPlayEvent(item, speakType)
    }

    class Factory() : ViewModelProvider.NewInstanceFactory() {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AdapterViewModel() as T
        }
    }

}