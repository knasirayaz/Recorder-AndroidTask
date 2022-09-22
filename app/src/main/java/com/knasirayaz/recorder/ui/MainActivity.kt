package com.knasirayaz.recorder.ui

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.knasirayaz.recorder.R
import com.knasirayaz.recorder.base.BaseActivity
import com.knasirayaz.recorder.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity : BaseActivity<ActivityMainBinding>(){



    fun setTitle(title : String){
        binding.tvTitle.text = title
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition{
                runBlocking {
                    delay(700)
                    false
                }
            }
        }

    }

}