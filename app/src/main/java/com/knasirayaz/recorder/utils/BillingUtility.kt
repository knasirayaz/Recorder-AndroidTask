package com.knasirayaz.recorder.utils

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*


/**
 * Created by zeerak on 2/22/2020 bt
 */
class BillingUtility(val context: Activity, val onPurchased: (() -> Unit)) :
    PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null
    private var mProductDetails: MutableMap<String, ProductDetails> = mutableMapOf()
    var isPurchasedApp = false
    var isEliteThemePurchased = false

    private var productsList = ArrayList<QueryProductDetailsParams.Product>()

    init {
        addProducts()
        setupBillingClient()
    }

    private fun addProducts() {
        productsList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(InAppPurchaseItems.PREMIUM.skuId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        productsList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(InAppPurchaseItems.THEME.skuId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryAvailableProducts()
                    isRemoveAdsPurchased {
                        isPurchasedApp = it
                        if (it) {
                            onPurchased.invoke()
                        }
                    }
                    isEliteThemePurchase {
                        isEliteThemePurchased = it
                    }

                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e("", "")
            }
        })
    }

    private fun isRemoveAdsPurchased(callback: (isPurchased: Boolean) -> Unit) {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP).build()
        billingClient!!.queryPurchasesAsync(params) { billingResult, list ->
            if (list.isEmpty()) {
                callback.invoke(false)
            } else {
                for (purchase in list) {
                    if (purchase.products.contains(mProductDetails[InAppPurchaseItems.PREMIUM.skuId]?.productId)) {
                        callback.invoke(true)
                    }
                }
                callback.invoke(false)
            }
        }
    }

    private fun isEliteThemePurchase(callback: (isPurchased: Boolean) -> Unit) {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP).build()

        billingClient!!.queryPurchasesAsync(params) { billingResult, list ->
            if (list.isEmpty()) {
                callback.invoke(false)
            } else {
                for (purchase in list) {
                    if (purchase.products.contains(mProductDetails[InAppPurchaseItems.THEME.skuId]?.productId)) {
                        callback.invoke(true)
                    }
                }
                callback.invoke(false)
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handleConsumedPurchases(purchase)
            }
            onPurchased.invoke()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            if (purchases != null) {
                for (purchase in purchases) {
                    setPurchasesAccordingly(purchase, toAcknowledge = false)
                }
            }
            onPurchased.invoke()
        }
    }

    private fun handleConsumedPurchases(purchase: Purchase) {
        setPurchasesAccordingly(purchase, toAcknowledge = false)
        /*val params = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient?.consumeAsync(params) { billingResult, purchaseToken ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    setPurchasesAccordingly(purchase, toAcknowledge = false)
                    // Update the appropriate tables/databases to grant user the items
                }

                else -> {
                }
            }
        }*/
    }

    private fun setPurchasesAccordingly(vararg purchases: Purchase, toAcknowledge: Boolean) {
        for (purchase in purchases) {
            if (toAcknowledge)
                acknowledgePurchase(purchase.purchaseToken)
            if (purchase.products.get(0) == InAppPurchaseItems.PREMIUM.skuId) {
                isPurchasedApp = true
                onPurchased.invoke()
            } else if (purchase.products.get(0) == InAppPurchaseItems.THEME.skuId) {
                isEliteThemePurchased = true
                onPurchased.invoke()
            } else if (purchase.products.get(0) == InAppPurchaseItems.PREMIUM.skuId && purchase.products.get(
                    0
                ) == InAppPurchaseItems.THEME.skuId
            ) {
                isPurchasedApp = true
                isEliteThemePurchased = true
                onPurchased.invoke()
            }
        }
    }

    interface IPurchaseFollowListener {
        fun onItemPurchased()
    }

    private fun queryAvailableProducts() {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(productsList)
                .build()

        billingClient?.queryProductDetailsAsync(queryProductDetailsParams) { billingResult,
                                                                             productDetailsList ->
            for (skuDetails in productDetailsList) {
                mProductDetails[skuDetails.productId] = skuDetails
            }
        }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient?.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
        }
    }

    fun purchaseItem(inAppPurchaseItems: InAppPurchaseItems) {
        val productDetailsParamsList = listOf(
            mProductDetails[inAppPurchaseItems.skuId]?.let {
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(it)
                    .build()
            }
        )

        try {
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            billingClient?.launchBillingFlow(context, billingFlowParams)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("EXCEPTION", e.stackTrace.toString())

            if(productsList.isEmpty()){
                addProducts()
            }
            setupBillingClient()
        }
    }

}

enum class InAppPurchaseItems(var skuId: String) {
    THEME("com.knasirayaz.recorder.elite_theme"),
    PREMIUM("com.knasirayaz.recorder.premium")
}