package yb.com.vocieRecoder.base

import android.app.Activity
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import yb.com.vocieRecoder.R
import yb.com.vocieRecoder.RecoderApplication.Companion.getGlobalContext
import java.util.concurrent.TimeUnit

abstract class BaseViewModel : ViewModel() {
    protected val disposables: CompositeDisposable = CompositeDisposable()


    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}

