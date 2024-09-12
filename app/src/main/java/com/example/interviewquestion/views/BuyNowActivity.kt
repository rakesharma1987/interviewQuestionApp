package com.example.interviewquestion.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.example.interviewquestion.R
import com.example.interviewquestion.databinding.ActivityBuyNowBinding

class BuyNowActivity : AppCompatActivity(), View.OnClickListener, PurchasesUpdatedListener {
    lateinit var binding: ActivityBuyNowBinding
    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyNowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBuynow.setOnClickListener(this)
        binding.btnRestore.setOnClickListener(this)

        // Initialize BillingClient
        billingClient = BillingClient.newBuilder(this)
            .setListener(this) // Purchase update listener
            .enablePendingPurchases() // Required for pending purchases
            .build()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_buynow -> {
                connectBillingClient()
            }

            R.id.btn_restore ->{

            }
        }
    }

    private fun connectBillingClient() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Billing client is ready, query available products
                    querySubscriptionProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Handle the case where the connection is lost
                connectBillingClient()
            }
        })
    }

    private fun querySubscriptionProducts() {
        val skuList = listOf("your_subscription_id") // Replace with your product ID
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)

        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    startSubscriptionPurchase(skuDetails)
                }
            }
        }
    }

    private fun startSubscriptionPurchase(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        billingClient.launchBillingFlow(this, flowParams)
    }



    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "User canceled the purchase", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Some other error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Grant the subscription, acknowledge the purchase
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Subscription has been granted, unlock features
                    }
                }
            }
        }
    }
}