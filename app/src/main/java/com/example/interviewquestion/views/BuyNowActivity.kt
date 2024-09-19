package com.example.interviewquestion.views

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.queryPurchasesAsync
import com.example.interviewquestion.R
import com.example.interviewquestion.adapters.BuyNowListviewAdapter
import com.example.interviewquestion.databinding.ActivityBuyNowBinding
import com.example.interviewquestion.util.MyPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule

class BuyNowActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityBuyNowBinding
    private lateinit var billingClient: BillingClient
    val TAG = "_tag"
    private var productDetailList = mutableListOf<ProductDetails.SubscriptionOfferDetails>()
    private lateinit var productDetails: ProductDetails
    private lateinit var subscriptionOfferDetails: ProductDetails.SubscriptionOfferDetails
    private lateinit var dialog: ProgressDialog


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyNowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.tvAmount.text = buildString {}

        dialog = ProgressDialog(this)
        dialog.setCancelable(false)
        dialog.setMessage("Fetching product details...")
        dialog.show()


        binding.iv.setBackgroundColor(0xFFFFFFF)
        val txt_list = arrayOf(resources.getString(R.string.txt_content), resources.getString(R.string.txt_content_1),
            resources.getString(R.string.txt_content_2), resources.getString(R.string.txt_content_3), resources.getString(R.string.txt_content_4))

        val adapter = BuyNowListviewAdapter(txt_list)
        binding.listView.adapter = adapter

        binding.btnBuyPremium.setOnClickListener(this)
        binding.btnRestore.setOnClickListener(this)

        billingConnection()
        if (MyPreferences.isPurchased()){
            binding.btnBuyPremium.text = getString(R.string.txt_premium)
            binding.btnBuyPremium.isEnabled = false
        }

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_buy_premium -> {
                launchPurchaseFlow(productDetails)
            }

            R.id.btn_restore -> {
                if (MyPreferences.isRestored()){
                    Toast.makeText(this, "Already restored.", Toast.LENGTH_SHORT).show()
                    return
                }
                billingClient.queryPurchasesAsync(BillingClient.ProductType.INAPP) { billingResult, purchases ->
                    if (/*billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED*/ purchases[0].purchaseToken.isNotEmpty()) {
                        MyPreferences.savePurchaseValueToPref(true)
                        MyPreferences.saveDeleteAndRestoredValue(true)
                        MyPreferences.setFreeVersion(false)
                        MyPreferences.setRestoreValue(true)
                        binding.btnBuyPremium.text = getString(R.string.txt_premium)
                        binding.btnBuyPremium.isEnabled = false
                        Log.d("Billing", "Restore purchases: ${billingResult.debugMessage}")
                        Snackbar.make(v, "Restore purchases", Snackbar.LENGTH_LONG).setAction("Restart", View.OnClickListener {
                            Toast.makeText(this, "Please restart app.", Toast.LENGTH_SHORT).show()
                        }).show()

                    } else {
                        Log.d("Billing", "Failed to restore purchases ${billingResult.debugMessage}")
                        Snackbar.make(v, "Failed to restore", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun billingConnection(){
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener { billingResult, list ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                    for (purchase in list) {
                        verifySubPurchase(purchase)
                    }
                }
            }.build()

        establishConnection()
    }

    fun establishConnection() {
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                establishConnection()
            }
        })
    }

    fun showProducts() {
        val productList: ImmutableList<QueryProductDetailsParams.Product> =
            ImmutableList.of(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("subscription_01.")
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient!!.queryProductDetailsAsync(params) {
                billingResult: BillingResult?, prodDetailsList: List<ProductDetails> ->
            productDetailList.clear()

            try {
                Timer().schedule(2000) {
                    Log.d(TAG, "posted delayed")

                    if(prodDetailsList.isNotEmpty()) {
//                        subscriptionOfferDetails = prodDetailsList[0].subscriptionOfferDetails
                        productDetails = prodDetailsList[0]
                        runOnUiThread {
                            dialog.dismiss()
                            binding.tvAmount.text = buildString {
                                append(prodDetailsList[0].oneTimePurchaseOfferDetails!!.formattedPrice)
                                append("/lifetime")
                            }
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@BuyNowActivity, "No product found.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }catch (e: Exception){
                Log.d(TAG, "showProducts: ${e.stackTrace}")
            }
        }
    }

    private fun launchPurchaseFlow(productDetails: ProductDetails/*, subscriptionOfferDetails: ProductDetails.SubscriptionOfferDetails*/) {
//        assert(productDetails.subscriptionOfferDetails != null)
        val productDetailsParamsList = ImmutableList.of(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
//                .setOfferToken(subscriptionOfferDetails.offerToken)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(this, billingFlowParams)
        if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            MyPreferences.savePurchaseValueToPref(true)
        }
    }

   private fun verifySubPurchase(purchases: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams
            .newBuilder()
            .setPurchaseToken(purchases.purchaseToken)
            .build()
        billingClient!!.acknowledgePurchase(
            acknowledgePurchaseParams
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                MyPreferences.savePurchaseValueToPref(true)
                MyPreferences.saveDeleteAndRestoredValue(true)
                MyPreferences.setFreeVersion(false)
                Snackbar.make(binding.listView, "Please restart app.", Snackbar.LENGTH_LONG).setAction("Restart", View.OnClickListener {
                    Toast.makeText(this, "Please restart app.", Toast.LENGTH_SHORT).show()
                }).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        ) { billingResult: BillingResult, list: List<Purchase> ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in list) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        verifySubPurchase(purchase)
                    }
                }
            }else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
                MyPreferences.savePurchaseValueToPref(true)
            }
        }
    }

//    private fun queryExistingPurchases() {
//        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
//            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                if (purchases.isNullOrEmpty()) {
//                    // No purchases found
//                    Toast.makeText(applicationContext, "No previous purchases", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "Purchased: ", Toast.LENGTH_SHORT).show()
//
//                    // Purchases found
////                    for (purchase in purchases) {
////                        Toast.makeText(applicationContext, "Purchased: ${purchase.packageName}", Toast.LENGTH_SHORT).show()
////                    }
//                }
//            } else {
//                // Query failed
//                Toast.makeText(applicationContext, "Failed to query purchases: ${billingResult.debugMessage}", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

}