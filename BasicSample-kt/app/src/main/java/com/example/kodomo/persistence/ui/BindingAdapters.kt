package com.example.kodomo.persistence.ui

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * @author linxiaotao
 * 2018/12/25 上午9:34
 */
@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}