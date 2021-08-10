package com.smart.drink_reminder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.smart.drink_reminder.Services.NetworkState

class WebViewActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var webView: WebView
    lateinit var webView_title: TextView
    lateinit var webView_pb: ProgressBar
    lateinit var mAdView: AdView
    lateinit var mPrefs: SharedPreferences
    val MyPREFERENCES = "DrinkWater"
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        mPrefs = getSharedPreferences(
            MyPREFERENCES, Context.MODE_PRIVATE
        )
        toolbar = findViewById(R.id.webView_toolbar)
        webView = findViewById(R.id.webView)
        webView_pb = findViewById(R.id.webView_pb)
        mAdView = findViewById(R.id.webAdsView)
        val appadscd: LinearLayout = findViewById(R.id.webview_ads)
        webView_pb.visibility=View.VISIBLE
        webView_title = findViewById(R.id.webView_title)
        webView_title.text=intent.getStringExtra("webTitle")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.loadUrl(intent.getStringExtra("webUrl")!!)
        if (!mPrefs.getBoolean("SmartDrinkINAPP", false)) {
            val networkState = NetworkState()
            if (networkState.isNetworkAvailable(this)) {
                val adRequest = AdRequest.Builder().build()
                mAdView.loadAd(adRequest)
                appadscd.visibility = View.VISIBLE
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                if (progress == 100) {
                    webView_pb.visibility=View.GONE
                }
            }
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this, MainActivity::class.java))
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }
            else -> return false
        }
    }
    override fun onPause() {
        if (mAdView!=null) {
            mAdView.pause();
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}