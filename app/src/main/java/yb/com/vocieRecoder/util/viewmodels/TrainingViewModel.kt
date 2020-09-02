package yb.com.vocieRecoder.util.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import yb.com.vocieRecoder.base.BaseViewModel
import yb.com.vocieRecoder.util.repository.RecorderRepository

class TrainingViewModel(
    recorderRepository: RecorderRepository
) : BaseViewModel() {


    class Factory(
        private val recorderRepository: RecorderRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TrainingViewModel(recorderRepository) as T
        }
    }
}