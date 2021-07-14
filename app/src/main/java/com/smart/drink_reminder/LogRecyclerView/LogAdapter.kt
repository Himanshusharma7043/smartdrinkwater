package com.smart.drink_reminder.LogRecyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.smart.drink_reminder.Database.DatabaseHandler
import com.smart.drink_reminder.R
import java.text.SimpleDateFormat
import java.util.*

class LogAdapter(
    private var context: Context,
    private var log_list: ArrayList<String>,
    private var timelist: ArrayList<String>,
    private var drawable_list: ArrayList<String>
) :
    RecyclerView.Adapter<LogAdapter.ViewHolder>() {
    lateinit var DB: DatabaseHandler
    private var listData: ArrayList<String> = log_list
    private var timeData: ArrayList<String> = timelist
    private var drawableList: ArrayList<String> = drawable_list
    lateinit var editor: SharedPreferences.Editor
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v: View = inflater.inflate(R.layout.log_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = log_list[position]
        val time = timeData[position]
        val drawable = drawableList[position]
        val name: String = when (drawable.toInt()) {
            R.drawable.water_glass -> "Water"
            R.drawable.tea -> "Tea"
            R.drawable.coffee -> "Coffee"
            R.drawable.milk -> "Milk"
            R.drawable.fruitjuice -> "Fruit juice"
            R.drawable.sodecan -> "Soda"
            R.drawable.beermag, -> "Beer"
            R.drawable.enerydrink,-> "Energy Drink"
            R.drawable.lemonade -> "Lemonade"
            else -> "Water"
        }
        holder.logName.text = name
        holder.drink_water.text = list
        holder.drink_Water_time.text = time
        holder.log_logo.setImageResource(drawable.toInt())
        holder.delete_log.setOnClickListener() {
            deleteItem(position)
        }
    }
    @SuppressLint("SimpleDateFormat", "CommitPrefEdits")
    fun deleteItem(index: Int) {
        try {
            val sdfdate = SimpleDateFormat("yyyy-MM-dd")
            val getdate: String = sdfdate.format(Date())
            val date: Date = sdfdate.parse(getdate)!!
            DB = DatabaseHandler(context)
            val mPrefs = context.getSharedPreferences(
                "DrinkWater", Context.MODE_PRIVATE
            )
            var totaldrink = mPrefs.getInt("totaldrink", 0)
            var progrvalue = mPrefs.getFloat("progress", 0f)
            val deleteLog = log_list[index]
            Log.e("TAG", "deleteItem: $deleteLog")
            val delnum: Int = deleteLog.replace("[\\D]".toRegex(), "").toInt()
            val total: Int = mPrefs.getInt("dailygoal", 3000)
            Log.e("TAG", "deleteItem:delnum:$delnum ")
            listData.removeAt(index)
            timeData.removeAt(index)
            drawableList.removeAt(index)
            totaldrink -= delnum
            val addpercent: Float = ((delnum * 100) / total).toFloat()
            progrvalue -= addpercent
            editor = mPrefs.edit()
            editor.putBoolean("deletePerform", true)
            editor.putInt("deleteDrink", delnum)
            editor.putInt("totaldrink", totaldrink)
            editor.putFloat("progress", progrvalue)
            editor.apply()
            val putLog: String = convertArrayToString(listData)
            val putTime: String = convertArrayToString(timelist)
            val putDrawable: String = convertArrayToString(drawableList)
            val logupdate = LogData(
                date.time,
                putDrawable,
                putLog,
                putTime,
                totaldrink,
                mPrefs.getInt("dailygoal", 3000)
            )
            notifyDataSetChanged()
            notifyItemRemoved(index)
            DB.updateContact(logupdate)
            val intent = Intent("custom-message")
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            Log.e("TAG", "listData: $listData")
        } catch (e: Exception) {
            Log.e("Error", "deleteItem:${e.message} ")
        }
    }

    fun convertArrayToString(array: ArrayList<String>): String {
        val strSeparator = ","
        var str = ""
        for (i in array.indices) {
            str += array[i]

            if (i < array.size - 1) {
                str += strSeparator
            }
        }
        return str
    }

    override fun getItemCount(): Int {
        return log_list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var drink_water: TextView = itemView.findViewById(R.id.drink_water_ml)
        var logName: TextView = itemView.findViewById(R.id.logName)
        var drink_Water_time: TextView = itemView.findViewById(R.id.drink_water_time)
        var delete_log: ImageView = itemView.findViewById(R.id.delete_log)
        var log_logo: ImageView = itemView.findViewById(R.id.log_logo)
    }
}