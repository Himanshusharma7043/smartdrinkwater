package com.smart.drink_reminder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashsScreen : AppCompatActivity() {
    val MyPREFERENCES = "DrinkWater"
    lateinit var editor: SharedPreferences.Editor
    private val SPLASH_TIME_OUT:Long=1000 // 3 sec
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashs_screen)
        val mPrefs = getSharedPreferences(
            MyPREFERENCES, Context.MODE_PRIVATE)
        editor = mPrefs.edit()
        editor.putBoolean("start", true)
        editor.commit()
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            if (mPrefs.getBoolean("apply", false)){
                startActivity(Intent(this,MainActivity::class.java))
            }else{
                startActivity(Intent(this,Basicinfo::class.java))
            }


            // close this activity
            finish()
        }, SPLASH_TIME_OUT)
    }
}