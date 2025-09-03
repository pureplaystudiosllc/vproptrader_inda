package com.futureharvest.vproptrader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider

class TradingAccountComposeActivity : ComponentActivity(), BillingManager.BillingManagerListener {
    private lateinit var viewModel: TradingAccountViewModel
    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TradingAccountViewModel::class.java]
        billingManager = BillingManager(this, this)

        setContent {
            TradingAccountPurchaseScreen(
                viewModel = viewModel,
                onProductSelected = { product ->
                    billingManager.queryProductDetails(product.productId)
                },
                onBackPressed = { finish() }
            )
        }
    }

    override fun onBillingClientReady() {
        viewModel.updateProducts(billingManager.availableTradingAccountProducts)
    }

    override fun onProductDetailsLoaded(productDetailsList: MutableList<com.android.billingclient.api.ProductDetails>?) {
        if (!productDetailsList.isNullOrEmpty()) {
            billingManager.launchBillingFlow(this, productDetailsList[0])
        }
    }

    override fun onPurchaseSuccess(purchase: com.android.billingclient.api.Purchase?, product: BillingManager.TradingAccountProduct?) {
        setResult(RESULT_OK)
        finish()
    }

    override fun onPurchaseError(billingResult: com.android.billingclient.api.BillingResult?) { }

    override fun onPurchasesUpdated(purchases: MutableList<com.android.billingclient.api.Purchase>?) { }

    override fun onAcknowledgePurchaseResponse(billingResult: com.android.billingclient.api.BillingResult?) { }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.destroy()
    }
}
