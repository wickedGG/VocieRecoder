package yb.com.vocieRecoder.util

import android.view.View
import androidx.databinding.BindingAdapter


object BindingAdpaterUtil {

    @JvmStatic
    @BindingAdapter("isSelected")
    fun bindIsSelected(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
    }


}



