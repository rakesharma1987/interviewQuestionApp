package com.example.interviewquestion.interfaces

import com.android.billingclient.api.ProductDetails

interface OnCLickProduct {
    fun onClickItem (productDetails: ProductDetails, subscriptionOfferDetails: ProductDetails.SubscriptionOfferDetails)
}