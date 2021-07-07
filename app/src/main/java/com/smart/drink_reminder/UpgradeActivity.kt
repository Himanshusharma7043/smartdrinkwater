package com.smart.drink_reminder

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import java.io.IOException
import java.util.*

class UpgradeActivity : AppCompatActivity(), PurchasesUpdatedListener {
    val PREF_FILE = "SmartDrinkReminder"
    val PURCHASE_KEY = "purchase_key"
    val PRODUCT_ID = "purchase_id"
    val base64Key = "base64key"
    private var billingClient: BillingClient? = null
    lateinit var purchaseButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)
        val closeBTN: ImageView = findViewById(R.id.closeUpgrade)
        purchaseButton = findViewById(R.id.premiumBTN)
        closeBTN.setOnClickListener() {
            startActivity(Intent(this, MainActivity::class.java))
        }
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases().setListener(this).build()

        if (billingClient!!.isReady) {
            Log.e("TAG", "onCreate: billingClient isReady")
            initiatePurchase(false)
        }else{
            Log.e("TAG", "onCreate: billingClient is not Ready")
        }
        purchaseButton.setOnClickListener() {
            //check if service is already connected
            //check if service is already connected
            if (billingClient!!.isReady) {
                initiatePurchase(true)
            }
            //else reconnect service
            else {
                billingClient =
                    BillingClient.newBuilder(this).enablePendingPurchases().setListener(this)
                        .build()
                billingClient!!.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            initiatePurchase(true)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error " + billingResult.debugMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    override fun onBillingServiceDisconnected() {
                        Log.e("TAG", "onBillingServiceDisconnected: " )
                    }
                })
            }
        }

    }

    private fun initiatePurchase(check:Boolean) {
        val skuList: MutableList<String> = ArrayList()
        skuList.add(PRODUCT_ID)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(SkuType.INAPP)
        billingClient!!.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (skuDetailsList != null && skuDetailsList.size > 0) {
                    if (check){
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList[0])
                            .build()
                        billingClient!!.launchBillingFlow(this, flowParams)
                    }else{
                        Log.e("TAG", "Product Price:${skuDetailsList[0].price} ")
                    }
                } else {
                    //try to add item/product id "purchase" inside managed product in google play console
                    Toast.makeText(
                        applicationContext,
                        "Purchase Item not Found",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    " Error " + billingResult.debugMessage, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase?>?) {
        //if item newly purchased
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            val queryAlreadyPurchasesResult = billingClient!!.queryPurchases(SkuType.INAPP)
            val alreadyPurchases = queryAlreadyPurchasesResult.purchasesList

            alreadyPurchases?.let {
                handlePurchases(it)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(applicationContext, "Purchase Canceled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                applicationContext,
                "Error " + billingResult.debugMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun handlePurchases(purchases: List<Purchase?>?) {
        for (purchase in purchases!!) {
            //if item is purchased
            if (PRODUCT_ID.equals(purchase?.skus) && purchase?.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(
                        applicationContext,
                        "Error : Invalid Purchase",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                // else purchase is valid
                //if item is purchased and not acknowledged
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient!!.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase)
                } else {
                    // Grant entitlement to the user on item purchase
                    // restart activity
                    if (!getPurchaseValueFromPref()) {
                        savePurchaseValueToPref(true)
                        Toast.makeText(applicationContext, "Item Purchased", Toast.LENGTH_SHORT)
                            .show()
                        recreate()
                    }
                }
            } else if (PRODUCT_ID.equals(purchase?.skus) && purchase?.purchaseState == Purchase.PurchaseState.PENDING) {
                Toast.makeText(
                    applicationContext,
                    "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT
                ).show()
            } else if (PRODUCT_ID.equals(purchase?.skus) && purchase?.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                savePurchaseValueToPref(false)
                // purchaseStatus.setText("Purchase Status : Not Purchased")
                Log.e("TAG", "Purchase Status : Not Purchased")
                purchaseButton.visibility = View.VISIBLE
                Toast.makeText(applicationContext, "Purchase Status Unknown", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    var ackPurchase =
        AcknowledgePurchaseResponseListener { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                //if purchase is acknowledged
                // Grant entitlement to the user. and restart activity
                savePurchaseValueToPref(true)
                Toast.makeText(applicationContext, "Item Purchased", Toast.LENGTH_SHORT).show()
                this.recreate()
            }
        }

    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            Security.verifyPurchase(base64Key, signedData, signature)
        } catch (e: IOException) {
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (billingClient != null) {
            billingClient!!.endConnection()
        }
    }

    private fun getPreferenceObject(): SharedPreferences {
        return applicationContext.getSharedPreferences(PREF_FILE, 0)
    }

    private fun getPreferenceEditObject(): SharedPreferences.Editor {
        val pref = applicationContext.getSharedPreferences(PREF_FILE, 0)
        return pref.edit()
    }

    private fun getPurchaseValueFromPref(): Boolean {
        return getPreferenceObject().getBoolean(PURCHASE_KEY, false)
    }

    private fun savePurchaseValueToPref(value: Boolean) {
        getPreferenceEditObject().putBoolean(PURCHASE_KEY, value).commit()
        getPreferenceEditObject().putBoolean("PURCHASE", value).commit()
    }
}