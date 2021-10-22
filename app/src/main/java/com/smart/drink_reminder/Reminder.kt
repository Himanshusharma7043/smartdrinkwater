package com.smart.drink_reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.smart.drink_reminder.Services.NetworkState
import com.smart.drink_reminder.Services.NotificationReceiver
import com.smart.drink_reminder.Services.PermanentNotification
import java.util.*

class Reminder : AppCompatActivity(), View.OnClickListener {
    lateinit var toolbar: Toolbar
    lateinit var wakeuptime: TextView
    lateinit var sleeptime: TextView
    lateinit var intervalTime: TextView
    lateinit var wakesleepText: TextView
    lateinit var reminderintervalText: TextView
    lateinit var everyText: TextView
    lateinit var minText: TextView
    lateinit var reminderfuthertext: TextView
    lateinit var futherText: TextView
    lateinit var notificationText: TextView
    lateinit var soundText: TextView
    lateinit var soundtypText: TextView
    lateinit var vibrationText: TextView
    lateinit var soundType: TextView
    var notificationManager: NotificationManager? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    val MyPREFERENCES = "DrinkWater"
    lateinit var furtherReminder: SwitchCompat
    lateinit var permanent_notification: SwitchCompat
    lateinit var sound: SwitchCompat
    lateinit var vibration: SwitchCompat
    var radioText: String = "Water"
    var index: Int? = null
    var reminderCheck: Boolean = true
    @SuppressLint("SetTextI18n", "ShortAlarm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

    }

    private fun init() {
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        val getTheme =
            sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(getTheme)
        val getlag = sharedPreferences.getString("language", "en")
        setLocale(R.layout.activity_main, getlag)
        setContentView(R.layout.activity_reminder)
        index = sharedPreferences.getInt("soundtype", 0)

        wakesleepText = findViewById(R.id.reminder_wakeupText)
        reminderintervalText = findViewById(R.id.reminder_intervalText)
        everyText = findViewById(R.id.everyText)
        minText = findViewById(R.id.minText)
        reminderfuthertext = findViewById(R.id.reminder_furtherText)
        futherText = findViewById(R.id.furtherText)
        notificationText = findViewById(R.id.notificationText)
        soundText = findViewById(R.id.soundText)
        soundtypText = findViewById(R.id.soundTypeText)
        vibrationText = findViewById(R.id.vibrationText)
        toolbar = findViewById(R.id.reminder_toolbar)
        wakeuptime = findViewById(R.id.reminder_setWakeup)
        sleeptime = findViewById(R.id.reminder_setSleep)
        intervalTime = findViewById(R.id.reminder_intervalSet)
        soundType = findViewById(R.id.notificationSound_type)
        furtherReminder = findViewById(R.id.reminder_furtherSwitch)
        permanent_notification = findViewById(R.id.manageNotificationSwitch)
        sound = findViewById(R.id.notificationSound)
        vibration = findViewById(R.id.notificationVibrationSwitch)

        wakeuptime.text = sharedPreferences.getString("wakeupTime", "8:00 AM")
        sleeptime.text = sharedPreferences.getString("sleepTime", "10:00 PM")
        intervalTime.text = sharedPreferences.getInt("intervalTime", 90).toString()
        soundType.text = sharedPreferences.getString("soundName", "water")
        furtherReminder.isChecked = sharedPreferences.getBoolean("furtherReminder", true)
        permanent_notification.isChecked =
            sharedPreferences.getBoolean("permanent_notification", false)

        sound.isChecked = sharedPreferences.getBoolean("sound", true)

        vibration.isChecked = sharedPreferences.getBoolean("vibration", true)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        wakeuptime.setOnClickListener() {
            puttime(wakeuptime)
        }
        sleeptime.setOnClickListener() {
            puttime(sleeptime)
        }

        intervalTime.setOnClickListener(this)
        soundType.setOnClickListener(this)
        furtherReminder.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "ONN", Toast.LENGTH_SHORT).show()
                setPreferencesSwitch("furtherReminder", true)
            } else {
                setPreferencesSwitch("furtherReminder", false)
                Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show()
            }
        }
        permanent_notification.setOnCheckedChangeListener { buttonView, isChecked ->
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (isChecked) {
                notificationManager!!.cancelAll()
                Toast.makeText(this, "ONN", Toast.LENGTH_SHORT).show()
                setPreferencesSwitch("permanent_notification", true)
                startService()
            } else {
                setPreferencesSwitch("permanent_notification", false)
                Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show()
                NotificationManagerCompat.from(this).cancel("CHANNEL_P", 0)
                notificationManager!!.cancelAll()
                stopService()
            }
        }
        sound.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "ONN", Toast.LENGTH_SHORT).show()
                setPreferencesSwitch("sound", true)
                setPreferencesTime("channelID", "channelid_5")
            } else {
                setPreferencesSwitch("sound", false)
                Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show()
                setPreferencesTime("channelID", "channelid_6")
            }
        }
        vibration.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "ONN", Toast.LENGTH_SHORT).show()
                setPreferencesSwitch("vibration", true)
            } else {
                setPreferencesSwitch("vibration", false)
                Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show()
            }
        }
        if (sharedPreferences.getBoolean("reminderSwitch", true)) {
            setvisibiitylayout(true)
        } else {
            setvisibiitylayout(false)
        }
        sendnotification()
    }

    private fun setPreferencesSound(s1: Int, s3: String) {
        val editor = sharedPreferences.edit()
        editor.putInt("soundtype", s1)
        editor.putString("soundName", s3)
        editor.apply()
    }

    private fun setPreferencesSwitch(name: String, check: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(name, check)
        editor.apply()
    }

    private fun setPreferencesTime(name: String, check: String) {
        val editor = sharedPreferences.edit()
        editor.putString(name, check)
        editor.apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reminder_switch -> {
                Toast.makeText(this, "Reminder switch", Toast.LENGTH_LONG).show()
                true
            }
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            else -> false
        }

    }
    @SuppressLint("WrongConstant")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.reminder_menu, menu)
        val menuItem: MenuItem = menu!!.findItem(R.id.reminder_switch)
        val reminderSwitch: SwitchCompat = menuItem.actionView as SwitchCompat
        val mPrefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
        Log.e("reminderSwitch", "" + mPrefs.getBoolean("reminderSwitch", true))

        reminderSwitch.isChecked = mPrefs.getBoolean("reminderSwitch", true)
        reminderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "ONN", Toast.LENGTH_SHORT).show()
                setvisibiitylayout(true)
                val editor = sharedPreferences.edit()
                editor.putBoolean("reminderSwitch", true)
                editor.apply()

            } else {
                setvisibiitylayout(false)
                Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show()
                val editor = sharedPreferences.edit()
                editor.putBoolean("reminderSwitch", false)
                editor.apply()
            }
        }
        return true
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setvisibiitylayout(check: Boolean) {
        if (check) {
            wakesleepText.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            wakeuptime.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            reminderintervalText.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            reminderfuthertext.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            notificationText.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            everyText.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            minText.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            futherText.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            soundText.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            soundtypText.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            vibrationText.setTextColor(ContextCompat.getColor(this, R.color.textColour))
            wakeuptime.setTextColor(ContextCompat.getColor(this, R.color.SteelBlue))
            intervalTime.setTextColor(ContextCompat.getColor(this, R.color.SteelBlue))
            sleeptime.setTextColor(ContextCompat.getColor(this, R.color.SteelBlue))
            soundType.setTextColor(ContextCompat.getColor(this, R.color.SteelBlue))
            Log.e("true", "true")
        } else {
            wakesleepText.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            reminderintervalText.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            everyText.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            minText.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            reminderfuthertext.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            futherText.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            notificationText.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            soundText.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            soundtypText.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            vibrationText.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            wakeuptime.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            intervalTime.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            sleeptime.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            soundType.setTextColor(ContextCompat.getColor(this, R.color.Darkset))
            Log.e("false", "false")

        }

        furtherReminder.isEnabled = check
        sound.isEnabled = check
        vibration.isEnabled = check
        wakeuptime.isEnabled = check
        intervalTime.isEnabled = check
        sleeptime.isEnabled = check
        soundType.isEnabled = check
        reminderCheck = check
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this, MainActivity::class.java))
            return true
        }
        return false
    }
    @SuppressLint("DefaultLocale")
    private fun puttime(settime: TextView?) {
        val c: Calendar = Calendar.getInstance()
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
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
                    val time: String = settime.text.toString()
                    if (settime.id == R.id.reminder_setWakeup) {
                        Toast.makeText(this, "reminder_setWakeup :" + time, Toast.LENGTH_SHORT)
                            .show()
                        setPreferencesTime("wakeupTime", time)
                    } else {
                        Toast.makeText(this, "reminder_setSleep:" + time, Toast.LENGTH_SHORT).show()
                        setPreferencesTime("sleepTime", time)
                    }

                }
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }

    fun startService() {
        if (sharedPreferences.getInt(
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
                this, sharedPreferences.getInt(
                    "soundtype",
                    R.raw.bubble
                )
            )
            mp.start()
        }
        setPreferencesSwitch("start", false)
        val serviceIntent = Intent(this, PermanentNotification::class.java)
        // serviceIntent.putExtra("inputExtra", input)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, PermanentNotification::class.java)
        stopService(serviceIntent)
    }

    fun sendnotification() {
        if (sharedPreferences.getBoolean("reminderSwitch", true)) {
            Log.e("TAG", "Reminder Activity sendnotification: " )
            if (!sharedPreferences.getBoolean("permanent_notification", false)) {
                val intent1 = Intent(this, NotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent1,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val cal = Calendar.getInstance()
                cal.add(Calendar.MINUTE, sharedPreferences.getInt("intervalTime", 90))
//                cal.add(Calendar.SECOND,30)
                val intervalTIME: Long = (sharedPreferences.getInt("intervalTime", 90) * 6000).toLong()
                val am = this.getSystemService(ALARM_SERVICE) as AlarmManager
                am.setRepeating(
                    AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalTIME,
                    pendingIntent
                )
            }
        }

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
//        }
        this.setContentView(activity)

    }


    @SuppressLint("CommitPrefEdits")
    private fun putIntSharep(name: String, values: Int) {
        editor = sharedPreferences.edit()
        editor.putInt(name, values)
        editor.commit()
    }
    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        if (v == intervalTime) {
            var mmzero = false
            var hrzero = false
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.setinterval)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.interval_set, null)
            val hrpicker: com.shawnlin.numberpicker.NumberPicker =
                inflate.findViewById(R.id.hrPicker)
            val mpicker: com.shawnlin.numberpicker.NumberPicker = inflate.findViewById(R.id.mPicker)
            hrpicker.maxValue = 5
            hrpicker.minValue = 0
            hrpicker.value = sharedPreferences.getInt("hrpicker", 1)
            hrpicker.wrapSelectorWheel = true
            val pickerVals: Array<String> = arrayOf("00", "10", "20", "30", "40", "50")
            mpicker.displayedValues = pickerVals
            mpicker.maxValue = pickerVals.size - 1
            mpicker.minValue = 0
            mpicker.value = sharedPreferences.getInt("mpicker", 3)
            mpicker.wrapSelectorWheel = true
            if (hrpicker.value == 0 && mpicker.value == 0) {
                mpicker.value = 3
                hrpicker.value = 1
            }
            hrpicker.setOnValueChangedListener { picker, oldVal, newVal ->
                if (newVal == 0 && mmzero) {
                    Toast.makeText(this, "Set proper Interval ", Toast.LENGTH_SHORT).show()
                    hrpicker.value = 1
                }
                hrzero = newVal == 0
            }
            mpicker.setOnValueChangedListener { picker, oldVal, newVal ->
                if (newVal == 0 && hrzero) {
                    Toast.makeText(this, "Set proper Interval ", Toast.LENGTH_SHORT).show()
                    mpicker.value = 1
                }
                mmzero = newVal == 0
            }
            builder.setPositiveButton("Ok", null)
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                    dialog.cancel()
                }
            builder.setView(inflate)
            builder.setCancelable(false)
            val dialog: AlertDialog = builder.create()

            dialog.show()
            val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener() {
                val hours: Int = Integer.parseInt(hrpicker.value.toString())
                val i: Int = Integer.parseInt(mpicker.value.toString())
                val minutes: Int = Integer.parseInt(pickerVals[i])


                    if (hours == 0) {
                        intervalTime.text = minutes.toString()
                        val editor = sharedPreferences.edit()
                        editor.putInt("intervalTime", minutes)
                        editor.apply()
                        //Log.e()
                    } else {
                        val interval: Int = (hours * 60) + minutes
                        intervalTime.text = interval.toString()
                        val editor = sharedPreferences.edit()
                        editor.putInt("intervalTime", interval)
                        editor.apply()
                    }
                    val editor = sharedPreferences.edit()
                    editor.putInt("hrpicker", hrpicker.value)
                    editor.putInt("mpicker", mpicker.value)
                    editor.apply()
                    Log.e("intervalTime", "" + sharedPreferences.getInt("intervalTime", 0))
                    dialog.cancel()

            }
        } else if (v == soundType) {
            val soundSelectID = sharedPreferences.getInt("soundSelectID", R.id.waterSound)
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle(R.string.NotificationSound)
            val inflater = LayoutInflater.from(this)
            val inflate: View = inflater.inflate(R.layout.sound_type, null)
            val radioGroup: RadioGroup? = inflate.findViewById(R.id.soundType)
            builder.setView(inflate)
            radioGroup?.check(soundSelectID)
            radioGroup?.setOnCheckedChangeListener { group, checkedId ->
                val radioButton = group
                    .findViewById<View>(checkedId) as RadioButton
                radioText = radioButton.text.toString()
                Log.e("TAG", "radioText:$radioText ")
                when (checkedId) {
                    R.id.waterSound -> {
                        val mp: MediaPlayer = MediaPlayer.create(this, R.raw.waterglass)
                        mp.start()
                    }
                    R.id.dewDropSound -> {
                        val mp: MediaPlayer = MediaPlayer.create(this, R.raw.waterdrop)
                        mp.start()
                    }
                    R.id.bubblesSound
                    -> {
                        val mp: MediaPlayer = MediaPlayer.create(this, R.raw.bubble)
                        mp.start()
                    }
                    R.id.systemDefault -> {
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
                    }
                }
            }

            builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                when (radioText) {
                    "Water" -> {
                        soundType.text = "Water"
                        putIntSharep("soundSelectID", R.id.waterSound)
                        setPreferencesSound(R.raw.waterglass, "Water")
                        setPreferencesTime("channelID", "channelid_1")
                    }
                    "Dew Drop" -> {
                        putIntSharep("soundSelectID", R.id.dewDropSound)
                        soundType.text = "Dew Drop"
                        setPreferencesSound(R.raw.waterdrop, "Dew Drop")
                        setPreferencesTime("channelID", "channelid_2")
                    }
                    "Bubbles"
                    -> {
                        putIntSharep("soundSelectID", R.id.bubblesSound)

                        soundType.text = "Bubbles"
                        setPreferencesSound(R.raw.bubble, "Bubbles")
                        setPreferencesTime("channelID", "channelid_3")
                    }
                    "System default" -> {
                        putIntSharep("soundSelectID", R.id.systemDefault)
                        soundType.text = "System Default"
                        setPreferencesSound(3, "System Default")
                        setPreferencesTime("channelID", "channelid_4")
                    }
                }
                Toast.makeText(this, "" + radioText, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun putBooleanShareP(name: String, value: Boolean) {
        editor = sharedPreferences.edit()
        editor.putBoolean(name, value)
        editor.commit()
    }


}
