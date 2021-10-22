package com.smart.drink_reminder.Services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.smart.drink_reminder.MainActivity
import com.smart.drink_reminder.R

class NotificationReceiver : BroadcastReceiver() {
    var MID = 1
    lateinit var mPrefs: SharedPreferences
    @SuppressLint("ResourceAsColor")
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            mPrefs = context.getSharedPreferences(
                "DrinkWater", Context.MODE_PRIVATE
            )
            //val time = System.currentTimeMillis()
            Log.e("TAG", "Notification receiver onReceive: ")
            val setprogress: Int = mPrefs.getFloat("progress", 0f).toInt()
            val CHANNEL_ID: String = mPrefs.getString("channelID", "channelid_1").toString()
            val totaldrink = mPrefs.getInt("totaldrink", 0)
            val getinput = mPrefs.getInt("dailygoal", 3000)
            val getgoaltype = mPrefs.getString("goaltype", "ml")!!
            if (mPrefs.getBoolean("reminderSwitch", true)) {
    //            if (!mPrefs.getBoolean("permanent_notification", false)) {
                Log.e("TAG", "Notification receiver : Notification Loaded")
                val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                val ii = Intent(context, MainActivity::class.java)
                val pendingIntent =
                    PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT)
                val bigText = NotificationCompat.BigTextStyle()
                bigText.setBigContentTitle("Time to drink")
                mBuilder.setContentIntent(pendingIntent)
                mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                mBuilder.setContentTitle("Time to drink")
                mBuilder.setContentText("$totaldrink/$getinput$getgoaltype")
                mBuilder.setStyle(bigText)
                mBuilder.setLights(R.color.Blue, 300, 1000)
                mBuilder.setProgress(100, setprogress, false)
                if (mPrefs.getBoolean("sound", true)) {
                    if (mPrefs.getInt("soundtype", 1) == 3) {
                        Log.e("call", "soundtype=3")
                        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    } else {
                        mBuilder.setSound(
                            Uri.parse(
                                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.applicationContext.packageName + "/" + mPrefs.getInt(
                                    "soundtype",
                                    R.raw.bubble
                                )
                            )
                        )
                        Log.e("call", "soundtype")
                    }
                    Log.e("call", "sound")
                } else {
                    mBuilder.setSound(null)
                    mBuilder.setNotificationSilent()
                    Log.e("call", "sound false")
                }
                if (mPrefs.getBoolean("vibration", true)) {
                    val v: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        v.vibrate(500)
                    }
                    mBuilder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    Log.e("vibration", "true")
                }
                Log.e("vibration", "false")
                val mNotificationManager: NotificationManager =
                    context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        "DrinkWater",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    if (mPrefs.getBoolean("sound", true)) {
                        if (mPrefs.getBoolean("sound", true)) {
                            if (mPrefs.getInt("soundtype", 1) == 3) {
                                val audioAttributes = AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                    .build()
                                Log.e("call", "soundtype=3")
                                channel.setSound(
                                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                                    audioAttributes
                                )
                            } else {
                                val audioAttributes = AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                    .build()
                                channel.setSound(
                                    Uri.parse(
                                        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.applicationContext.packageName + "/" + mPrefs.getInt(
                                            "soundtype",
                                            R.raw.bubble
                                        )
                                    ), audioAttributes
                                )
                                Log.e("call", "soundtype")
                            }
                            Log.e("call", "sound")
                        }

                        Log.e("call", "channel sound")
                    } else {
                        channel.setSound(null, null)
                        Log.e("call", "channel sound false")

                    }
                    mNotificationManager.createNotificationChannel(channel)
                    mBuilder.setChannelId(CHANNEL_ID)
                } else {
                    Log.e("Call", "AlarmReceiver")
                }
                mNotificationManager.notify(0, mBuilder.build())
    //            }
            }
        } catch (e: Exception) {
            Log.e("TAG", "onReceive: "+e.message )
        }
    }

}
