package yb.com.vocieRecoder.ui

import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_training.*
import yb.com.vocieRecoder.R
import yb.com.vocieRecoder.RecoderConfig
import yb.com.vocieRecoder.adapter.TrainingContentAdapter
import yb.com.vocieRecoder.base.BaseActivity
import yb.com.vocieRecoder.databinding.ActivityMainBinding
import yb.com.vocieRecoder.model.entity.TrainingEntity
import yb.com.vocieRecoder.model.repository.AdapterRepository
import yb.com.vocieRecoder.util.CommonUtil
import yb.com.vocieRecoder.util.InjectorUtils
import yb.com.vocieRecoder.model.repository.RecorderRepository
import yb.com.vocieRecoder.util.viewmodels.TrainingViewModel

class MainActivity : BaseActivity() {
    private lateinit var viewDataBinding: ActivityMainBinding
    private lateinit var trainingViewModel: TrainingViewModel

    private val adapterRepository = AdapterRepository()
    private var trainingContentAdapter =
        TrainingContentAdapter(adapterRepository).apply { setHasStableIds(true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        trainingViewModel = ViewModelProvider(this, InjectorUtils.provideTrainingViewModel())
            .get(TrainingViewModel::class.java)

        viewDataBinding.lifecycleOwner = this
        viewDataBinding.trainingViewModel = trainingViewModel
        viewDataBinding.commonUtil = CommonUtil

        rc_training.apply {
            setHasFixedSize(true)
            adapter = trainingContentAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

    }


    override fun subscribeEvnet() {

        trainingViewModel.sdCardStorageErrorEvent.observe(this, Observer {
            Snackbar.make(
                viewDataBinding.root,
                getString(R.string.training_record_msg_sdcard_full),
                Snackbar.LENGTH_SHORT
            ).show()
        })

        trainingViewModel.checkMicPermissionEvent.observe(this, Observer {
            if (trainingViewModel.isFileSystemAvailable()) {
                var result = false
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    result = true
                }
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    result = true
                }
                if (result) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO
                        ),
                        RecoderConfig.EXTRA_REQUEST_PERMISSION
                    )
                }
                // 권한 승인
                else {
                    trainingViewModel.record()
                }
            }
        })

        trainingViewModel.bottomSheetShow.observe(this, Observer {
            if (it) {
                sp_category.apply {
                    adapter = object : ArrayAdapter<String>(
                        context,
                        R.layout.item_spinner_text,
                        trainingViewModel.trainingMap.keys.toList()
                    ) {
                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getDropDownView(position, null, parent)
                            if (position == sp_category.selectedItemPosition) {
                                (v as TextView).apply {
                                    setBackgroundColor(Color.parseColor("#242424"))
                                    setTextColor(Color.parseColor("#bb2a2d"))
                                }
                            } else {
                                (v as TextView).apply {
                                    setBackgroundColor(Color.parseColor("#242424"))
                                    setTextColor(Color.parseColor("#99FFFFFF"))
                                }
                            }
                            return v
                        }
                    }
                    setSelection(trainingViewModel.trainingMap.keys.indexOf(tv_category.text))
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }

                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            position: Int,
                            id: Long
                        ) {
                            trainingContentAdapter.apply {
                                selectedItem = trainingViewModel.trainingData.value?.get(
                                    trainingViewModel.currentPosition.value
                                        ?: 0
                                )
                                setData(trainingViewModel.trainingMap[(p1 as TextView).text] as ArrayList<TrainingEntity>?)
                            }
                            trainingContentAdapter.getItemSelectedPosition().let {
                                if (it >= 0) {
                                    (rc_training.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                                        it,
                                        0
                                    )
                                }
                            }
                        }
                    }
                }
                BottomSheetBehavior.from(layout_bottom_index).peekHeight = 1200
            } else {
                BottomSheetBehavior.from(layout_bottom_index).peekHeight = 0
            }
        })

        trainingViewModel.selfRecordEmptyEvent.observe(this, Observer {
            Toast.makeText(
                this,
                getText(R.string.training_content_msg_self_record_empty),
                Toast.LENGTH_SHORT
            ).show()
        })

        adapterRepository.trainingMoveEvent.observe(this, Observer { item ->
            trainingViewModel.trainingData.value?.indexOf(item)?.let {
                trainingViewModel.changePosition(it)
            }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RecoderConfig.EXTRA_REQUEST_PERMISSION -> {
                var permissionResult = true
                permissions.forEachIndexed { index, s ->
                    if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                        permissionResult = false
                    }
                }
                if (permissionResult) {
                    trainingViewModel.record()
                }
            }
        }
    }


}