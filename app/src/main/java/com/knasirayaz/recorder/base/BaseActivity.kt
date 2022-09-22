package com.knasirayaz.recorder.base

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity

abstract class BaseActivity<DataBinding : ViewDataBinding> : FragmentActivity() {
    private lateinit var dataBinding: DataBinding
    protected val binding get() = dataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, getLayoutId()) //DataBindingUtil.inflate(layoutInflater, getLayoutId(), null, false)

    }
    abstract fun getLayoutId(): Int
}