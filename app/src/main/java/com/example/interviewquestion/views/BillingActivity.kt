package com.example.interviewquestion.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.example.interviewquestion.R
import com.example.interviewquestion.adapters.ProductDetailAdapter
import com.example.interviewquestion.databinding.ActivityBillingBinding
import com.example.interviewquestion.interfaces.OnCLickProduct
import com.example.interviewquestion.util.MyPreferences
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule

// TODO: This activity is not in use.

class BillingActivity : AppCompatActivity(), OnCLickProduct {
    private var billingClient : BillingClient?= null
    private var productDetailList = mutableListOf<ProductDetails.SubscriptionOfferDetails>()
    var handler = Handler()
    private lateinit var billingActivityBinding: ActivityBillingBinding
    private var TAG = "testApi"
    private var onCLickProduct = this
    lateinit var productDetailAdapter: ProductDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_billing)
        supportActionBar!!.title = "Subscription"

        CoroutineScope(Dispatchers.IO).launch {
            billingConnection()
        }
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
            ImmutableList.of( //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("monthlysubscriptionantisecret") //monthlysubscrtiptionstopsmoking
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("yearlysubscriptionantisecret") //yearlysubscription
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),  //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("antisecretlistenin")
                    .setProductType(BillingClient.ProductType.SUBS)
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
                        prodDetailsList[0].subscriptionOfferDetails?.let {
                            productDetailList.addAll(it)
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(
                                this@BillingActivity,
                                "No product found.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Log.d(TAG, productDetailList.size.toString() + " number of products")
                    runOnUiThread(Runnable {
//                        Toast.makeText(this@BillingActivity, "ProductDetail List ${productDetailList.size.toString()}", Toast.LENGTH_SHORT).show()
                        billingActivityBinding.pbLoading.visibility = View.GONE
                        if (prodDetailsList.isNotEmpty()){
                            Log.d("ProductList", "showProducts: ${prodDetailsList[0]}")
                            productDetailAdapter = ProductDetailAdapter(
                                onCLickProduct,
                                this@BillingActivity,
                                prodDetailsList[0].subscriptionOfferDetails,
                                prodDetailsList[0]
                            )
                        }else{
                            return@Runnable
                        }
                        billingActivityBinding.rvSubscriptionList.setHasFixedSize(true)
                        billingActivityBinding.rvSubscriptionList.layoutManager =
                            LinearLayoutManager(
                                this@BillingActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        billingActivityBinding.rvSubscriptionList.adapter = productDetailAdapter
                    })
                }
            }catch (e: Exception){
                Log.d(TAG, "showProducts: ${e.stackTrace}")
            }
        }
    }

    fun launchPurchaseFlow(productDetails: ProductDetails, subscriptionOfferDetails: ProductDetails.SubscriptionOfferDetails) {
        assert(productDetails.subscriptionOfferDetails != null)
        val productDetailsParamsList = ImmutableList.of(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(subscriptionOfferDetails.offerToken)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        val billingResult = billingClient!!.launchBillingFlow(this, billingFlowParams)
    }

    fun verifySubPurchase(purchases: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams
            .newBuilder()
            .setPurchaseToken(purchases.purchaseToken)
            .build()
        billingClient!!.acknowledgePurchase(
            acknowledgePurchaseParams
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                MyPreferences.savePurchaseValueToPref(true)
                MyPreferences.saveFirstLaunchAfterPaid(true)
                this.finish()
            }
        }
        Log.d(TAG, "Purchase Token: " + purchases.purchaseToken)
        Log.d(TAG, "Purchase Time: " + purchases.purchaseTime)
        Log.d(TAG, "Purchase OrderID: " + purchases.orderId)
    }

    override fun onResume() {
        super.onResume()
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult: BillingResult, list: List<Purchase> ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in list) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        verifySubPurchase(purchase)
                    }
                }
            }
        }
    }

    override fun onClickItem(productDetails: ProductDetails, subscriptionOfferDetails: ProductDetails.SubscriptionOfferDetails) {
        launchPurchaseFlow(productDetails,subscriptionOfferDetails)
    }
}