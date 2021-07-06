package com.smart.drink_reminder

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.smart.drink_reminder.Database.DatabaseHandler
import java.text.SimpleDateFormat
import java.util.*

class Basicinfo : AppCompatActivity() {

    lateinit var male: Button
    lateinit var female: Button
    lateinit var number: NumberPicker
    lateinit var weight: NumberPicker
    lateinit var drinkwatervalues: Array<String>
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var wakeup_time_picker: TextView
    lateinit var sleep_time_picker: TextView
    val MyPREFERENCES = "DrinkWater"
    var gender: String = "male"
    var id: Int = 1
    lateinit var DB: DatabaseHandler
    lateinit var pickerVals: Array<String>
    var weight_number: String? = null
    var weight_type: Int? = null
    var strSeparator = ","

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("DefaultLocale", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basicinfo)
        DB = DatabaseHandler(this)
        sharedPreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val apply: Button = findViewById(R.id.basic_apply)
        pickerVals = arrayOf( "100ml", "200ml", "300ml")
        val getstring: String = convertArrayToString(pickerVals)
        male = findViewById(R.id.male)
        female = findViewById(R.id.female)
        number = findViewById(R.id.weight_numbers)
        weight = findViewById(R.id.weight)
        wakeup_time_picker = findViewById(R.id.wake_up_time_picker)
        sleep_time_picker = findViewById(R.id.sleep_time_picker)
        wakeup_time_picker.setOnClickListener {
            puttime(wakeup_time_picker)
        }
        sleep_time_picker.setOnClickListener {
            puttime(sleep_time_picker)
        }
        pickerVals = arrayOf("kg", "lbs")
        weight.displayedValues = pickerVals
        weight.maxValue = 1
        weight.minValue = 0
        number.maxValue = 300
        number.value=65
        number.minValue = 10
        number.wrapSelectorWheel = true
        weight.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal == 0) {
                number.maxValue = 300
                number.value=65
                number.minValue = 10
                number.wrapSelectorWheel = true
            } else {
                number.maxValue = 661
                number.minValue = 22
                number.value=65
                number.wrapSelectorWheel = true
            }
        }
        weight_type = weight.value
        male.performClick()
        female.setBackgroundColor(resources.getColor(R.color.blur))
        male.setBackgroundColor(Color.WHITE)
        male.setOnClickListener {
            gender = "male"
            female.setBackgroundColor(resources.getColor(R.color.blur))
            male.setBackgroundColor(Color.WHITE)
        }
        female.setOnClickListener {
            gender = "female"
            male.setBackgroundColor(resources.getColor(R.color.blur))
            female.setBackgroundColor(Color.WHITE)
        }

        apply.setOnClickListener {
            weight_number = number.value.toString()
            weight_type = weight.value
            val type: String = pickerVals[weight_type!!]
            putdataPreferences("gender", gender)
            putdataPreferences("weight_type", type)
            putdataPreferences("weight_number", weight_number!!)
            putdataPreferences("wake_up_time", wakeup_time_picker.text as String)
            putdataPreferences("sleep_time", sleep_time_picker.text as String)
            putdataPreferences("watervalues",  "100ml,200ml, 300ml")
            val sdfdate = SimpleDateFormat("yyyy-MM-dd")
            val getdate: String =sdfdate.format(Date())
            val date:Date = sdfdate.parse(getdate)
            val calendar = Calendar.getInstance()
            val getdatee: Long = getmilis(sdfdate.format(calendar.time))
            editor = sharedPreferences.edit()
            editor.putBoolean("apply", true)
            editor.putLong("lastdate", date.time)
            editor.putLong("startedDate", getdatee)
            editor.commit()
            startActivity(Intent(this, MainActivity::class.java))
            weight_number = null
            weight_type = 0

        }

    }

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
                        if (hourOfDay === 12 || hourOfDay === 0) 12 else hourOfDay % 12,
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

    fun convertArrayToString(array: Array<String>): String {
        var str: String = ""
        for (i in array.indices) {
            str += array[i]

            if (i < array.size - 1) {
                str += strSeparator
            }
        }
        return str
    }
}