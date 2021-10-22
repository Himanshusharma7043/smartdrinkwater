package com.smart.drink_reminder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashsScreen : AppCompatActivity() {
    private val mPref = "DrinkWater"
    lateinit var editor: SharedPreferences.Editor
    private val splashTimeOut:Long=1000
    var mPrefs:SharedPreferences?=null
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashs_screen)
         mPrefs = getSharedPreferences(
            mPref, Context.MODE_PRIVATE)

        Handler(Looper.getMainLooper()).postDelayed({
            if(mPrefs!!.getBoolean("apply", false)){
                startActivity(Intent(this,MainActivity::class.java))
            }else{
                startActivity(Intent(this,Basicinfo::class.java))
            }
            finish()
        }, splashTimeOut)
        putBooleanShareP("addCupAds",true)
        putBooleanShareP("intervalAds",true)
        if (!mPrefs!!.getBoolean("start",false)){
            putBooleanShareP("start",true)
        }
    }
    @SuppressLint("CommitPrefEdits")
    private fun putBooleanShareP(name: String, value: Boolean) {
        editor = mPrefs!!.edit()
        editor.putBoolean(name, true)
        editor.commit()
    }





}