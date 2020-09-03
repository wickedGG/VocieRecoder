package yb.com.vocieRecoder.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import yb.com.vocieRecoder.R
import yb.com.vocieRecoder.RecoderApplication
import yb.com.vocieRecoder.RecoderConfig
import yb.com.vocieRecoder.base.BaseActivity
import yb.com.vocieRecoder.databinding.ActivityMainBinding
import yb.com.vocieRecoder.util.CommonUtil
import yb.com.vocieRecoder.util.InjectorUtils
import yb.com.vocieRecoder.util.repository.RecorderRepository
import yb.com.vocieRecoder.util.viewmodels.TrainingViewModel
import java.io.IOException

class MainActivity : BaseActivity() {
    private lateinit var viewDataBinding: ActivityMainBinding
    private lateinit var trainingViewModel: TrainingViewModel

    private var fileName: String = ""

    private var recorder: MediaRecorder? = null

    private var player: MediaPlayer? = null

    private var permissionToRecordAccepted = false
    private var permissions: Array<String> =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    internal inner class RecordButton(ctx: Context) : AppCompatButton(ctx) {

        var mStartRecording = true

        var clicker: OnClickListener = OnClickListener {
            onRecord(mStartRecording)
            text = when (mStartRecording) {
                true -> "Stop recording"
                false -> "Start recording"
            }
            mStartRecording = !mStartRecording
        }

        init {
            text = "Start recording"
            setOnClickListener(clicker)
        }
    }

    internal inner class PlayButton(ctx: Context) : AppCompatButton(ctx) {
        var mStartPlaying = true
        var clicker: OnClickListener = OnClickListener {
            onPlay(mStartPlaying)
            text = when (mStartPlaying) {
                true -> "Stop playing"
                false -> "Start playing"
            }
            mStartPlaying = !mStartPlaying
        }

        init {
            text = "Start playing"
            setOnClickListener(clicker)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        trainingViewModel = ViewModelProvider(this, InjectorUtils.provideTrainingViewModel())
            .get(TrainingViewModel::class.java)

        viewDataBinding.lifecycleOwner = this
        viewDataBinding.trainingViewModel = trainingViewModel
        viewDataBinding.commonUtil = CommonUtil

        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"

        ActivityCompat.requestPermissions(this, permissions, RecoderConfig.REQUEST_AUDIO_PERMISSION)
    }

    override fun subscribeEvnet() {

        trainingViewModel.recordState.observe(this, {
            when (it) {
                RecorderRepository.RECORDER_STATE.IDLE, RecorderRepository.RECORDER_STATE.CANCEL, RecorderRepository.RECORDER_STATE.END -> {
                    if (viewDataBinding.ivRecord.drawable is AnimationDrawable) {
                        (viewDataBinding.ivRecord.drawable as AnimationDrawable).stop()
                    }
                    viewDataBinding.ivRecord.setImageDrawable(getDrawable(R.drawable.ic_round_mic_24))
                }

                RecorderRepository.RECORDER_STATE.RECORDING -> {

                    getDrawable(R.drawable.anim_record).apply {
                        viewDataBinding.ivRecord.setImageDrawable(this)
                        (this as AnimationDrawable).start()
                    }
                }

                RecorderRepository.RECORDER_STATE.CANCEL, RecorderRepository.RECORDER_STATE.END -> {

                }
            }
        })

    }


    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("test", "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("test", "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }


    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == RecoderConfig.REQUEST_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

}