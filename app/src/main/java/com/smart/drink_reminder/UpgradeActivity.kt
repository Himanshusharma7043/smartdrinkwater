package com.smart.drink_reminder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.smart.drink_reminder.Services.Security
import com.smart.drink_reminder.Slider.SliderAdapter
import com.smart.drink_reminder.Slider.SliderData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class UpgradeActivity : AppCompatActivity(), PurchasesUpdatedListener {
    val PREF_FILE = "DrinkWater"
    val PURCHASE_KEY = "SmartDrinkINAPP"
//    val PRODUCT_ID = "android.test.purchased"
    val PRODUCT_ID = "com_smart_drink_reminder_1_package"
    val base64Key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj6J3pQQy4UJmRVyz6GiCR4CWLPugHkIluXuCtoKNbcVmJONR3sxRhEwyjNbF3FC/7GS0VUm5kRl3OiWDMEVd7JfMXuoq1YUWcZJuHZw+vLgDlEiyeoqjb36ifd6b1+/3BduZ3dBAoOXDjpONnpO8vGNeRVTB/56SYsBLjqnl8WXQfF8eSU+mGeobw7sfzMHanYLP7w/xDKTJLAzVQ55As/qh5MzOpYDqJcRM8NSmqbPTFYhcAey4eYGC1VUhi9kkfanLWNFAJD0Z+VeXCydIXY0PIJpdtzsKlXeQAdhszgf4kO2KQwxP2HP+XqIq4zl5Mdp7/MvhRSvBDvofA2/K3QIDAQAB"
    private var billingClient: BillingClient?=null
    lateinit var purchaseButton: Button
    lateinit var productAmountTXT: TextView
    private var viewPager: ViewPager? = null
    private var sliderAdapter: SliderAdapter? = null
    val sliderDataArrayList: MutableList<SliderData> = ArrayList<SliderData>()
    var currentPage = 0
    val DELAY_MS: Long = 1000
    val PERIOD_MS: Long = 2000
    lateinit var editor: SharedPreferences.Editor
    lateinit var mPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)
        mPrefs = getSharedPreferences(
            PREF_FILE, Context.MODE_PRIVATE
        )
        val closeBTN: ImageView = findViewById(R.id.closeUpgrade)
        purchaseButton = findViewById(R.id.premiumBTN)
        productAmountTXT = findViewById(R.id.productAmountTXT)
        viewPager = findViewById(R.id.viewpager)
        closeBTN.setOnClickListener() {
            startActivity(Intent(this, MainActivity::class.java))
        }
        setConnection()
        purchaseButton.setOnClickListener() {
            if (billingClient!!.isReady) {
                Log.e("TAG", "billingClient:isReady ")
                initiatePurchase()
            }
            //else reconnect service
            else {
                Log.e("TAG", "billingClient:is not Ready ")
                billingClient =
                    BillingClient.newBuilder(this).enablePendingPurchases().setListener(this)
                        .build()
                billingClient!!.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingResponseCode.OK) {
                            initiatePurchase()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error " + billingResult.debugMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        Log.e("TAG", "onBillingServiceDisconnected: ")
                    }
                })
            }
        }
        sliderDataArrayList.add(
            SliderData(
                R.drawable.beermag,
                true, "", ContextCompat.getColor(this, R.color.Black)
            )
        )
        sliderDataArrayList.add(
            SliderData(
                R.drawable.beermag,
                false, "Beer",
                ContextCompat.getColor(this, R.color.Gold)
            )
        )
        sliderDataArrayList.add(
            SliderData(
                R.drawable.enerydrink,
                false, "Energy Drink",
                ContextCompat.getColor(this, R.color.Black)
            )
        )
        sliderDataArrayList.add(
            SliderData(
                R.drawable.lemonade,
                false, "Lemonade",
                ContextCompat.getColor(this, R.color.GreenYellow)
            )
        )

        sliderAdapter = SliderAdapter(this, sliderDataArrayList)
        viewPager!!.adapter = sliderAdapter
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == 5 - 1) {
                currentPage = 0
            }
            viewPager!!.setCurrentItem(currentPage++, true)
        }
        val timer: Timer
        timer = Timer() // This will create a new Thread
        timer.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)
    }
    private fun setConnection() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases()
            .setListener(this).build()
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    loadText()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error " + billingResult.debugMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e(
                    "TAG",
                    "onBillingServiceDisconnected: "
                )
            }
        })
    }
    private fun initiatePurchase() {
        Log.e("TAG", "initiatePurchase: ")
        val skuList: MutableList<String> = ArrayList()
        skuList.add(PRODUCT_ID)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(INAPP)
        billingClient!!.querySkuDetailsAsync(
            params.build())
        { billingResult, skuDetailsList ->
            if (billingResult.responseCode ==BillingResponseCode.OK) {
                if (skuDetailsList != null && skuDetailsList.isNotEmpty()) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList[0]!!)
                        .build()
                    Log.e("TAG", "onSkuDetailsResponse:Flow ")
                    billingClient!!.launchBillingFlow(this, flowParams)
                } else {
                    //try to add item/product id "purchase" inside managed product in google play console
                    Log.e(
                        "Tag",
                        "Purchase Item not Found: "
                    )
                    Log.e(
                        "Tag",
                        "skuDetailsList: " + skuDetailsList?.size
                    )
                }
            } else {
                Log.e("Error", ": " + billingResult.debugMessage)
            }
        }

    }
private fun loadText(){
    Log.e("TAG", "initiatePurchase: ")
    val skuList: MutableList<String> = ArrayList()
    skuList.add(PRODUCT_ID)
    val params = SkuDetailsParams.newBuilder()
    params.setSkusList(skuList).setType(INAPP)

    billingClient!!.querySkuDetailsAsync(
        params.build()
    ) { billingResult: BillingResult, skuDetailsList: List<SkuDetails>? ->
        if (billingResult.responseCode == BillingResponseCode.OK) {
            if (skuDetailsList != null && skuDetailsList.size > 0) {
               // textView.setText(skuDetailsList[0].price)
                Log.e("TAG", "Price: "+skuDetailsList[0].description )
                productAmountTXT.text=skuDetailsList[0].price
            } else {
                //try to add item/product id "purchase" inside managed product in google play console
                Log.e(
                    "TAG",
                    "Purchase Item not Found: "
                )
                Log.e("Error", ""+ skuDetailsList.toString())

            }
        } else {
            Log.e("Error", ": " + billingResult.debugMessage)
        }
    }
}
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase?>?) {
        Log.e("TAG", "onPurchasesUpdated: ")
        //if item newly purchased
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            Log.e("onPurchasesUpdated", "purchases: ")
            Log.e("onPurchasesUpdated", "onPurchasesUpdated: "+purchases.size )
            handlePurchases(purchases)
            Log.e("TAG", "onPurchasesUpdated: "+purchases.size )
        } else if (billingResult.responseCode == BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.e("onPurchasesUpdated", "onPurchasesUpdated: ITEM_ALREADY_OWNED")
            val queryAlreadyPurchasesResult = billingClient!!.queryPurchases(INAPP)
            val alreadyPurchases = queryAlreadyPurchasesResult.purchasesList
            alreadyPurchases?.let {
                handlePurchases(it)
            }
        } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "Purchase Canceled", Toast.LENGTH_SHORT).show()
            Log.e("onPurchasesUpdated", "Purchase Canceled: " )
        } else {
            Toast.makeText(
                this,
                "Error " + billingResult.debugMessage,
                Toast.LENGTH_SHORT
            ).show()
            Log.e("onPurchasesUpdated", "Error: "+billingResult.debugMessage )
        }
    }

    fun handlePurchases(purchases: List<Purchase?>?) {
        Log.e("TAG", "handlePurchases: ")
        for (purchase in purchases!!) {
            //if item is purchased

            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase!!.purchaseToken)
                .build()
            Log.e("TAG", "purchase.purchaseState: "+purchase.purchaseState )
            Log.e("handlePurchases", "purchase.skus: "+purchase.skus[0].toString() )
            Log.e("handlePurchases", "purchases.size: "+purchases.size )
            Log.e("handlePurchases", "purchase.orderId: "+purchase.orderId )
            Log.e("handlePurchases", "purchase in purchases: ")
            Log.e("handlePurchases", "product condition: "+("android.test.purchased" === purchase.skus[0].toString() ))
            Log.e("handlePurchases", "product state: "+(purchase.purchaseState == Purchase.PurchaseState.PURCHASED ))
            Log.e("TAG", "condition: "+(PRODUCT_ID == purchase.skus[0].toString() && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) )
            if (PRODUCT_ID ==purchase.skus[0].toString() && purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
                Log.e("handlePurchases", "PURCHASED: ")
                if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
                    Log.e("handlePurchases", "purchase.signature:false ")
                    Log.e("handlePurchases", "Invalid Purchase: ")
//                    return
                }
                else{
                    Log.e("handlePurchases", "purchase.signature: true")
                }
                // else purchase is valid
                //if item is purchased and not acknowledged
                if (!purchase.isAcknowledged) {
                    Log.e("TAG", "isAcknowledged:false ", )
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient!!.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase)
                    Toast.makeText(this, "Item Purchased", Toast.LENGTH_SHORT)
                        .show()
//                    recreate()
                    Log.e("handlePurchases", "if item is purchased and not acknowledged: ")
                } else {
                    // Grant entitlement to the user on item purchase
                    // restart activity
                    Log.e("TAG", "isAcknowledged:true ", )
                    if (!getPurchaseValueFromPref()) {
                        savePurchaseValueToPref(true)
                        Toast.makeText(this, "Item Purchased", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("handlePurchases", "Item Purchased ")
                        recreate()
                    }
                }
            } else if (PRODUCT_ID.equals(purchase.skus) && purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Toast.makeText(
                    this,
                    "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT
                ).show()
                Log.e("handlePurchases", "Purchase is Pending. Please complete Transaction: ")
            } else if (PRODUCT_ID.equals(purchase.skus) && purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                savePurchaseValueToPref(false)
                // purchaseStatus.setText("Purchase Status : Not Purchased")
                Log.e("handlePurchases", "Purchase Status : Not Purchased")
                purchaseButton.visibility = View.VISIBLE
                Toast.makeText(this, "Purchase Status Unknown", Toast.LENGTH_SHORT)
                    .show()
            }else{
                Log.e("TAG", "Something went wrong " )
            }
        }
    }



    var ackPurchase =
        AcknowledgePurchaseResponseListener { billingResult ->
            if (billingResult.responseCode == BillingResponseCode.OK) {

                //if purchase is acknowledged
                // Grant entitlement to the user. and restart activity
                savePurchaseValueToPref(true)
                Toast.makeText(this, "Item Purchased", Toast.LENGTH_SHORT).show()
                Log.e("ackPurchase", "Item Purchased" )
//                this.recreate()
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
        Log.e("TAG", "savePurchaseValueToPref: " )
        getPreferenceEditObject().putBoolean(PURCHASE_KEY, value).commit()
        getPreferenceEditObject().putBoolean("PURCHASE", value).commit()
        getPreferenceEditObject().putInt("drinkPicker", 0)

    }
}