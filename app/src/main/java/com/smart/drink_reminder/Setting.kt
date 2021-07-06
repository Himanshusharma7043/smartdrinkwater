package com.smart.drink_reminder

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.smart.drink_reminder.AddCupRecyclerView.CupAdapter
import java.util.*

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Setting : AppCompatActivity() {
    lateinit var mAdView: AdView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    val MyPREFERENCES = "DrinkWater"
    var context: Context? = null
    lateinit var toolbar: Toolbar
    lateinit var getgoaltype: String
    var strSeparator = ","
    var getinput: Int = 0
    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        getinput = sharedPreferences.getInt("dailygoal", 3000)
        getgoaltype = sharedPreferences.getString("goaltype", "ml")!!
        mAdView = findViewById(R.id.madView)
        val unit: CardView = findViewById(R.id.setting_units)
        val weight: CardView = findViewById(R.id.setting_weight)
        val gender: CardView = findViewById(R.id.setting_gender)
        val daily_goal: CardView = findViewById(R.id.setting_daily_goal)
        val appadscd: CardView = findViewById(R.id.appadscd)
        val cups: CardView = findViewById(R.id.setting_cups)
        val language_select: CardView = findViewById(R.id.setting_language)
        val themes: CardView = findViewById(R.id.setting_theme)
        toolbar = findViewById(R.id.setting_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        unit.setOnClickListener() {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.units)
            builder.setMessage(R.string.unit_info)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.dialog_radiobutton, null)
            val radioGroup: RadioGroup = inflate.findViewById(R.id.radioGroup)
            val radioButton: RadioButton = inflate.findViewById(R.id.radioMale)
            val radioButton1: RadioButton = inflate.findViewById(R.id.radioFemale)
            radioButton.text = "kg,ml"
            radioButton1.text = "lbs,fl oz"
            radioGroup.check(sharedPreferences.getInt("unitSelected", R.id.radioMale))
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radioMale -> {
                        putIntSharep("unitSelected", R.id.radioMale)
                    }
                    R.id.radioFemale -> {
                        putIntSharep("unitSelected", R.id.radioFemale)
                    }
                }
            }
            builder.setPositiveButton(
                R.string.ok
            ) { dialog, which ->
                val selectedId = radioGroup.checkedRadioButtonId
                if (selectedId == -1) {
                    Toast.makeText(
                        this,
                        R.string.no_found,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    val radioButton = radioGroup
                        .findViewById(selectedId) as RadioButton
                    Toast.makeText(this, radioButton.text, Toast.LENGTH_SHORT).show()
                    if (radioButton.text == "kg,ml") {
                        editor = sharedPreferences.edit()
                        editor.putString("weighttype", "kg")
                        editor.commit()
                        editor = sharedPreferences.edit()
                        editor.putString("goaltype", "ml")
                        editor.commit()
                    } else {
                        editor = sharedPreferences.edit()
                        editor.putString("weighttype", "lbs")
                        editor.commit()
                    }

                }
            }.setNegativeButton(
                R.string.cancel
            ) { dialog, which ->
                dialog.cancel()
            }
            builder.setView(inflate)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        Handler().postDelayed({
            appadscd.visibility = View.VISIBLE
        }, 10000)
        gender.setOnClickListener() {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.gender)
            builder.setMessage(R.string.gender_info)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.dialog_radiobutton, null)
            val radioGroup: RadioGroup = inflate.findViewById(R.id.radioGroup)
            radioGroup.check(sharedPreferences.getInt("genderSelected", R.id.radioMale))
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radioMale -> {
                        putIntSharep("genderSelected", R.id.radioMale)
                    }
                    R.id.radioFemale -> {
                        putIntSharep("genderSelected", R.id.radioFemale)
                    }
                }
            }

            builder.setPositiveButton("OK") { dialog, which ->
                val selectedId = radioGroup.checkedRadioButtonId
                if (selectedId == -1) {
                    Toast.makeText(
                        this,
                        R.string.no_found,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    val radioButton = radioGroup
                        .findViewById(selectedId) as RadioButton
                    Toast.makeText(this, radioButton.text, Toast.LENGTH_SHORT).show()
                }
            }.setNegativeButton("CANCEL") { dialog, which ->
                dialog.cancel()
            }
            builder.setView(inflate)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        weight.setOnClickListener() {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.weight)
            builder.setMessage(R.string.weight_info)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.weight_alertdialog, null)
            val weight_edit: EditText = inflate.findViewById(R.id.weight_take)
            val weight_type: TextView = inflate.findViewById(R.id.weight_type_alert)
            builder.setPositiveButton("OK") { dialog, which ->
                val input: String = weight_edit.text.toString()
                Toast.makeText(
                    this,
                    "Weight :" + input + " " + weight_type.text,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }.setNegativeButton("CANCEL") { dialog, which ->
                dialog.cancel()
            }
            builder.setView(inflate)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        daily_goal.setOnClickListener() {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.daily_goal)
            builder.setMessage(R.string.daily_goal_info)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.daily_goal_alert, null)
            val goal_edit: EditText = inflate.findViewById(R.id.daily_goal_take)
            val goal_type: TextView = inflate.findViewById(R.id.daily_goal_type_alert)
            goal_type.text = sharedPreferences.getString("goaltype", "ml")
            val getgoaledit = sharedPreferences.getInt("dailygoal", 3000)
            val pcText: TextView = inflate.findViewById(R.id.dailyGoalPC)
            val daily_goalTotal: TextView = inflate.findViewById(R.id.daily_goalTotal)
            val weatherText: TextView = inflate.findViewById(R.id.dailyGoalWeather)
            val pcimg: ImageView = inflate.findViewById(R.id.dailyGoalPCimg)
            val weatherimg: ImageView = inflate.findViewById(R.id.dailyGoalWeatherimg)
            val getpcnum = sharedPreferences.getInt("pcadded", 0)
            val getweathernum = sharedPreferences.getInt("weatheadded", 0)
            pcText.text = getString(
                sharedPreferences.getInt(
                    "pcselectedString",
                    R.string.sedentary
                )
            ) + ": +$getpcnum"
            weatherText.text = getString(
                sharedPreferences.getInt(
                    "weatherselectedString",
                    R.string.normal
                )
            ) + ": +$getweathernum"

            pcimg.setImageResource(
                sharedPreferences.getInt(
                    "pcselectedDrawable",
                    R.drawable.human
                )
            )
            weatherimg.setImageResource(
                sharedPreferences.getInt(
                    "weatherselectedDrawable",
                    R.drawable.cloudy
                )
            )
            goal_edit.setText(getgoaledit.toString())
            daily_goalTotal.text = "Total sum: $getgoaledit ${goal_type.text}"
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
                            daily_goalTotal.text = "Total sum: $set ${goal_type.text}"
                        } catch (e: Exception) {
                        }
                    }
                }
            })
            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                val input: String = goal_edit.text.toString()
                Toast.makeText(
                    this,
                    "Daily Goal :" + input + " " + goal_type.text,
                    Toast.LENGTH_SHORT
                )
                    .show()
                editor = sharedPreferences.edit()
                editor.putInt("dailygoal", input.toIntOrNull()!!)
                editor.commit()
            }).setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
            builder.setView(inflate)
            val dialog: AlertDialog = builder.create()
            dialog.show()
//            dialog.getButton(DialogInterface.BUTTON_POSITIVE).textSize = 24f
//            dialog.getButton(DialogInterface.BUTTON_POSITIVE).textSize = 24f
        }
        themes.setOnClickListener() {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.theme_changing, null)
            val radioGroup: RadioGroup = inflate.findViewById(R.id.radioGroup)
            builder.setView(inflate)
            radioGroup.check(sharedPreferences.getInt("themeSelected", R.id.radioLight))
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val radioButton = group
                    .findViewById<View>(checkedId) as RadioButton
                val theme = when (checkedId) {
                    R.id.radioLight -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    R.id.radioDark -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_NO
                }
                when (checkedId) {
                    R.id.radioLight -> {
                        putIntSharep("charttextcolor",Color.BLACK)
                        putIntSharep("themeSelected", R.id.radioLight)
                    }
                    R.id.radioDark -> {
                        putIntSharep("charttextcolor",Color.WHITE)
                        putIntSharep("themeSelected", R.id.radioDark)
                    }
                }
                putIntSharep("theme", theme)
                // AppCompatDelegate.setDefaultNightMode(theme)
                //onRestart()
                Log.e("Theme:", "" + theme)
            }
            builder.setPositiveButton("Set", DialogInterface.OnClickListener { dialog, which ->
                val pd = ProgressDialog.show(
                    this, "",
                    "Loading...", true
                )
                pd.show()
                onRestart()
                dialog.dismiss()
            })
            val dialog: AlertDialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(R.color.backgroundColour)
            dialog.show()
        }
        language_select.setOnClickListener() {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.selectlanguage)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.language_select, null)
            val radioGroup: RadioGroup = inflate.findViewById(R.id.language_group)
            builder.setView(inflate)
            val dialog: AlertDialog = builder.create()
            dialog.show()
            radioGroup.check(sharedPreferences.getInt("languageSelected", R.id.eng_language))
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val radioButton = group
                    .findViewById<View>(checkedId) as RadioButton
                when (checkedId) {
                    R.id.eng_language -> {
                        setLocale(this, "en")
                        putIntSharep("languageSelected", R.id.eng_language)
                    }
                    R.id.hindi_language
                    -> {
                        setLocale(this, "hi")
                        putIntSharep("languageSelected", R.id.hindi_language)
                    }
                    R.id.gujarati_language -> {
                        setLocale(this, "gu")
                        putIntSharep("languageSelected", R.id.gujarati_language)
                    }
                }
                dialog.dismiss()
            }
        }
        cups.setOnClickListener() {
            val cupdata = ArrayList<String>()
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.addandremove)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.addnew, null)
            val cupinput: EditText = inflate.findViewById(R.id.addnewcups)
            val add: ImageButton = inflate.findViewById(R.id.addnewcupbutton)
            val recyclerView: RecyclerView = inflate.findViewById(R.id.addnewrv)
            val cupAdapter = CupAdapter(applicationContext, cupdata)
            recyclerView.layoutManager = LinearLayoutManager(applicationContext)
            recyclerView.adapter = cupAdapter
            var oldlist = ArrayList<String>()
            val getdata = sharedPreferences.getString("watervalues", "Add New,300ml, 200ml,100ml")
            oldlist = convertStringToArray(getdata.toString())!!
            for (i in 0 until oldlist.size) {
                if (oldlist[i] != "Add New") {
                    cupdata.add(oldlist[i])
                }
            }
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
                var input: String = cupinput.text.toString()
                var already: Boolean = false
                val add: Int = input.replace("[\\D]".toRegex(), "").toInt()
                for (i in 0 until cupdata.size) {
                    var check: Int = cupdata[i].replace("[\\D]".toRegex(), "").toInt()
                    if (check == add) {
                        already = true
                        break
                    } else already = false
                }
                if (already)
                    Toast.makeText(this, "Already added", Toast.LENGTH_SHORT).show()
                else {
                    cupdata.add("$input$getgoaltype")
                    cupAdapter.notifyDataSetChanged()
                    cupinput.setText("")
                }
            }
            builder.setPositiveButton("Done", DialogInterface.OnClickListener { dialog, which ->
                var input: String = cupinput.text.toString()
                cupdata.add("Add New")
                cupAdapter.notifyDataSetChanged()
                val getcupdata: String = convertArrayToString(cupdata)
                editor = sharedPreferences.edit()
                editor.putString("watervalues", getcupdata)
                editor.commit()
                dialog.cancel()
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            builder.setView(inflate)
            val dialog: AlertDialog = builder.create()
            dialog.window?.setLayout(600, 400)
            dialog.show()
        }
    }
    @SuppressLint("CommitPrefEdits")
    private fun putIntSharep(name: String, values: Int) {
        editor = sharedPreferences.edit()
        editor.putInt(name, values)
        editor.commit()
    }
    @SuppressLint("NewApi", "CommitPrefEdits")
    fun setLocale(activity: Activity, languageCode: String?) {
        val languageToLoad = languageCode // your language
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
        this.setContentView(R.layout.activity_setting)
        onRestart()
        editor = sharedPreferences.edit()
        editor.putString("language", languageToLoad)
        editor.commit()
        Log.e("Language:", "" + languageToLoad)
    }

    fun convertArrayToString(array: ArrayList<String>): String {
        var str: String = ""
        for (i in array.indices) {
            str += array[i]

            if (i < array.size - 1) {
                str += strSeparator
            }
        }
        return str
    }

    fun convertStringToArray(str: String): ArrayList<String>? {
        val words = ArrayList<String>()
        for (w in str.trim(' ').split(strSeparator)) {
            if (w.isNotEmpty()) {
                words.add(w)
            }
        }
        return words
        //val theList: List<String> = Arrays.asList(str .split(","))
        //return str.split(strSeparator).toTypedArray()
    }

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(Intent(this, Setting::class.java))
    }

    override fun onStart() {
        super.onStart()
        val mPrefs = getSharedPreferences(
            MyPREFERENCES, Context.MODE_PRIVATE
        ) //add key
        val getTheme =
            mPrefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(getTheme)
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

}