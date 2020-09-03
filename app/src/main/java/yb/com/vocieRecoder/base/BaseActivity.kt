package yb.com.vocieRecoder.base

import android.app.Activity
import android.bluetooth.BluetoothHeadset
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import io.reactivex.disposables.CompositeDisposable
import java.lang.reflect.InvocationTargetException


open class BaseActivity : AppCompatActivity() {
    lateinit var TAG: String
    protected val disposables by lazy { CompositeDisposable() }


    override fun onStart() {
        super.onStart()
        subscribeEvnet()
        initListner()
    }

    open fun initListner() {

    }

    open fun subscribeEvnet() {

    }


}