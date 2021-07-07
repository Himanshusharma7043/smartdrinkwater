package com.smart.drink_reminder

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.github.florent37.viewtooltip.ViewTooltip
import com.github.florent37.viewtooltip.ViewTooltip.TooltipView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.smart.drink_reminder.Database.DatabaseHandler
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Achievements : AppCompatActivity(), View.OnClickListener {
    lateinit var toolbar: Toolbar
    lateinit var mAdView: AdView
    lateinit var strike: LinearLayout
    lateinit var cupmaker: LinearLayout
    lateinit var share: LinearLayout
    lateinit var highfive: LinearLayout
    lateinit var monthlyboss: LinearLayout
    lateinit var devotion: LinearLayout
    lateinit var strikeimg: CircleImageView
    lateinit var shareIMG: CircleImageView
    lateinit var cupMakerIMG: CircleImageView
    lateinit var highFiveIMG: CircleImageView
    lateinit var devotionIMG: CircleImageView
    lateinit var monthlyBossIMG: CircleImageView
    lateinit var chainMasterIMG: CircleImageView
    lateinit var waterLordIMG: CircleImageView
    lateinit var aquaHeroIMG: CircleImageView
    lateinit var chainmaster: LinearLayout
    lateinit var waterlord: LinearLayout
    lateinit var gridLayout: GridLayout
    lateinit var aquahero: LinearLayout
    lateinit var mPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var DB: DatabaseHandler
    lateinit var goalAchieveTEXT:TextView
    lateinit var totalGoalTEXT:TextView
    lateinit var progressbar:ProgressBar
    @SuppressLint("SimpleDateFormat")
    val sdfdate = SimpleDateFormat("yyyy-MM-dd")
    val MyPREFERENCES = "DrinkWater"
    var mCustomFont: Typeface? = null
    @SuppressLint("NewApi", "UseCompatLoadingForDrawables", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPrefs = getSharedPreferences(
            MyPREFERENCES, Context.MODE_PRIVATE
        )
        val getTheme =
            mPrefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(getTheme)
        setContentView(R.layout.activity_achievements)
        DB = DatabaseHandler(this)
        strike = findViewById(R.id.strike)
        cupmaker = findViewById(R.id.cupmaker)
        share = findViewById(R.id.sharell)
        highfive = findViewById(R.id.highfive)
        gridLayout = findViewById(R.id.gridLayout)
        monthlyboss = findViewById(R.id.monthlyboss)
        devotion = findViewById(R.id.devotion)
        chainmaster = findViewById(R.id.chainmaster)
        waterlord = findViewById(R.id.waterlord)
        aquahero = findViewById(R.id.aquahero)
        toolbar = findViewById(R.id.achievement_toolbar)
        strikeimg = findViewById(R.id.strikeimg)
        shareIMG = findViewById(R.id.shareIMG)
        cupMakerIMG = findViewById(R.id.cupMakerIMG)
        highFiveIMG = findViewById(R.id.highFiveIMG)
        devotionIMG = findViewById(R.id.devotionIMG)
        chainMasterIMG = findViewById(R.id.chainMasterIMG)
        waterLordIMG = findViewById(R.id.waterLordIMG)
        aquaHeroIMG = findViewById(R.id.aquaHeroIMG)
        monthlyBossIMG = findViewById(R.id.monthlyBossIMG)
        progressbar=findViewById(R.id.achievementProgressBar)
        goalAchieveTEXT=findViewById(R.id.goalAchieve)
        totalGoalTEXT=findViewById(R.id.achievementTotalGoal)
        progressbar.progress=20
        val appadscd: CardView = findViewById(R.id.appadscd)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mAdView = findViewById(R.id.madView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        val setprogress=mPrefs.getInt("achievementPB",0)

        ObjectAnimator.ofInt(progressbar, "progress", setprogress)
            .setDuration(500)
            .start()
        goalAchieveTEXT.text=mPrefs.getInt("goalAchieve",0).toString()
        totalGoalTEXT.text=mPrefs.getInt("achievementTotalGoal",5000).toString()
        Handler().postDelayed({
            appadscd.visibility = View.VISIBLE
        }, 3000)
        ObjectAnimator.ofInt(progressbar, "progress", mPrefs.getInt("achievementPB",0))
            .setDuration(500)
            .start()
        checkImageVis(shareIMG,"share")
        checkImageVis(strikeimg,"achivedailygoal")
        checkImageVis(cupMakerIMG,"cupaddedtask")
        checkImageVis(highFiveIMG,"FivedaysrowTask")
        checkImageVis(monthlyBossIMG,"monthlyBossTask")
        checkImageVis(devotionIMG,"fourDaysCupTask")
        checkImageVis(waterLordIMG,"ninetyDaysAverageTask")
        checkImageVis(aquaHeroIMG,"sixDayscupCountTask")
        checkImageVis(chainMasterIMG,"sevendaysrowTask")
//        checkAchievements()
        strike.setOnClickListener(this)
        cupmaker.setOnClickListener(this)
        share.setOnClickListener(this)
        highfive.setOnClickListener(this)
        monthlyboss.setOnClickListener(this)
        devotion.setOnClickListener(this)
        chainmaster.setOnClickListener(this)
        waterlord.setOnClickListener(this)
        aquahero.setOnClickListener(this)
    }

    private fun checkImageVis(img: CircleImageView?, name: String) {
        if (mPrefs.getBoolean(name, false)) {
            img!!.alpha = 0.9f
            img.borderColor=Color.GREEN
            img.borderWidth=4
        } else {
            img!!.alpha = 0.2f
            img.borderColor=Color.RED
            img.borderWidth=4
        }
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
    @SuppressLint("SimpleDateFormat")

    fun convertStringToArray(str: String): ArrayList<String> {
        val strSeparator = ","
        val words = ArrayList<String>()
        for (w in str.trim(' ').split(strSeparator)) {
            if (w.isNotEmpty()) {
                words.add(w)
            }
        }
        return words
    }
    @SuppressLint("SimpleDateFormat")

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.strike -> {
                setTooltip(strike, "Achieve Daily goal")//complete
            }
            R.id.cupmaker -> {
                setTooltip(cupmaker, "Make Three Custom drinking cups")
            }
            R.id.sharell -> {
                setTooltip(share, "Share App")//complete
            }
            R.id.highfive -> {
                setTooltip(highfive, "Five Successfully days in row")//complete
            }
            R.id.monthlyboss -> {
                setTooltip(monthlyboss, "Monthly average above 2150ml")//complete
            }
            R.id.devotion -> {
                setTooltip(devotion, "Drink 4 and more cups for 30 Days")//complete
            }
            R.id.chainmaster -> {
                setTooltip(chainmaster, "5 Times 10 Successfully days in a row")
            }
            R.id.waterlord -> {
                setTooltip(waterlord, "90 Days average above 2150ml")//complete
            }
            R.id.aquahero -> {
                setTooltip(aquahero, "Drink 6 or more cups a days for 90 days")//complete
            }
        }
    }

    private fun setTooltip(layout: LinearLayout, name: String) {
        val viewTooltip: TooltipView = ViewTooltip
            .on(layout)
            .color(Color.BLUE)
            .textColor(Color.WHITE)
            .autoHide(true, 1000)
            .distanceWithView(0)
            .arrowHeight(15)
            .arrowWidth(15)
            .padding(20, 20, 20, 20)
            .position(ViewTooltip.Position.BOTTOM)
            .text(name)
            .show()
    }

}