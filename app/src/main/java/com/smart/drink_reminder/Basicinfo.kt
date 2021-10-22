package com.smart.drink_reminder

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.smart.drink_reminder.Database.DatabaseHandler
import com.smart.drink_reminder.Services.NetworkState
import java.text.SimpleDateFormat
import java.util.*

class Basicinfo : AppCompatActivity(), View.OnClickListener {
    //    lateinit var male: Button
//    lateinit var female: Button
    lateinit var apply: Button
    private lateinit var number: NumberPicker
    lateinit var weight: NumberPicker
    lateinit var radioGroup: RadioGroup
    lateinit var drinkwatervalues: Array<String>
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var wakeup_time_picker: TextView
    lateinit var sleep_time_picker: TextView
    lateinit var basic_PB: ProgressBar
    val MyPREFERENCES = "DrinkWater"
    var gender: String = "male"
    var id: Int = 1
    lateinit var DB: DatabaseHandler
    lateinit var pickerVals: Array<String>
    var weight_number: String? = null
    var selectedWeight: Int? = null
    var dailygoal: Int? = null
    var weight_type: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basicinfo)
        init()

    }
    @SuppressLint("CommitPrefEdits")
    private fun init() {

        DB = DatabaseHandler(this)
        sharedPreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        apply = findViewById(R.id.basic_apply)

        pickerVals = arrayOf("100ml", "200ml", "300ml")
//        male = findViewById(R.id.male)
//        female = findViewById(R.id.female)
        number = findViewById(R.id.weight_numbers)
        weight = findViewById(R.id.weight)
        wakeup_time_picker = findViewById(R.id.wake_up_time_picker)
        sleep_time_picker = findViewById(R.id.sleep_time_picker)
        radioGroup = findViewById(R.id.bi_radioGroup)
        radioGroup.check(R.id.bi_maleRadio)
        wakeup_time_picker.setOnClickListener(this)
        sleep_time_picker.setOnClickListener(this)
        pickerVals = arrayOf("kg", "lbs")
        weight.displayedValues = pickerVals
        weight.maxValue = 1
        weight.minValue = 0
        number.maxValue = 300
        number.value = 65
        number.minValue = 10
        number.wrapSelectorWheel = true
        radioGroup.check(R.id.bi_maleRadio)
        putIntSharep("genderSelected", R.id.radioMale)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.bi_maleRadio -> {
                    gender = getString(R.string.male)
                    putIntSharep("genderSelected", R.id.radioMale)
                }
                R.id.bi_femaleRadio -> {
                    gender = getString(R.string.female)
                    putIntSharep("genderSelected", R.id.radioFemale)
                }
            }
        }

        apply.setOnClickListener(this)
    }

    @SuppressLint("CommitPrefEdits")
    private fun putIntSharep(name: String, values: Int) {
        editor = sharedPreferences.edit()
        editor.putInt(name, values)
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
    @SuppressLint("DefaultLocale", "CommitPrefEdits")
    private fun putdataPreferences(name: String, value: String) {
        editor = sharedPreferences.edit()
        editor.putString(name, value)
        editor.commit()
    }
    @SuppressLint("DefaultLocale")
    private fun puttime(settime: TextView?) {
        val c: Calendar = Calendar.getInstance()
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        val select_time: String? = null
        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(
            this,
            { view, hourOfDay, minute ->
                val isPM = hourOfDay >= 12
                if (settime != null) {
                    settime.text = java.lang.String.format(
                        "%02d:%02d %s",
                        if (hourOfDay == 12 || hourOfDay == 0) 12 else hourOfDay % 12,
                        minute,
                        if (isPM) "PM" else "AM"
                    )
                }
                Log.e("wakeup_time_picker", "" + wakeup_time_picker.text)
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
        Log.e("select_time", "" + select_time)
    }

    override fun onClick(v: View?) {
        if (v == wakeup_time_picker) {
            puttime(wakeup_time_picker)
        } else if (v == wakeup_time_picker) {
            puttime(sleep_time_picker)
        } else if (v == apply) {
            applyClick()
        }
    }

    @SuppressLint("CommitPrefEdits", "SimpleDateFormat")
    private fun applyClick() {
        weight_number = number.value.toString()
        weight_type = weight.value
        val type: String = pickerVals[weight_type!!]
        selectedWeight = number.value
        if (sharedPreferences.getInt("genderSelected", R.id.radioMale) == R.id.radioMale) {
            dailygoal = selectedWeight!! * 35
            putIntSharep("suggestedDailyGoal", selectedWeight!! * 35)
            putdataPreferences("targetTXT", "${selectedWeight!! * 35}ml")
            putIntSharep("dailygoal", number.value * 35)
            Log.e("TAG", "onCreate:male")
            putdataPreferences("gender", "Male")
            putdataPreferences("genderValueTXT", "Male")
        } else {
            dailygoal = selectedWeight!! * 32
            putIntSharep("suggestedDailyGoal", selectedWeight!! * 32)
            putdataPreferences("targetTXT", "${selectedWeight!! * 32}ml")
            putIntSharep("dailygoal", number.value * 32)
            Log.e("TAG", "onCreate:female ")
            putdataPreferences("gender", "Female")
            putdataPreferences("genderValueTXT", "Female")
        }
        putdataPreferences("weightTXT", "${number.value}kg")
        putdataPreferences("weight_type", type)
        putdataPreferences("weight_number", weight_number!!)
        putdataPreferences("wake_up_time", wakeup_time_picker.text as String)
        putdataPreferences("sleep_time", sleep_time_picker.text as String)
        putdataPreferences("watervalues", "100ml,200ml, 300ml")
        val sdfdate = SimpleDateFormat("yyyy-MM-dd")
        val getdate: String = sdfdate.format(Date())
        val date: Date = sdfdate.parse(getdate)!!
        val calendar = Calendar.getInstance()
        val getdatee: Long = getmilis(sdfdate.format(calendar.time))
        editor = sharedPreferences.edit()
        //  editor.putBoolean("apply", true)
        editor.putLong("lastdate", date.time)
        editor.putBoolean("firstTIME", true)
        editor.putLong("startedDate", getdatee)
        editor.putInt("veryActive", (dailygoal!! / 3.65).toInt())
        editor.putInt("mediumActive", (dailygoal!! / 5.651).toInt())
        editor.putInt("littleActive", (dailygoal!! / 11.001).toInt())
        editor.putInt("summersVal", (dailygoal!! / 3.95).toInt())
        editor.putInt("springVal", (dailygoal!! / 6.10).toInt())
        editor.putInt("winterVal", (dailygoal!! / 11.85).toInt())
        editor.putInt("totalAchieve", 0)
        editor.commit()
        weight_number = null
        weight_type = 0
        editor = sharedPreferences.edit()
        editor.putBoolean("apply", true)
        editor.commit()
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }

}