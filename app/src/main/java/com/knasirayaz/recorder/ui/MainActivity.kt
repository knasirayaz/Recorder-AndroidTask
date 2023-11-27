package com.knasirayaz.recorder.ui

import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.knasirayaz.recorder.R
import com.knasirayaz.recorder.base.BaseActivity
import com.knasirayaz.recorder.databinding.ActivityMainBinding
import com.knasirayaz.recorder.utils.BillingUtility
import com.knasirayaz.recorder.utils.InAppPurchaseItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity : BaseActivity<ActivityMainBinding>(){



    fun setTitle(title : String){
        binding.tvTitle.text = title
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
    private var billingUtility: BillingUtility? = null

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

        billingUtility = BillingUtility(this) {
            Log.e("","")
        }

        binding.btnBuyPremium.setOnClickListener {
            startPurchaseFlow(InAppPurchaseItems.PREMIUM)
        }



    }

    private fun startPurchaseFlow(inAppPurchaseItems: InAppPurchaseItems) {
        billingUtility?.purchaseItem(inAppPurchaseItems)
    }


}