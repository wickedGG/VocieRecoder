package yb.com.vocieRecoder.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import yb.com.vocieRecoder.AppDatabase
import yb.com.vocieRecoder.R
import yb.com.vocieRecoder.RecoderApplication.Companion.getGlobalContext
import yb.com.vocieRecoder.base.BaseActivity

class IntroActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppDatabase.getInstance(getGlobalContext()).apply {
            trainingDao().getDatas()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isEmpty()) {
                        AppDatabase.getInstance(getGlobalContext()).trainingDao()
                            .insertMedia(AppDatabase.ConvertCSV())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { _, _ ->
                                moveMain()
                            }
                    } else {
                        moveMain()
                    }
                }
        }
    }

    private fun moveMain() {
        startActivity(Intent(this@IntroActivity, MainActivity::class.java))
    }
}