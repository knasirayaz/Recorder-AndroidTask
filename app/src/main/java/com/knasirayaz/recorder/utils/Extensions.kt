package com.knasirayaz.recorder.utils

import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar


fun View?.hide(){
    this?.visibility = View.GONE
}

fun View?.invisible(){
    this?.visibility = View.INVISIBLE
}

fun View?.show(){
    this?.visibility = View.VISIBLE
}

fun snackbar(v: View, message: Any?) {
    val snackbar = Snackbar.make(v, "$message", Snackbar.LENGTH_LONG)
    val snackbarView = snackbar.view
    val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.maxLines = 1
    snackbar.show()
}
