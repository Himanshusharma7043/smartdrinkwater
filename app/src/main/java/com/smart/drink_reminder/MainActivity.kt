package com.smart.drink_reminder

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.plattysoft.leonids.ParticleSystem
import com.shawnlin.numberpicker.NumberPicker
import com.smart.drink_reminder.AddCupRecyclerView.CupAdapter
import com.smart.drink_reminder.Database.DatabaseHandler
import com.smart.drink_reminder.LogRecyclerView.LogAdapter
import com.smart.drink_reminder.LogRecyclerView.LogData
import com.smart.drink_reminder.Services.NetworkState
import com.smart.drink_reminder.Services.NotificationReceiver
import com.smart.drink_reminder.Services.PermanentNotification
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n", "CommitPrefEdits")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    private var progrvalue: Float = 0f
    lateinit var mid_button: Button
    lateinit var mAdView: AdView
    lateinit var edit_reminder_time: ImageView
    lateinit var drinkPicker: NumberPicker
    lateinit var plusWater: ImageButton
    lateinit var minusWater: ImageButton
    lateinit var drinkSize: TextView
    lateinit var remindStatus: TextView
    lateinit var reminder_Text: TextView
    lateinit var levelTEXT: TextView
    lateinit var remindTime: TextView
    lateinit var addNew: ImageView
    lateinit var reminderLogo: ImageView
    lateinit var upgradeBTN: ConstraintLayout
    lateinit var reminderCardView: CardView
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var logAdapter: LogAdapter
    lateinit var recyclerView: RecyclerView
    val MyPREFERENCES = "DrinkWater"
    lateinit var DB: DatabaseHandler
    var totaldrink: Int = 0
    @SuppressLint("SimpleDateFormat")
    val sdfdate = SimpleDateFormat("yyyy-MM-dd")
    val getdate: String = sdfdate.format(Date())
    val date: Date = sdfdate.parse(getdate)!!
    lateinit var mPrefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var physicalActivity: ImageButton
    lateinit var weather: ImageButton
    private var loglist = ArrayList<String>()
    private var timelist = ArrayList<String>()
    private var drawablelist = ArrayList<String>()
    var oldlist = ArrayList<String>()
    lateinit var progress_bar: ProgressBar
    lateinit var waterIndicator: ConstraintLayout
    lateinit var waterDrinked: TextView
    lateinit var waterinput: TextView
    lateinit var waterMeasures: TextView
    lateinit var slash: TextView
    lateinit var cardView: CardView
    lateinit var navigationView: NavigationView
    lateinit var getgoaltype: String
    lateinit var getdrinktype: String
    var getdrinkimg: Int = 0
    var getdrinktextcolor: Int = 0
    var getlastdate: Long = 0
    // var getinput: Int = 0
    var strSeparator = ","
    var getval = 1
    val drinkArray = arrayOf(
        "Water",
        "Coffee",
        "Milk",
        "Tea",
        "Soda",
        "Fruit Juice",
        "Beer",
        "Energy Drink",
        "Lemonade"
    )
    val midicon = intArrayOf(
        R.drawable.water_glass,
        R.drawable.coffee,
        R.drawable.milk,
        R.drawable.tea,
        R.drawable.sodecan,
        R.drawable.fruitjuice,
        R.drawable.beermag,
        R.drawable.enerydrink,
        R.drawable.lemonade
    )
    @SuppressLint(
        "ResourceAsColor", "SetTextI18n", "CommitPrefEdits",
        "UseCompatLoadingForDrawables", "SdCardPath"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPrefs = getSharedPreferences(
            MyPREFERENCES, Context.MODE_PRIVATE
        )
        val getTheme =
            mPrefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(getTheme)
        getval = mPrefs.getInt("getval", 1)
        Log.e("TAG", "In-app product: " + mPrefs.getBoolean("SmartDrinkINAPP", false))
        getdrinktype = mPrefs.getString("drinktype", "Water")!!
        getdrinkimg = mPrefs.getInt("drinkimg", R.drawable.water_glass)
        getdrinktextcolor = mPrefs.getInt("drinktextcolour", R.color.Blue)
        getgoaltype = mPrefs.getString("goaltype", "ml")!!
        getlastdate = mPrefs.getLong("lastdate", date.time)
        val getdata = mPrefs.getString("watervalues", "100ml,200ml, 300ml")
        oldlist = convertStringToArray(getdata.toString())
        if (date.time == getlastdate) {
            progrvalue = mPrefs.getFloat("progress", 0f)
            totaldrink = mPrefs.getInt("totaldrink", 0)
            Log.e("TAG", "same day: ")
        } else {
            progrvalue = 0f
            totaldrink = 0
            editor = mPrefs.edit()
            editor.putLong("lastdate", date.time)
            editor.putInt("totaldrink", 0)
            editor.commit()
            putBooleanShareP("oneTime", true)
            Log.e("TAG", "new day: ")
        }
        val getlag = mPrefs.getString("language", "en")
        setLocale(R.layout.activity_main, getlag)
        setContentView(R.layout.activity_main)
        editor = mPrefs.edit()
        editor.putBoolean("apply", true)
        editor.commit()
        DB = DatabaseHandler(this)
        toolbar = findViewById(R.id.toolbar)
        upgradeBTN = findViewById(R.id.upgradeBTN)
        setSupportActionBar(toolbar)
        recyclerView = findViewById(R.id.log_rv)
        cardView = findViewById(R.id.card_view_todays_log)
        physicalActivity = findViewById(R.id.human)
        drinkPicker = findViewById(R.id.drinkPicker)
        plusWater = findViewById(R.id.plusWater)
        minusWater = findViewById(R.id.minusWater)
        drinkSize = findViewById(R.id.drinkSize)
        drinkSize.text = mPrefs.getString("mid_button_text", "100ml")!!
        edit_reminder_time = findViewById(R.id.edit_reminder_time)
        weather = findViewById(R.id.weather)
        drawerLayout = findViewById(R.id.drawer_layout)
        mid_button = findViewById(R.id.mid_button)
        mid_button.text = mPrefs.getString("mid_button_text", "100ml")!!
        waterDrinked = findViewById(R.id.waterDrinked)
        waterinput = findViewById(R.id.waterinput)
        waterinput.text = mPrefs.getInt("dailygoal", 3000).toString()
        waterMeasures = findViewById(R.id.waterMeasures)
        waterIndicator = findViewById(R.id.waterindicator)
        slash = findViewById(R.id.slash)
        navigationView = findViewById(R.id.navigation_view_drawer)
        addNew = findViewById(R.id.addNew)
        progress_bar = findViewById(R.id.progress_bar)
        reminderLogo = findViewById(R.id.reminder_logo)
        reminderCardView = findViewById(R.id.main_activity_reminder)
        remindStatus = findViewById(R.id.next_reminder_text)
        reminder_Text = findViewById(R.id.reminder_Text)
        remindTime = findViewById(R.id.next_reminder)
        levelTEXT = findViewById(R.id.level)
        levelTEXT.text = mPrefs.getString("level", "Level 1")
        if(mPrefs.getBoolean("SmartDrinkINAPP",false)){
            upgradeBTN.visibility=View.GONE
        }else{
            upgradeBTN.visibility=View.VISIBLE
        }
        upgradeBTN.setOnClickListener() {
            startActivity(
                Intent(
                    this,
                    UpgradeActivity::class.java
                )
            )
        }
        if (mPrefs.getBoolean("reminderSwitch", true)) {
            remindStatus.text = getString(R.string.next_reminder)
            remindStatus.setTextColor(ContextCompat.getColor(this, R.color.white))
            reminder_Text.setTextColor(ContextCompat.getColor(this, R.color.white))
            remindTime.visibility = View.VISIBLE
            reminderLogo.setImageResource(R.drawable.clock)
            edit_reminder_time.setColorFilter(
                ContextCompat.getColor(this, R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            edit_reminder_time

        } else {
            remindStatus.text = getString(R.string.reminder_off)
            remindStatus.setTextColor(ContextCompat.getColor(this, R.color.lightWhite))
            reminder_Text.setTextColor(ContextCompat.getColor(this, R.color.lightWhite))
            remindTime.visibility = View.INVISIBLE
            reminderLogo.setImageResource(R.drawable.noremind)
            edit_reminder_time.setColorFilter(
                ContextCompat.getColor(this, R.color.lightWhite),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        drawerLayout.setStatusBarBackgroundColor(R.color.white)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.White)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener(this)
        navigationView.menu.findItem(R.id.space).isEnabled = false
        physicalActivity.setImageResource(mPrefs.getInt("pcselectedDrawable", R.drawable.human))
        weather.setImageResource(mPrefs.getInt("weatherselectedDrawable", R.drawable.cloudy))
        physicalActivity.setOnClickListener() {
            var pcselectedDrawable = mPrefs.getInt("pcselectedDrawable", R.drawable.human)
            var pctext1 = mPrefs.getInt("pctext1", R.id.sedentary1)
            var pctext2 = mPrefs.getInt("pctext2", R.id.sedentary2)
            var pcimg = mPrefs.getInt("pcimg", R.id.sedentaryimg)
            var pcadded = mPrefs.getInt("pcadded", 0)
            var pcselectedString = 0
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.Physical_activity)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.physicalactivity, null)
            val veryActiveCL: ConstraintLayout = inflate.findViewById(R.id.veryActiveCL)
            val moderateActiveCL: ConstraintLayout = inflate.findViewById(R.id.moderateActiveCL)
            val lightlyActiveCL: ConstraintLayout = inflate.findViewById(R.id.lightlyActiveCL)
            val sedentaryCL: ConstraintLayout = inflate.findViewById(R.id.sedentaryCL)
            val very_activeimg: ImageView = inflate.findViewById(R.id.very_activeimg)
            val moderateactiveimg: ImageView = inflate.findViewById(R.id.moderateactiveimg)
            val lightlyactiveimg: ImageView = inflate.findViewById(R.id.lightlyactiveimg)
            val sedentaryimg: ImageView = inflate.findViewById(R.id.sedentaryimg)
            val very_activetext1: TextView = inflate.findViewById(R.id.very_activetext1)
            val very_activetext2: TextView = inflate.findViewById(R.id.very_activetext2)
            val moderate_active1: TextView = inflate.findViewById(R.id.moderate_active1)
            val moderate_active2: TextView = inflate.findViewById(R.id.moderate_active2)
            val lightlyactive1: TextView = inflate.findViewById(R.id.lightlyactive1)
            val lightlyactive2: TextView = inflate.findViewById(R.id.lightlyactive2)
            val sedentary1: TextView = inflate.findViewById(R.id.sedentary1)
            val sedentary2: TextView = inflate.findViewById(R.id.sedentary2)
            very_activetext2.text = "+${mPrefs.getInt("veryActive", 650)}" + " ml"
            moderate_active2.text = "+${mPrefs.getInt("mediumActive", 450)}" + " ml"
            lightlyactive2.text = "+${mPrefs.getInt("littleActive", 220)}" + " ml"

            when (pctext1) {
                very_activetext1.id -> {
                    preselected(very_activetext1, very_activetext2, very_activeimg)
                }
                moderate_active1.id -> {
                    preselected(moderate_active1, moderate_active2, moderateactiveimg)
                }
                lightlyactive1.id -> {
                    preselected(lightlyactive1, lightlyactive2, lightlyactiveimg)
                }
                sedentary1.id -> {
                    preselected(sedentary1, sedentary2, sedentaryimg)
                }
            }
            veryActiveCL.setOnClickListener() {
                setselected(very_activeimg, sedentaryimg, moderateactiveimg, lightlyactiveimg)
                setselectedtext(
                    very_activetext1,
                    moderate_active1,
                    lightlyactive1,
                    sedentary1,
                    very_activetext2,
                    moderate_active2,
                    lightlyactive2,
                    sedentary2
                )
                pcselectedDrawable = R.drawable.gym
                pctext1 = very_activetext1.id
                pctext2 = very_activetext2.id
                pcimg = very_activeimg.id
                pcadded = mPrefs.getInt("veryActive", 650)

                pcselectedString = R.string.very_active
            }
            moderateActiveCL.setOnClickListener() {
                setselected(moderateactiveimg, sedentaryimg, very_activeimg, lightlyactiveimg)
                setselectedtext(
                    moderate_active1,
                    very_activetext1,
                    lightlyactive1,
                    sedentary1,
                    moderate_active2,
                    very_activetext2,
                    lightlyactive2,
                    sedentary2
                )
                pcselectedDrawable = R.drawable.moderateactive
                pctext1 = moderate_active1.id
                pctext2 = moderate_active2.id
                pcimg = moderateactiveimg.id
                pcadded = mPrefs.getInt("mediumActive", 450)
                pcselectedString = R.string.medium_active
            }
            lightlyActiveCL.setOnClickListener() {
                setselected(lightlyactiveimg, moderateactiveimg, very_activeimg, sedentaryimg)
                setselectedtext(
                    lightlyactive1,
                    moderate_active1,
                    very_activetext1,
                    sedentary1,
                    lightlyactive2,
                    moderate_active2,
                    very_activetext2,
                    sedentary2
                )
                pcselectedDrawable = R.drawable.lightlyactive
                pctext1 = lightlyactive1.id
                pctext2 = lightlyactive2.id
                pcimg = lightlyactiveimg.id
                pcadded = mPrefs.getInt("littleActive", 220)
                pcselectedString = R.string.little_active
            }
            sedentaryCL.setOnClickListener() {
                setselected(sedentaryimg, moderateactiveimg, very_activeimg, lightlyactiveimg)
                setselectedtext(
                    sedentary1,
                    moderate_active1,
                    lightlyactive1,
                    very_activetext1,
                    sedentary2,
                    moderate_active2,
                    lightlyactive2,
                    very_activetext2
                )
                pcselectedDrawable = R.drawable.human
                pctext1 = sedentary1.id
                pctext2 = sedentary2.id
                pcimg = sedentaryimg.id
                pcadded = 0
                pcselectedString = R.string.not_active
            }
            builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                val lastadded: Int = mPrefs.getInt("pcadded", 0)
                var getdrinked: Int = mPrefs.getInt("dailygoal", 3000)
                Log.e("before getdrinked", "" + getdrinked)
                Log.e("before pcadded", "" + lastadded)

                if (pctext2 == very_activetext2.id) {
                    getdrinked -= lastadded
                    getdrinked += pcadded
                    Log.e("Call", "very_activetext2")
                } else if (pctext2 == moderate_active2.id) {
                    getdrinked -= lastadded
                    getdrinked += pcadded
                    Log.e("Call", "moderate_active2")

                } else if (pctext2 == lightlyactive2.id) {
                    getdrinked -= lastadded
                    getdrinked += pcadded
                    Log.e("Call", "lightlyactive2")

                } else if (pctext2 == sedentary2.id) {
                    getdrinked -= lastadded
                    getdrinked += pcadded
                    Log.e("Call", "sedentary2")
                }
                animateTextView(mPrefs.getInt("dailygoal", 3000), getdrinked, waterinput)
                Log.e("After getdrinked", "" + getdrinked)
                putIntSharep("pcselectedDrawable", pcselectedDrawable)
                putIntSharep("pctext1", pctext1)
                putIntSharep("pctext2", pctext2)
                putIntSharep("pcimg", pcimg)
                putIntSharep("pcadded", pcadded)
                putIntSharep("pcselectedString", pcselectedString)
                putIntSharep("dailygoal", getdrinked)
                physicalActivity.setImageResource(pcselectedDrawable)
                Log.e("pcadded", "" + mPrefs.getInt("pcadded", 0))
                dialog.cancel()
            }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
            }
            builder.setView(inflate)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        weather.setOnClickListener() {
            var weatherselectedDrawable =
                mPrefs.getInt("weatherselectedDrawable", R.drawable.cloudy)
            var weathertext1 = mPrefs.getInt("weathertext1", R.id.normaltext1)
            var weathertext11 = mPrefs.getInt("weathertext11", R.id.normaltext11)
            var weatherimg = mPrefs.getInt("weatherimg", R.id.pc3)
            var weatheadded = mPrefs.getInt("weatheadded", 0)
            var weatherselectedString = 0
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.Physical_activity)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.custom_weather, null)
            //  val weatherSwtich: SwitchCompat = inflate.findViewById(R.id.weatherSwtich)
            val hotCL: ConstraintLayout = inflate.findViewById(R.id.hotCL)
            val warmCL: ConstraintLayout = inflate.findViewById(R.id.warmCL)
            val normalCL: ConstraintLayout = inflate.findViewById(R.id.normalCL)
            val coldCL: ConstraintLayout = inflate.findViewById(R.id.coldCL)
            val hotweatherimg: ImageView = inflate.findViewById(R.id.cw1)
            val coldweatherimg: ImageView = inflate.findViewById(R.id.pc4)
            val warmweatherimg: ImageView = inflate.findViewById(R.id.pc2)
            val normalweatherimg: ImageView = inflate.findViewById(R.id.pc3)
            val hottext1: TextView = inflate.findViewById(R.id.hottext1)
            val hottext11: TextView = inflate.findViewById(R.id.hottext11)
            val warmtext1: TextView = inflate.findViewById(R.id.warmtext1)
            val warmtext11: TextView = inflate.findViewById(R.id.warmtext11)
            val normaltext1: TextView = inflate.findViewById(R.id.normaltext1)
            val normaltext11: TextView = inflate.findViewById(R.id.normaltext11)
            val coldtext1: TextView = inflate.findViewById(R.id.coldtext1)
            val coldtext11: TextView = inflate.findViewById(R.id.coldtext11)
            hottext11.text = "+${mPrefs.getInt("summersVal", 530)}" + " ml"
            warmtext11.text = "+${mPrefs.getInt("springVal", 325)}" + " ml"
            coldtext11.text = "+${mPrefs.getInt("winterVal", 210)}" + " ml"
            when (weathertext1) {
                hottext1.id -> {
                    preselected(hottext1, hottext11, hotweatherimg)
                }
                warmtext1.id -> {
                    preselected(warmtext1, warmtext11, warmweatherimg)
                }
                normaltext1.id -> {
                    preselected(normaltext1, normaltext11, normalweatherimg)
                }
                coldtext1.id -> {
                    preselected(coldtext1, coldtext11, coldweatherimg)
                }
            }

            hotCL.setOnClickListener() {
                setselected(hotweatherimg, coldweatherimg, warmweatherimg, normalweatherimg)
                setselectedtext(
                    hottext1,
                    warmtext1,
                    normaltext1,
                    coldtext1,
                    hottext11,
                    warmtext11,
                    normaltext11,
                    coldtext11
                )
                weatherselectedDrawable = R.drawable.sun
                weathertext1 = hottext1.id
                weathertext11 = hottext11.id
                weatherimg = hotweatherimg.id
                weatheadded = mPrefs.getInt("summersVal", 530)
                weatherselectedString = R.string.Summer
            }
            warmCL.setOnClickListener() {
                setselected(warmweatherimg, coldweatherimg, hotweatherimg, normalweatherimg)
                setselectedtext(
                    warmtext1,
                    hottext1,
                    normaltext1,
                    coldtext1,
                    warmtext11,
                    hottext11,
                    normaltext11,
                    coldtext11
                )
                weatherselectedDrawable = R.drawable.warm
                weathertext1 = warmtext1.id
                weathertext11 = warmtext11.id
                weatherimg = warmweatherimg.id
                weatheadded = mPrefs.getInt("springVal", 325)
                weatherselectedString = R.string.spring
            }
            normalCL.setOnClickListener() {
                setselected(normalweatherimg, warmweatherimg, hotweatherimg, coldweatherimg)
                setselectedtext(
                    normaltext1,
                    warmtext1,
                    hottext1,
                    coldtext1,
                    normaltext11,
                    warmtext11,
                    hottext11,
                    coldtext11
                )
                weatherselectedDrawable = R.drawable.cloudy
                weathertext1 = normaltext1.id
                weathertext11 = normaltext11.id
                weatherimg = normalweatherimg.id
                weatheadded = 0
                weatherselectedString = R.string.normal
            }
            coldCL.setOnClickListener() {
                setselected(coldweatherimg, warmweatherimg, hotweatherimg, normalweatherimg)
                setselectedtext(
                    coldtext1,
                    warmtext1,
                    normaltext1,
                    hottext1,
                    coldtext11,
                    warmtext11,
                    normaltext11,
                    hottext11
                )
                weatherselectedDrawable = R.drawable.cold
                weathertext1 = coldtext1.id
                weathertext11 = coldtext11.id
                weatherimg = coldweatherimg.id
                weatheadded = mPrefs.getInt("winterVal", 210)
                weatherselectedString = R.string.winter
            }
            builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                val lastadded: Int = mPrefs.getInt("weatheadded", 0)
                var getdrinked: Int = mPrefs.getInt("dailygoal", 3000)
                if (weathertext11 == hottext11.id) {
                    getdrinked -= lastadded
                    getdrinked += weatheadded
                    Log.e("Call", "very_activetext2")
                } else if (weathertext11 == warmtext11.id) {
                    getdrinked -= lastadded
                    getdrinked += weatheadded
                    Log.e("Call", "moderate_active2")
                } else if (weathertext11 == normaltext11.id) {
                    getdrinked -= lastadded
                    getdrinked += weatheadded
                    Log.e("Call", "lightlyactive2")

                } else if (weathertext11 == coldtext11.id) {
                    getdrinked -= lastadded
                    getdrinked += weatheadded
                    Log.e("Call", "sedentary2")
                }
                animateTextView(mPrefs.getInt("dailygoal", 3000), getdrinked, waterinput)
                putIntSharep("weatherselectedDrawable", weatherselectedDrawable)
                putIntSharep("weathertext1", weathertext1)
                putIntSharep("weathertext11", weathertext11)
                putIntSharep("weatherimg", weatherimg)
                putIntSharep("weatheadded", weatheadded)
                putIntSharep("weatherselectedString", weatherselectedString)
                putIntSharep("dailygoal", getdrinked)
                weather.setImageResource(weatherselectedDrawable)
                dialog.cancel()
            }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
            }
            builder.setView(inflate)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        reminderCardView.setOnClickListener() {
            startActivity(Intent(this, Reminder::class.java))
        }
        updatelog()
        logAdapter = LogAdapter(applicationContext, loglist, timelist, drawablelist)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        logAdapter.notifyDataSetChanged()
        recyclerView.adapter = logAdapter
        drinkPicker.displayedValues = drinkArray
        drinkPicker.maxValue = drinkArray.size - 1
        drinkPicker.minValue = 0
//        if (mPrefs.getBoolean("purFirstLoad",false)){
//            Log.e("TAG", "purFirstLoad:true ", )
//            drinkPicker.value = 0
//            adjustMidbutton(midicon[0], R.color.Blue)
//            putBooleanShareP("purFirstLoad",false)
//
//        }else{
//            Log.e("TAG", "purFirstLoad:false ", )
//            drinkPicker.value = mPrefs.getInt("drinkPicker", 0)
//        }

        drinkPicker.value = mPrefs.getInt("drinkPicker", 0)
        drinkPicker.invalidate()
        drinkPicker.wrapSelectorWheel = true
        drinkPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            editor = mPrefs.edit()
            editor.putString("drinktype", drinkArray[newVal])
            editor.putInt("drinkPicker", newVal)
            editor.commit()
            when {
                drinkArray[newVal] == "Water" -> {
                    adjustMidbutton(midicon[newVal], R.color.Blue)
                }
                drinkArray[newVal] == "Tea" -> {
                    adjustMidbutton(midicon[newVal], R.color.Goldenrod)
                }
                drinkArray[newVal] == "Coffee" -> {
                    adjustMidbutton(midicon[newVal], R.color.SaddleBrown)
                }
                drinkArray[newVal] == "Milk" -> {
                    adjustMidbutton(midicon[newVal], R.color.SteelBlue)
                }
                drinkArray[newVal] == "Fruit Juice" -> {
                    adjustMidbutton(midicon[newVal], R.color.Orange)
                }
                drinkArray[newVal] == "Soda" -> {
                    adjustMidbutton(midicon[newVal], R.color.DarkRed)
                }
                drinkArray[newVal] == "Beer" -> {
                    if (mPrefs.getBoolean("PURCHASE", false)) {
                        adjustMidbutton(midicon[newVal], R.color.Goldenrod)
                    } else {
                        adjustMidbutton(R.drawable.crown, R.color.Gold)
                    }
                }
                drinkArray[newVal] == "Energy Drink" -> {
                    if (mPrefs.getBoolean("PURCHASE", false)) {
                        adjustMidbutton(midicon[newVal], R.color.black)
                    } else {
                        adjustMidbutton(R.drawable.crown, R.color.Gold)
                    }
                }
                drinkArray[newVal] == "Lemonade" -> {
                    if (mPrefs.getBoolean("PURCHASE", false)) {
                        adjustMidbutton(midicon[newVal], R.color.GreenYellow)
                    } else {
                        adjustMidbutton(R.drawable.crown, R.color.Gold)
                    }

                }
                else -> {
                    adjustMidbutton(R.drawable.crown, R.color.Gold)
                }
            }
        }
        plusWater.setOnClickListener() {
            if (oldlist.size >= 1) {
                if (oldlist.size == 1) {
                    drinkSize.text = oldlist[0]
                    mid_button.text = oldlist[0]
                    putStringSharep("mid_button_text", mid_button.text.toString())
                    putIntSharep("getval", getval)
                } else {
                    getval++
                    if (getval >= oldlist.size) {
                        getval = 0
                        drinkSize.text = oldlist[getval]
                        mid_button.text = oldlist[getval]
                        putStringSharep("mid_button_text", mid_button.text.toString())
                        putIntSharep("getval", getval)

                    } else {
                        drinkSize.text = oldlist[getval]
                        mid_button.text = oldlist[getval]
                        putStringSharep("mid_button_text", mid_button.text.toString())
                        putIntSharep("getval", getval)

                    }
                }
            } else {
                drinkSize.text = "NULL"
                mid_button.text = "NULL"
                putStringSharep("mid_button_text", mid_button.text.toString())

            }
        }
        minusWater.setOnClickListener() {
            if (oldlist.size >= 1) {
                if (oldlist.size == 1) {
                    drinkSize.text = oldlist[0]
                    mid_button.text = oldlist[0]
                    putStringSharep("mid_button_text", mid_button.text.toString())
                    putIntSharep("getval", getval)

                } else {
                    getval--
                    if (getval <= 0) {
                        if (getval < 0) {
                            getval = oldlist.size
                            drinkSize.text = oldlist[getval - 1]
                            mid_button.text = oldlist[getval - 1]
                            putStringSharep("mid_button_text", mid_button.text.toString())
                            putIntSharep("getval", getval)

                        } else {
                            drinkSize.text = oldlist[getval]
                            mid_button.text = oldlist[getval]
                            getval = oldlist.size
                            putStringSharep("mid_button_text", mid_button.text.toString())
                            putIntSharep("getval", getval)

                        }
                    } else {
                        drinkSize.text = oldlist[getval]
                        mid_button.text = oldlist[getval]
                        putStringSharep("mid_button_text", mid_button.text.toString())
                        putIntSharep("getval", getval)
                    }
                }
            } else {
                drinkSize.text = "NULL"
                mid_button.text = "NULL"
                putStringSharep("mid_button_text", mid_button.text.toString())
            }
        }
        addNew.setOnClickListener() {
            var already = false
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.addandremove)
            oldlist.clear()
            val getData = mPrefs.getString("watervalues", "100ml,200ml, 300ml")
            oldlist = convertStringToArray(getData.toString())
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.addnew, null)
            val cupinput: EditText = inflate.findViewById(R.id.addnewcups)
            val add: ImageButton = inflate.findViewById(R.id.addnewcupbutton)
            val recyclerView: RecyclerView = inflate.findViewById(R.id.addnewrv)
            val cupAdapter = CupAdapter(applicationContext, oldlist)
            recyclerView.layoutManager = LinearLayoutManager(applicationContext)
            recyclerView.adapter = cupAdapter

            cupAdapter.notifyDataSetChanged()
            cupinput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (!s?.isEmpty()!!) {
                        add.visibility = View.VISIBLE
                        add.isEnabled = true
                    } else {
                        add.visibility = View.GONE
                        add.isEnabled = false
                    }
                }
            })
            add.setOnClickListener() {
                var addedcup = mPrefs.getInt("addedCup", 0)
                val input: String = cupinput.text.toString()
                val addnew: Int = input.replace("[\\D]".toRegex(), "").toInt()
                for (i in 0 until oldlist.size) {
                    val check: Int = oldlist[i].replace("[\\D]".toRegex(), "").toInt()
                    if (check == addnew) {
                        already = true
                        break
                    } else already = false
                }
                if (already)
                    Toast.makeText(this, "Already added", Toast.LENGTH_SHORT).show()
                else {
                    oldlist.add("$input$getgoaltype")
                    cupAdapter.notifyDataSetChanged()
                    cupinput.setText("")
                    if (!mPrefs.getBoolean("cupaddedtask", false)) {
                        addedcup++
                        putIntSharep("addedCup", addedcup)
                    }
                }
            }
            builder.setPositiveButton(getString(R.string.save)) { dialog, which ->
                cupAdapter.notifyDataSetChanged()
                if (oldlist.size <= 1) {
                    drinkSize.text = "NULL"
                    mid_button.text = "NULL"
                    putStringSharep("mid_button_text", mid_button.text.toString())
                }
                val getcupdata: String = convertArrayToString(oldlist)
                putStringSharep("watervalues", getcupdata)

                if (mPrefs.getInt("addedCup", 0) >= 3) {
                    if (!mPrefs.getBoolean("cupaddedtask", false)) {
                        putBooleanShareP("cupaddedtask", true)
                        createRewardDialog(R.drawable.cupmaker, "Cup Maker")
                        putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                    }
                }
                dialog.cancel()
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                if (mPrefs.getInt("addedCup", 0) >= 3) {
                    if (!mPrefs.getBoolean("cupaddedtask", false)) {
                        putBooleanShareP("cupaddedtask", true)
                        createRewardDialog(R.drawable.cupmaker, "Cup Maker")
                        putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                    }
                }
                dialog.dismiss()
            }
            builder.setView(inflate)
            val dialog: AlertDialog = builder.create()
            dialog.window?.setLayout(600, 400)
            dialog.show()
        }
        if (logAdapter.itemCount == 0) {
            cardView.visibility = View.GONE
        } else if (logAdapter.itemCount >= 0) {
            cardView.visibility = View.VISIBLE
        }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mid_button.setCompoundDrawablesWithIntrinsicBounds(getdrinkimg, 0, 0, 0)
        mid_button.setTextColor(ContextCompat.getColor(this, getdrinktextcolor))

        ObjectAnimator.ofInt(progress_bar, "progress", progrvalue.toInt())
            .setDuration(500)
            .start()

        slash.text = "/"
        try {
            //waterDrinked.text = "$totaldrink"
            animateTextView(0, totaldrink, waterDrinked)
            waterinput.text = "${mPrefs.getInt("dailygoal", 3000)}"
            waterMeasures.text = getgoaltype

        } catch (e: Exception) {
            Log.e("Exception", "$e")
        }
        // setwaternumberpicker()
        waterIndicator.setOnClickListener() {
            val suggestedDailyGoal = mPrefs.getInt("suggestedDailyGoal", 2640)
            val genderValueTXT = mPrefs.getString("genderValueTXT", "Male")!!
            val weightTXT = mPrefs.getString("weightTXT", "65kg")!!
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.daily_goal)
            builder.setMessage(
                getString(R.string.daily_goal_info) + " ($genderValueTXT) and weight ($weightTXT) " + getString(
                    R.string.according
                ) + " $suggestedDailyGoal ml"
            )
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.daily_goal_alert, null)
            val goal_edit: EditText = inflate.findViewById(R.id.daily_goal_take)
            val goal_type: TextView = inflate.findViewById(R.id.daily_goal_type_alert)
            val daily_goalTotal: TextView = inflate.findViewById(R.id.daily_goalTotal)
            val pcText: TextView = inflate.findViewById(R.id.dailyGoalPC)
            val weatherText: TextView = inflate.findViewById(R.id.dailyGoalWeather)
            val pcimg: ImageView = inflate.findViewById(R.id.dailyGoalPCimg)
            val weatherimg: ImageView = inflate.findViewById(R.id.dailyGoalWeatherimg)
            val getpcnum = mPrefs.getInt("pcadded", 0)
            val getweathernum = mPrefs.getInt("weatheadded", 0)
            pcText.text = getString(
                mPrefs.getInt(
                    "pcselectedString",
                    R.string.not_active
                )
            ) +": +$getpcnum"
            weatherText.text = getString(
                mPrefs.getInt(
                    "weatherselectedString",
                    R.string.normal
                )
            ) + ": +$getweathernum"

            pcimg.setImageResource(mPrefs.getInt("pcselectedDrawable", R.drawable.human))
            weatherimg.setImageResource(
                mPrefs.getInt(
                    "weatherselectedDrawable",
                    R.drawable.cloudy
                )
            )

            goal_type.text = getgoaltype
            val getgoaledit = mPrefs.getInt("dailygoal", 3000)

            goal_edit.setText(getgoaledit.toString())
            daily_goalTotal.text = getString(R.string.total_sum)+"$getgoaledit ${goal_type.text}"
            goal_edit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        try {
                            val afterInput: Int = s.replace("[\\D]".toRegex(), "").toInt()
                            val set: Int = afterInput + getpcnum + getweathernum
                            daily_goalTotal.text = getString(R.string.total_sum)+"$set ${goal_type.text}"
                        } catch (e: Exception) {
                        }
                    }
                }
            })
            builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                val input: String = goal_edit.text.toString()
                val set: Int = input.replace("[\\D]".toRegex(), "").toInt()
                resetprogress(set)
                waterDrinked.text = "$totaldrink"
                val lastval: Int = waterinput.text.replace("[\\D]".toRegex(), "").toInt()
                animateTextView(lastval, set, waterinput)
                waterMeasures.text = "${goal_type.text}"
                putStringSharep("targetTXT", input + " " + goal_type.text)
                editor = mPrefs.edit()
                editor.putInt("dailygoal", set)
                editor.commit()
            }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
            }
            builder.setView(inflate)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        mid_button.setOnClickListener() {
            if (mPrefs.getInt("drinkimg", R.drawable.water_glass) == R.drawable.crown) {
                startActivity(
                    Intent(
                        this,
                        UpgradeActivity::class.java
                    )
                )
            } else if (mid_button.text == "NULL") {
                //  Toast.makeText(this,"Please add values !!",Toast.LENGTH_SHORT).show()
                Snackbar.make(
                    window.decorView.rootView,
                    "Please add values !!",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                val total: Int = mPrefs.getInt("dailygoal", 3000)
                val num: Int = drinkSize.text.replace("[\\D]".toRegex(), "").toInt()
                if (totaldrink >= total && progrvalue >= 100f) {
                    Snackbar.make(
                        window.decorView.rootView,
                        "Your daily goal completed",
                        Snackbar.LENGTH_LONG
                    ).show()
                    ObjectAnimator.ofInt(progress_bar, "progress", 100)
                        .setDuration(500)
                        .start()
                    progrvalue = 100f
                    val putLog: String = convertArrayToString(loglist)
                    val putTime: String = convertArrayToString(timelist)
                    val putDrawable: String = convertArrayToString(drawablelist)
                    val logupdate = LogData(
                        date.time,
                        putDrawable,
                        putLog,
                        putTime,
                        mPrefs.getInt("dailygoal", 3000),
                        mPrefs.getInt("dailygoal", 3000)
                    )
                    DB.updateContact(logupdate)
                    editor = mPrefs.edit()
                    editor.putFloat("progress", 100f)
                    editor.commit()
                } else {
                    val addpercent: Float = ((num * 100) / total).toFloat()
                    if (progrvalue <= 100) {
                        progrvalue += addpercent
                        updateProgressBar(num)
                    }
                }
                if (progrvalue >= 100f) {
                    var rowCount = 0
                    if (!mPrefs.getBoolean("Fivedaysrow", false)) {
                        if (mPrefs.getBoolean("oneTime", true)) {
                            val calendar = Calendar.getInstance()
                            val getdate: Long = getmilis(sdfdate.format(calendar.time))
                            val weekCal = Calendar.getInstance()
                            val weekMili = ArrayList<Long>()
                            weekMili.clear()
                            weekCal.timeInMillis = getdate
                            for (i in 1..5) {
                                weekCal.set(Calendar.DATE, -1).toString()
                                Log.e("TAG", "onCreate: " + sdfdate.format(weekCal.time))
                                calendar.time = weekCal.time
                                weekMili.add(calendar.timeInMillis)
                            }
                            for (i in 0..4) {
                                if (DB.checkIfRecordExist(weekMili[i])) {
                                    val getOldRecord = DB.getonevalue(weekMili[i])
                                    if (getOldRecord.totaldrink!! >= getOldRecord.dailygoal!!) {
                                        rowCount++
                                    }
                                } else {
                                    Log.e("TAG", "Not Exist: ")
                                }
                            }
                            Log.e("TAG", "rowCount:$rowCount ")
                            putBooleanShareP("oneTime", false)
                            checkAchievements()
                        }
                    }
                    if (!mPrefs.getBoolean("achivedailygoal", false)) {
                        putBooleanShareP("achivedailygoal", true)
                        Log.e("TAG", "achivedailygoal:true ")
                        createRewardDialog(R.drawable.mountainflag, "1st Strike")
                        putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                    }
                }
                updateAchievementPB()
            }
        }
        MobileAds.initialize(this) {}
        val adsLayout: LinearLayout = findViewById(R.id.adsll)
        mAdView = findViewById(R.id.madView)

        if (!mPrefs.getBoolean("SmartDrinkINAPP", false)) {
            val networkState = NetworkState()
            if (networkState.isNetworkAvailable(this)) {
                val adRequest = AdRequest.Builder().build()
                mAdView.loadAd(adRequest)
                    adsLayout.visibility = View.VISIBLE
              }
        }

        sendnotification()
        if (mPrefs.getBoolean("permanent_notification", false) && mPrefs.getBoolean(
                "start",
                false
            )
        ) {
            startService()
            putBooleanShareP("start", false)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("custom-message")
        )
        if (mPrefs.getBoolean("firstTIME", true)) {
            mid_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.water_glass, 0, 0, 0)
            mid_button.setTextColor(ContextCompat.getColor(this, R.color.Blue))
            mid_button.text = "100ml"
            drinkSize.text = "100ml"
            putBooleanShareP("firstTIME", false)
        }
        updatereminderText()
    }

    private fun createRewardDialog(image: Int, name: String) {
        val hearts = intArrayOf(
            R.drawable.water_glass,
            R.drawable.coffee,
            R.drawable.milk,
            R.drawable.tea,
            R.drawable.sodecan,
            R.drawable.fruitjuice,
            R.drawable.beermag,
            R.drawable.enerydrink,
            R.drawable.lemonade
        )

        for (element in hearts) {
            ParticleSystem(this, 100, element, 3000)
                .setAcceleration(0.00013f, 90)
                .setSpeedByComponentsRange(0f, 0f, 0.05f, 0.1f)
                .setFadeOut(200, AccelerateInterpolator())
                .emitWithGravity(findViewById(R.id.emiter_top_left), Gravity.BOTTOM, 8, 5000)
        }
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        val inflater = LayoutInflater.from(this)
        val inflate: View = inflater.inflate(R.layout.reward_layout, null)
        val rewardName: TextView = inflate.findViewById(R.id.rewardName)
        val rewardImg: ImageView = inflate.findViewById(R.id.rewardImg)
        val viewRewardBtn: Button = inflate.findViewById(R.id.viewRewardBtn)
        rewardName.text = "You Achieved $name"
        rewardImg.setImageResource(image)
        viewRewardBtn.setOnClickListener() {
            startActivity(Intent(this, Achievements::class.java))
        }
        builder.setView(inflate)
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    private fun updateAchievementPB() {
        var totalLog = 0
        var goalTotal = mPrefs.getInt("achievementTotalGoal", 5000)
        var achievementPB = 0f
        var goalPercent: Float
        var totalTAP = 0
        var totalGOAL = 0
        val DB = DatabaseHandler(this)
        val calendar = Calendar.getInstance()
        var activeDays = 0
        val weekCal = Calendar.getInstance()
        weekCal.timeInMillis = mPrefs.getLong("startedDate", calendar.timeInMillis)
        for (i in 0..11) {
            weekCal.set(Calendar.DAY_OF_MONTH, 1).toString()
            val num = weekCal.getActualMaximum(Calendar.DAY_OF_MONTH)

            for (j in 1..num) {
                calendar.time = weekCal.time
                if (DB.checkIfRecordExist(calendar.timeInMillis)) {
                    activeDays++
                    val getOldRecord = DB.getonevalue(calendar.timeInMillis)
                    val getLogval = convertStringToArray(getOldRecord.logvalue!!)
                    totalTAP += getLogval.size
                    totalGOAL = getOldRecord.dailygoal!!
                    for (k in 0 until getLogval.size) {
                        totalLog += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                    }
                }
                weekCal.add(Calendar.DATE, 1)
            }
        }
        if (totalGOAL <= 0) {
            totalGOAL = mPrefs.getInt("dailygoal", 3000)
        }
        val grandPercent = ((totalLog * 100) / totalGOAL)
        Log.e("TAG", "grandPercent: $grandPercent")
        Log.e("TAG", "activeDays:$activeDays ")
        Log.e("TAG", "totalTAP:$totalTAP ")
        putIntSharep("grandPercent", grandPercent)
        putIntSharep("activeDays", activeDays)
        putIntSharep("totalTAP", totalTAP)
        putIntSharep("goalAchieve", totalLog)
        goalPercent = ((totalLog * 100) / goalTotal).toFloat()
        Log.e("TAG", "goalPercent: $goalPercent")
        Log.e("TAG", "totalLog: $totalLog")

        if (totalLog >= goalTotal) {
            Log.e("TAG", "Previous goal Achieved:$goalTotal")
            goalTotal *= 2
            when (goalTotal) {
                5000 -> {
                    levelTEXT.text = "Level 1"
                    putStringSharep("level", "Level 1")
                }
                10000 -> {
                    createRewardDialog(R.drawable.achivement_water_icon, "Level 2")
                    levelTEXT.text = "Level 2"
                    putStringSharep("level", "Level 2")
                }
                20000 -> {
                    createRewardDialog(R.drawable.achivement_water_icon, "Level 3")
                    levelTEXT.text = "Level 3"
                    putStringSharep("level", "Level 3")
                }
                40000 -> {
                    createRewardDialog(R.drawable.achivement_water_icon, "Level 4")
                    levelTEXT.text = "Level 4"
                    putStringSharep("level", "Level 4")
                }
                80000 -> {
                    createRewardDialog(R.drawable.achivement_water_icon, "Level 5")
                    levelTEXT.text = "Level 5"
                    putStringSharep("level", "Level 5")
                }
                160000 -> {
                    createRewardDialog(R.drawable.achivement_water_icon, "Level 6")
                    levelTEXT.text = "Level 6"
                    putStringSharep("level", "Level 6")
                }
                320000 -> {
                    createRewardDialog(R.drawable.achivement_water_icon, "Level 7")
                    levelTEXT.text = "Level 7"
                    putStringSharep("level", "Level 7")
                }
                640000 -> {
                    createRewardDialog(R.drawable.achivement_water_icon, "Level 8")
                    levelTEXT.text = "Level 8"
                    putStringSharep("level", "Level 8")
                }
                1280000 -> {
                    createRewardDialog(R.drawable.achivement_water_icon, "Level 9")
                    levelTEXT.text = "Level 9"
                    putStringSharep("level", "Level 9")
                }
            }
            goalPercent = ((totalLog * 100) / goalTotal).toFloat()
            achievementPB += goalPercent
            putIntSharep("achievementPB", achievementPB.toInt())
            putIntSharep("achievementTotalGoal", goalTotal)
        } else {
            achievementPB += goalPercent
            putIntSharep("achievementTotalGoal", goalTotal)
            putIntSharep("achievementPB", achievementPB.toInt())
        }
        Log.e("TAG", "achievementPB:$achievementPB ")

    }

    private fun checkAchievements() {
        Log.e("TAG", "checkAchievements: ")
        var numberOfDays = 0
        var monthTotal = 0
        var fiveDayRow = 0
        var sevenDayRow = 0
        var rowCount = 0
        var fourDayscupCount = 0
        var sixDayscupCount = 0
        var goalTotal = 0
        DB = DatabaseHandler(this)
        val calendar = Calendar.getInstance()
        val nextDate = Calendar.getInstance()
        val weekCal = Calendar.getInstance()
        weekCal.timeInMillis = mPrefs.getLong("startedDate", calendar.timeInMillis)
        for (i in 0..11) {
            weekCal.set(Calendar.DAY_OF_MONTH, 1).toString()
            val num = weekCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (j in 1..num) {
                calendar.time = weekCal.time
                if (DB.checkIfRecordExist(calendar.timeInMillis)) {
                    val getOldRecord = DB.getonevalue(calendar.timeInMillis)
                    val getLogval = convertStringToArray(getOldRecord.logvalue!!)
                    goalTotal += getOldRecord.dailygoal!!
                    for (k in 0 until getLogval.size) {
                        monthTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                    }
                    if (getLogval.size >= 3) {
                        fourDayscupCount++
                    }
                    if (getLogval.size >= 5) {
                        sixDayscupCount++
                    }
                    if (getOldRecord.totaldrink!! >= getOldRecord.dailygoal!!) {
                        rowCount++
                        nextDate.time = weekCal.time
                    }
                    if (getOldRecord.totaldrink!! >= getOldRecord.dailygoal!!) {
                        fiveDayRow++
                        sevenDayRow++
                        if (fiveDayRow >= 5) {
                            if (!mPrefs.getBoolean("FivedaysrowTask", false)) {
                                putBooleanShareP("FivedaysrowTask", true)
                                createRewardDialog(R.drawable.highfive, "High Five")
                                putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                            }
                        }
                        if (sevenDayRow >= 7) {
                            if (!mPrefs.getBoolean("sevendaysrowTask", false)) {
                                putBooleanShareP("sevendaysrowTask", true)
                                createRewardDialog(R.drawable.chainmaster, "Chain Master")
                                putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                            }
                        }

                    } else {
                        if (fiveDayRow >= 5) {
                            if (!mPrefs.getBoolean("FivedaysrowTask", false)) {
                                putBooleanShareP("FivedaysrowTask", true)
                                createRewardDialog(R.drawable.highfive, "High Five")
                                putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                            }
                        } else {
                            fiveDayRow = 0
                            Log.e("TAG", "Else fiveDayRow: $fiveDayRow")
                        }
                        if (sevenDayRow >= 7) {
                            if (!mPrefs.getBoolean("sevendaysrowTask", false)) {
                                putBooleanShareP("sevendaysrowTask", true)
                                createRewardDialog(R.drawable.chainmaster, "Chain Master")
                                putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                            }
                        } else {
                            sevenDayRow = 0
                            Log.e("TAG", "Else fiveDayRow: $sevenDayRow")
                        }
                    }

                    numberOfDays++
                }
                weekCal.add(Calendar.DATE, 1)
            }
            if (fourDayscupCount >= 30) {
                if (!mPrefs.getBoolean("fourDaysCupTask", false)) {
                    putBooleanShareP("fourDaysCupTask", true)
                    createRewardDialog(R.drawable.devotion, " Devotion")
                    putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                }
            }
            if (numberOfDays >= 90) {
                Log.e("TAG", "numberOfMonth: $numberOfDays")
                val ninetyDaysAverage = monthTotal / num
                if (ninetyDaysAverage >= 2150) {
                    if (!mPrefs.getBoolean("ninetyDaysAverageTask", false)) {
                        putBooleanShareP("ninetyDaysAverageTask", true)
                        createRewardDialog(R.drawable.waterlord, "Water Lord")
                        putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                    }
                }
                if (sixDayscupCount >= 90) {
                    if (!mPrefs.getBoolean("sixDayscupCountTask", false)) {
                        putBooleanShareP("sixDayscupCountTask", true)
                        createRewardDialog(R.drawable.aquahero, "Aqua Hero")
                        putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                    }
                }
            }
            if (i == 0) {
                if (numberOfDays == num) {
                    Log.e("TAG", "numberOfMonth: $numberOfDays")
                    val monthAverage = monthTotal / num
                    if (monthAverage >= 2150) {
                        putBooleanShareP("monthlyBossTask", true)
                        if (!mPrefs.getBoolean("monthlyBossTask", false)) {
                            putBooleanShareP("monthlyBossTask", true)
                            createRewardDialog(R.drawable.monthlyboss, "Monthly Boss")
                            putIntSharep("totalAchieve", mPrefs.getInt("totalAchieve", 0) + 1)
                        }
                    }
                }
            }
        }
    }

    private fun putBooleanShareP(name: String, value: Boolean) {
        editor = mPrefs.edit()
        editor.putBoolean(name, value)
        editor.commit()
    }
    @SuppressLint("SimpleDateFormat")
    private fun getmilis(date: String): Long {
        val timeInMilliseconds: Long
        val sdfdate = SimpleDateFormat("yyyy-MM-dd")
        sdfdate.isLenient = false
        val mDate = sdfdate.parse(date)
        timeInMilliseconds = mDate!!.time
        return timeInMilliseconds
    }
    @SuppressLint("CommitPrefEdits")
    private fun putStringSharep(name: String, values: String) {
        editor = mPrefs.edit()
        editor.putString(name, values)
        editor.commit()
    }
    @SuppressLint("CommitPrefEdits")
    private fun adjustMidbutton(drawble: Int, textcolor: Int) {
        mid_button.setCompoundDrawablesWithIntrinsicBounds(drawble, 0, 0, 0)
        mid_button.setTextColor(ContextCompat.getColor(this, textcolor))
        putIntSharep("drinkimg", drawble)
        getdrinkimg = mPrefs.getInt("drinkimg", R.drawable.water_glass)
        putIntSharep("drinktextcolour", textcolor)
    }
    @SuppressLint("CommitPrefEdits")
    private fun putIntSharep(name: String, values: Int) {
        editor = mPrefs.edit()
        editor.putInt(name, values)
        editor.commit()
    }
    @SuppressLint("SimpleDateFormat")
    fun getDate(milliSeconds: Long): String? {
        val formatter = SimpleDateFormat("hh:mm aa")
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    private fun preselected(text1: TextView, text2: TextView, img: ImageView) {
        text1.setTextColor(ContextCompat.getColor(this, R.color.textColour))
        text2.setTextColor(ContextCompat.getColor(this, R.color.textColour))
        img.setColorFilter(
            ContextCompat.getColor(this, R.color.Blue),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun setselected(
        selected: ImageView,
        img2: ImageView,
        img3: ImageView,
        img4: ImageView,
    ) {
        img2.setColorFilter(
            ContextCompat.getColor(this, R.color.textColourlight),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        img3.setColorFilter(
            ContextCompat.getColor(this, R.color.textColourlight),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        img4.setColorFilter(
            ContextCompat.getColor(this, R.color.textColourlight),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        selected.setColorFilter(
            ContextCompat.getColor(this, R.color.Blue),
            android.graphics.PorterDuff.Mode.SRC_IN
        )

    }

    private fun setselectedtext(
        selected1: TextView,
        text1: TextView,
        text2: TextView,
        text3: TextView,
        selected2: TextView,
        text11: TextView,
        text22: TextView,
        text33: TextView
    ) {
        text1.setTextColor(ContextCompat.getColor(this, R.color.textColourlight))
        text2.setTextColor(ContextCompat.getColor(this, R.color.textColourlight))
        text3.setTextColor(ContextCompat.getColor(this, R.color.textColourlight))
        text11.setTextColor(ContextCompat.getColor(this, R.color.textColourlight))
        text22.setTextColor(ContextCompat.getColor(this, R.color.textColourlight))
        text33.setTextColor(ContextCompat.getColor(this, R.color.textColourlight))
        selected1.setTextColor(ContextCompat.getColor(this, R.color.textColour))
        selected2.setTextColor(ContextCompat.getColor(this, R.color.textColour))
    }
    @SuppressLint("CommitPrefEdits")
    fun resetprogress(total: Int) {
        val num = mPrefs.getInt("totaldrink", 0)
        progrvalue = 0f
        val addpercent: Float = ((num * 100) / total).toFloat()
        if (progrvalue <= 100) {
            progrvalue += addpercent
            //  updateProgressBar(num)
            ObjectAnimator.ofInt(progress_bar, "progress", progrvalue.toInt())
                .setDuration(500)
                .start()
        }
        editor = mPrefs.edit()
        editor.putFloat("progress", progrvalue)
        editor.commit()
    }

    fun updatereminderText() {
        if (totaldrink >= mPrefs.getInt("dailygoal", 3000)) {
            Log.e("TAG", " If:updatereminderText: ", )
            remindTime.text = mPrefs.getString("wake_up_time", "8:00 AM")
        } else {
            Log.e("TAG", " Else:updatereminderText: ", )
            val nextremindertime: Long = mPrefs.getLong(
                "lastdrinktime",
                1621600680000
            ) + TimeUnit.MINUTES.toMillis(
                mPrefs.getInt("intervalTime", 90).toLong()
            )
            remindTime.text = getDate(nextremindertime)
        }
    }

    fun animateTextView(initialValue: Int, finalValue: Int, textview: TextView) {
        val valueAnimator = ValueAnimator.ofInt(initialValue, finalValue)
        valueAnimator.duration = 1500
        valueAnimator.addUpdateListener { vA ->
            textview.text = vA.animatedValue.toString()
        }
        valueAnimator.start()
    }

    fun updatelog() {
        val res: Cursor = DB.getdata()
        if (res.count == 0) {
            return
        } else {
            val getdatechecker = DB.checkIfRecordExist(date.time)
            if (getdatechecker) {
                DB = DatabaseHandler(this)
                val getOldRecord = DB.getonevalue(date.time)
                loglist = convertStringToArray(getOldRecord.logvalue.toString())
                timelist = convertStringToArray(getOldRecord.time.toString())
                drawablelist = convertStringToArray(getOldRecord.drawable.toString())
            }
        }
    }

    fun convertArrayToString(array: ArrayList<String>): String {
        var str = ""
        for (i in array.indices) {
            str += array[i]

            if (i < array.size - 1) {
                str += strSeparator
            }
        }
        return str
    }

    fun convertStringToArray(str: String): ArrayList<String> {
        val words = ArrayList<String>()
        for (w in str.trim(' ').split(strSeparator)) {
            if (w.isNotEmpty()) {
                words.add(w)
            }
        }
        return words
    }
    @SuppressLint("SimpleDateFormat", "CommitPrefEdits")
    private fun updateProgressBar(num: Int) {
        val sdf = SimpleDateFormat("hh:mm aa")
        val currentTime = sdf.format(Date())
        loglist.add("$num$getgoaltype")
        timelist.add(currentTime)
        drawablelist.add("" + getdrinkimg)
        logAdapter.notifyDataSetChanged()
        totaldrink = totaldrink + num
        val putLog: String = convertArrayToString(loglist)
        val putTime: String = convertArrayToString(timelist)
        val putDrawable: String = convertArrayToString(drawablelist)
        if (totaldrink >= mPrefs.getInt("dailygoal", 3000)) {
            ObjectAnimator.ofInt(progress_bar, "progress", 100)
                .setDuration(500)
                .start()
            val getdatechecker = DB.checkIfRecordExist(date.time)
            if (getdatechecker) {
                val logupdate = LogData(
                    date.time,
                    putDrawable,
                    putLog,
                    putTime,
                    mPrefs.getInt("dailygoal", 3000),
                    mPrefs.getInt("dailygoal", 3000)
                )
                logAdapter.notifyDataSetChanged()
                DB.updateContact(logupdate)
            }
        } else {
            ObjectAnimator.ofInt(progress_bar, "progress", progrvalue.toInt())
                .setDuration(500)
                .start()
            val getdatechecker = DB.checkIfRecordExist(date.time)
            if (getdatechecker) {
                val logupdate = LogData(
                    date.time,
                    putDrawable,
                    putLog,
                    putTime,
                    totaldrink,
                    mPrefs.getInt("dailygoal", 3000)
                )
                logAdapter.notifyDataSetChanged()
                DB.updateContact(logupdate)
            } else {
                val addlog =
                    LogData(
                        date.time,
                        "" + R.drawable.water_glass,
                        "$num$getgoaltype",
                        currentTime,
                        mPrefs.getInt("totaldrink", 0),
                        mPrefs.getInt("dailygoal", 3000)
                    )
                DB.addContact(addlog)
            }
            logAdapter.notifyDataSetChanged()
            if (logAdapter.itemCount == 0) {
                cardView.visibility = View.GONE
            } else if (logAdapter.itemCount >= 0) {
                cardView.visibility = View.VISIBLE
            }
        }
        Log.e("date.time", "" + date.time)
        // waterDrinked.text = "$totaldrink"
        val lastval: Int = waterDrinked.text.replace("[\\D]".toRegex(), "").toInt()
        animateTextView(lastval, totaldrink, waterDrinked)
        waterinput.text = "${mPrefs.getInt("dailygoal", 3000)}"
        waterMeasures.text = getgoaltype
        if (mPrefs.getBoolean("permanent_notification", false)
        ) {
            val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            val notificationBuilder = NotificationCompat.Builder(this, "CHANNELP")
            val notification = notificationBuilder
                .setOngoing(true)
                .setChannelId("CHANNELP")
                .setContentTitle("Time to drink")
                .setContentText("$totaldrink/${mPrefs.getInt("dailygoal", 3000)}$getgoaltype")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setAutoCancel(false)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setProgress(100, progrvalue.toInt(), false)
                .setSound(
                    null
                )
                .build()
            val notificationManager = applicationContext
                .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(101, notification)
        }
        editor = mPrefs.edit()
        editor.putLong("lastdate", date.time)
        editor.putFloat("progress", progrvalue)
        editor.putLong("lastdrinktime", System.currentTimeMillis())
        editor.putInt("totaldrink", totaldrink)
        editor.commit()
        updatereminderText()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else true
    }
    @SuppressLint("CommitPrefEdits")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reminder -> {
                startActivity(Intent(this, Reminder::class.java))
            }
            R.id.statistics -> {
                startActivity(Intent(this, Statistics::class.java))
            }
            R.id.achievement -> {
                startActivity(Intent(this, Achievements::class.java))
            }
            R.id.rate -> {
                val alert = AlertDialog.Builder(this, R.style.AlertDialogCustom)
                val inflater = LayoutInflater.from(applicationContext)
                val inflate: View = inflater.inflate(R.layout.rating_layout, null)
                val ratingbar = inflate.findViewById<RatingBar>(R.id.ratingBar)
                val submit = inflate.findViewById<Button>(R.id.rating_button)
                alert.setView(inflate)
                val dialog = alert.create()
                dialog.show()
                submit.setOnClickListener {
                    val rating = ratingbar.rating.toString()
                    if (rating.isEmpty()) {
                        Toast.makeText(this, "RATE US ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, rating, Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                }
            }
            R.id.contactus -> {
                val email = Intent(Intent.ACTION_SEND)
                email.putExtra(Intent.EXTRA_EMAIL, arrayOf("info@heaveninfotech.com"))
                email.putExtra(Intent.EXTRA_SUBJECT, "Inquiry")
                email.putExtra(Intent.EXTRA_TEXT, "Hello Sir/mam Inquiry related to : ")
                email.type = "message/rfc822"
                startActivity(Intent.createChooser(email, "Choose an Email client :"))
            }
            R.id.share -> {
                val ishare = Intent(Intent.ACTION_SEND)
                ishare.type = "text/plain"
                ishare.putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + packageName
                )
                startActivity(ishare)
                putBooleanShareP("share", true)
            }
//            R.id.backup -> {
//                startActivity(Intent(this, Backup::class.java))
//            }
            R.id.drawer_home -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.settings -> {
                startActivity(Intent(this, Setting::class.java))
            }
            R.id.privacyPolicyBTN -> {
//                val url = "https://heavyapps.co.in/privacy_policy.html"
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse(url)
//                startActivity(i)
                startActivity(Intent(this, WebViewActivity::class.java))
                val toWeb = Intent(this, WebViewActivity::class.java)
                toWeb.putExtra("webTitle", getString(R.string.privacy))
                toWeb.putExtra("webUrl", "https://heavyapps.co.in/privacy_policy.html")
                startActivity(toWeb)
            }
            R.id.termAndCondition -> {
//                val url = "https://heavyapps.co.in/termsandconditions.html"
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse(url)
//                startActivity(i)
                startActivity(Intent(this, WebViewActivity::class.java))
                val toWeb = Intent(this, WebViewActivity::class.java)
                toWeb.putExtra("webTitle", getString(R.string.termcondition))
                toWeb.putExtra("webUrl", "https://heavyapps.co.in/termsandconditions.html")
                startActivity(toWeb)
            }
        }
        return true

    }
    @SuppressLint("NewApi", "CommitPrefEdits")
    fun setLocale(activity: Int, languageCode: String?) {
        val languageToLoad = languageCode // your language
        val locale = Locale(languageToLoad!!)
        Locale.setDefault(locale)
        val config = Configuration()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            config.setLocale(locale)
//            baseContext.createConfigurationContext(config)
//        } else {
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )

        this.setContentView(activity)

    }

//    override fun onStart() {
//        super.onStart()
//         val  mPrefs: SharedPreferences = getSharedPreferences(
//            MyPREFERENCES, Context.MODE_PRIVATE
//        )
//        val getlag = mPrefs.getString("language", "en")
//        setLocale(R.layout.activity_main, getlag)
//    }
    fun sendnotification() {
        if (!mPrefs.getBoolean("reminderSwitch", true)) {
            if (mPrefs.getBoolean("permanent_notification", false)) {
                val intent1 = Intent(this, NotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent1,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val cal = Calendar.getInstance()
                cal.add(Calendar.MINUTE, mPrefs.getInt("intervalTime", 90))
                val am = this.getSystemService(ALARM_SERVICE) as AlarmManager
                am.setRepeating(
                    AlarmManager.RTC_WAKEUP, cal.timeInMillis, 60000,
                    pendingIntent
                )
            }
        }

    }

    fun startService() {
        if (mPrefs.getInt(
                "soundtype",
                R.raw.bubble
            ) == 3
        ) {
            try {
                val notification =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val r = RingtoneManager.getRingtone(
                    getApplicationContext(),
                    notification
                )
                r.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val mp: MediaPlayer = MediaPlayer.create(
                this, mPrefs.getInt(
                    "soundtype",
                    R.raw.bubble
                )
            )
            mp.start()
        }
        val serviceIntent = Intent(this, PermanentNotification::class.java)
        // serviceIntent.putExtra("inputExtra", input)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            return true
        }
        return false
    }


    var mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.e("TAG", "onReceive: ")
            // if (mPrefs.getBoolean("deletePerform", false)) {
            Log.e("TAG", "deletePerform: ")
            //Log.e("date.time", "" + date.time)
            val lastval: Int = waterDrinked.text.replace("[\\D]".toRegex(), "").toInt()
            totaldrink = mPrefs.getInt("totaldrink", 0)
            progrvalue = mPrefs.getFloat("progress", 0f)
            ObjectAnimator.ofInt(progress_bar, "progress", progrvalue.toInt())
                .setDuration(500)
                .start()
            if (totaldrink >= 0) {
                animateTextView(lastval, totaldrink, waterDrinked)
            } else {
                totaldrink = 0
            }
            waterinput.text = "${mPrefs.getInt("dailygoal", 3000)}"
            waterMeasures.text = getgoaltype
            if (logAdapter.itemCount == 0) {
                cardView.visibility = View.GONE
            } else if (logAdapter.itemCount >= 0) {
                cardView.visibility = View.VISIBLE
            }
            updatereminderText()
            updateAchievementPB()
        }

    }

}

