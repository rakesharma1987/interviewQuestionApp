package com.example.interviewquestion.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
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
import com.android.billingclient.api.queryPurchasesAsync
import com.example.interviewquestion.R
import com.example.interviewquestion.adapters.BuyNowListviewAdapter
import com.example.interviewquestion.databinding.ActivityBuyNowBinding
import com.example.interviewquestion.util.MyPreferences

class BuyNowActivity : AppCompatActivity(), View.OnClickListener, PurchasesUpdatedListener {
    lateinit var binding: ActivityBuyNowBinding
    private lateinit var billingClient: BillingClient


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyNowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.iv.setBackgroundColor(0xFFFFFFF)
        val txt_list = arrayOf(resources.getString(R.string.txt_content), resources.getString(R.string.txt_content_1),
            resources.getString(R.string.txt_content_2), resources.getString(R.string.txt_content_3), resources.getString(R.string.txt_content_4))

        val adapter = BuyNowListviewAdapter(txt_list)
        binding.listView.adapter = adapter

        binding.btnBuyPremium.setOnClickListener(this)
        binding.btnRestore.setOnClickListener(this)

        // Initialize BillingClient
        billingClient = BillingClient.newBuilder(this)
            .setListener(this) // Purchase update listener
            .enablePendingPurchases() // Required for pending purchases
            .build()


    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_buy_premium -> {
                connectBillingClient()
            }

            R.id.btn_restore ->{
                billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP){billingResult, purchasesList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                        MyPreferences.savePurchaseValueToPref(true)

                        if (MyPreferences.isPurchased()) binding.btnBuyPremium.visibility = View.INVISIBLE
                        else binding.btnBuyPremium.visibility = View.VISIBLE
                    }else{
                        Toast.makeText(this, "Not applicable for Restore", Toast.LENGTH_SHORT).show()
                    }
                }
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
        val skuList = listOf("subscription_01.") // Replace with your product ID
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

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
                MyPreferences.savePurchaseValueToPref(true)

                binding.btnBuyPremium.text = resources.getString(R.string.txt_premium)
                binding.btnBuyPremium.isEnabled = false
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "User canceled the purchase", Toast.LENGTH_SHORT).show()
            MyPreferences.savePurchaseValueToPref(false)
        } else {
            Toast.makeText(this, "Some other error occurred", Toast.LENGTH_SHORT).show()
            MyPreferences.savePurchaseValueToPref(false)
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
//                        MyPreferences.savePurchaseValueToPref(true)
                        Log.d("BILLING", "handlePurchase: ${billingResult.debugMessage}")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (MyPreferences.isPurchased()){
            binding.btnBuyPremium.visibility = View.INVISIBLE
            binding.btnBuyPremium.text = resources.getString(R.string.txt_premium)
            binding.btnBuyPremium.isEnabled = false
        }
        else binding.btnBuyPremium.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}