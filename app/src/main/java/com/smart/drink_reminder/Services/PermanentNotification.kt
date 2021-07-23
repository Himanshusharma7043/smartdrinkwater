package com.smart.drink_reminder.Services

import android.app.*
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.smart.drink_reminder.MainActivity
import com.smart.drink_reminder.R

class PermanentNotification : Service() {

//    override fun onCreate() {
//        super.onCreate()
//    }

    lateinit var mPrefs: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mPrefs = this.getSharedPreferences(
            "DrinkWater", Context.MODE_PRIVATE
        )
        val totaldrink = mPrefs.getInt("totaldrink", 0)
        val getinput = mPrefs.getInt("dailygoal", 3000)
        val getgoaltype = mPrefs.getString("goaltype", "ml")!!
        val setprogress: Int = mPrefs.getFloat("progress", 0f).toInt()
        val CHANNEL_ONE_ID = "CHANNELP"
        val CHANNEL_ONE_NAME = "Drink water"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel: NotificationChannel?
            notificationChannel = NotificationChannel(
                CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.setShowBadge(true)
            notificationChannel.setSound(null,null)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }
        val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ONE_ID)
        val notification = notificationBuilder
            .setOngoing(true)
            .setChannelId(CHANNEL_ONE_ID)
            .setContentTitle("Time to drink")
            .setContentText("$totaldrink/$getinput$getgoaltype")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(icon)
            .setAutoCancel(false)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setProgress(100, setprogress, false)
            .setSound(null)
            .build()
        val v: Vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(500)
        }
        notification.flags = Notification.FLAG_ONGOING_EVENT
        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notification.flags = Notification.FLAG_ONGOING_EVENT
        notification.contentIntent =
            PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)

        startForeground(101, notification)

        return START_STICKY
    }



    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
