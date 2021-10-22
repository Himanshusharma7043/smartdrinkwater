package com.smart.drink_reminder

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.smart.drink_reminder.Database.DatabaseHandler
import com.smart.drink_reminder.Services.NetworkState
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
@SuppressLint("SimpleDateFormat")
class Statistics : AppCompatActivity() {
    lateinit var barChart: BarChart
    // variable for our bar data.
    lateinit var barData: BarData
    lateinit var pieChart: PieChart
    lateinit var toolbar: Toolbar
    lateinit var datePickertext: TextView
    lateinit var rightnav: ImageButton
    lateinit var leftnav: ImageButton
    lateinit var sunIMG: ImageView
    lateinit var monIMG: ImageView
    lateinit var tueIMG: ImageView
    lateinit var wedIMG: ImageView
    lateinit var thuIMG: ImageView
    lateinit var friIMG: ImageView
    lateinit var satIMG: ImageView
    lateinit var totalIMG: TextView
    lateinit var drinkTapAvgText: TextView
    lateinit var averageCompletionText: TextView
    lateinit var averagePercent: TextView
    lateinit var totalChain: TextView
    lateinit var spinner: Spinner
    lateinit var mPrefs: SharedPreferences
    var xaxisList: List<String> = java.util.ArrayList()
    var getspin: String? = null
    val c1 = Calendar.getInstance()
    val newCalendar = Calendar.getInstance()
    val tempcal = Calendar.getInstance()
    val tempcalleft = Calendar.getInstance()
    val weekformat = SimpleDateFormat("dd MMM")
    lateinit var entries: List<LegendEntry>
    val monthFormat = SimpleDateFormat("MMM yyyy")
    val yearFormat = SimpleDateFormat("yyyy")
    val newDate = Calendar.getInstance()
    var spinerdata = arrayOf("Weekly report", "Monthly report", "Yearly report", "Lifetime report")
//    var spinerdata = arrayOf(getString(R.string.weeklyreport), getString(R.string.monthlyreport), getString(R.string.yearlyreport), getString(R.string.lifetimereprot))
    var yearStats = arrayOf(
        "JAN",
        "FEB",
        "MAR",
        "APR",
        "MAY",
        "JUN",
        "JUL",
        "AUG",
        "SEP",
        "OCT",
        "NOV",
        "DEC"
    )
    var weekStats = arrayOf("SUN", "MON", "TUS", "WED", "THU", "FRI", "SAT")
    private var pieLableName = ArrayList<String>()
    private var colorList = ArrayList<Int>()
    private var weekMili = ArrayList<Long>()
    private var monthstats = ArrayList<String>()
    private var weekData = ArrayList<Float>()
    private var piechartvalue = ArrayList<Float>()
    private var monthMili = ArrayList<Long>()
    private var monthData = ArrayList<Float>()
    private var yearData = ArrayList<Float>()
    private var lifeTimeData = ArrayList<Float>()
    var lifestats = arrayOf("2021", "2022", "2023")
    var currentStats: String? = null
    var barDataSet: BarDataSet? = null
    lateinit var averageVolumeValues: LinearLayout
    lateinit var clstatistics_spinner: ConstraintLayout
    lateinit var averageCompletionValues: LinearLayout
    lateinit var drinkFrequencyValues: LinearLayout
    val MyPREFERENCES = "DrinkWater"
    lateinit var barEntriesArrayList: ArrayList<BarEntry>
    val sdfdate = SimpleDateFormat("yyyy-MM-dd")
    lateinit var DB: DatabaseHandler
    var waterLogTotal = 0f
    var teaLogTotal = 0f
    var coffeeLogTotal = 0f
    var milkLogTotal = 0f
    var fruitLogTotal = 0f
    var sodaLogTotal = 0f
    var beerLogTotal = 0f
    var energyLogTotal = 0f
    var lemonadeLogTotal = 0f
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPrefs = getSharedPreferences(
            MyPREFERENCES, Context.MODE_PRIVATE
        )
        val getlag = mPrefs.getString("language", "en")
        setLocale(R.layout.activity_main, getlag)
        setContentView(R.layout.activity_statistics)
        DB = DatabaseHandler(this)
        for (i in 1..30) {
            if (i % 2 == 0) {
                monthstats.add("" + i)
            } else {
                monthstats.add(" ")
            }
        }
        Log.e("TAG", "monthstats:$monthstats ")
        barChart = findViewById(R.id.idBarChart)
        pieChart = findViewById(R.id.idpiechart)
        rightnav = findViewById(R.id.date_right)
        leftnav = findViewById(R.id.date_left)
        spinner = findViewById(R.id.statistics_spinner)
        datePickertext = findViewById(R.id.date_picker)
        clstatistics_spinner = findViewById(R.id.clstatistics_spinner)
        averageVolumeValues = findViewById(R.id.averageVolumeValues)
        averageCompletionValues = findViewById(R.id.averageCompletionValues)
        drinkFrequencyValues = findViewById(R.id.drinkFrequencyValues)
        sunIMG = findViewById(R.id.sunIMG)
        monIMG = findViewById(R.id.monIMG)
        tueIMG = findViewById(R.id.tueIMG)
        wedIMG = findViewById(R.id.wedIMG)
        thuIMG = findViewById(R.id.thuIMG)
        friIMG = findViewById(R.id.friIMG)
        satIMG = findViewById(R.id.satIMG)
        totalIMG = findViewById(R.id.totalIMG)
        totalChain = findViewById(R.id.totalChain)

        averageCompletionText = findViewById(R.id.averageCompletionText)
        averagePercent = findViewById(R.id.averagePercent)
        drinkTapAvgText = findViewById(R.id.drinkTapAverageText)

        toolbar = findViewById(R.id.statistics_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val adapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.spinner_text, spinerdata)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
        c1.time = newCalendar.time
        c1.set(Calendar.DAY_OF_WEEK, 7)
        tempcalleft.time = c1.time
        c1.set(Calendar.DAY_OF_WEEK, 7)
        tempcal.time = c1.time
        Log.e("TAG", "goalAchieve: " + mPrefs.getInt("goalAchieve", 0))
        Log.e("TAG", "activeDays: " + mPrefs.getInt("activeDays", 1))

        averageCompletionText.text =
            (mPrefs.getInt("goalAchieve", 0) / mPrefs.getInt("activeDays", 1)).toString()
        averagePercent.text =
            (mPrefs.getInt("grandPercent", 0) / mPrefs.getInt("activeDays", 1)).toString()
        drinkTapAvgText.text =
            (mPrefs.getInt("totalTAP", 0) / mPrefs.getInt("activeDays", 1)).toString()

        datePickertext.setOnClickListener() {
            val StartTime = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    newDate[year, monthOfYear] = dayOfMonth
                    //datePickertext.setText(sdf.format(newDate.time))
                    c1.time = newDate.time
                    when (currentStats) {
                        "week" -> {
                            setTimeVisiblity(true)
                            c1.set(Calendar.DAY_OF_WEEK, 1).toString()
                            val startDay: String = weekformat.format(c1.time)
                            c1.set(Calendar.DAY_OF_WEEK, 2).toString()
                            Log.e("getnext", "" + c1.time)
                            c1.set(Calendar.DAY_OF_WEEK, 7)
                            val endDay: String = weekformat.format(c1.time)
                            datePickertext.text = ("$startDay - $endDay")
                            val getdate: Long = getmilis(sdfdate.format(newDate.time))
                            setweek(getdate)
                        }
                        "month" -> {
                            setTimeVisiblity(true)
                            datePickertext.text = (monthFormat.format(newDate.time))
                            val num = newDate.getActualMaximum(Calendar.DAY_OF_MONTH)
                            val getdate: Long = getmilis(sdfdate.format(newDate.time))
                            setmonth(getdate, num)
                            Log.e("num", "" + num)
                        }
                        "year" -> {
                            setTimeVisiblity(true)
                            datePickertext.text = (yearFormat.format(newDate.time))
                            val getdate: Long = getmilis(sdfdate.format(newDate.time))
                            setyear(getdate)
                        }
                        "lifetime" -> {
                            setTimeVisiblity(false)
                            datePickertext.text = ("Life Time")
                        }
                    }
                }, newCalendar[Calendar.YEAR], newCalendar[Calendar.MONTH],
                newCalendar[Calendar.DAY_OF_MONTH]
            ).show()
            c1.set(Calendar.DAY_OF_WEEK, 7)
            tempcalleft.time = c1.time
            c1.set(Calendar.DAY_OF_WEEK, 7)
            tempcal.time = c1.time
        }
        barChart.axisRight.isEnabled = false
        barChart.setDrawValueAboveBar(false)
        barChart.setTouchEnabled(false)
        val y: YAxis = barChart.axisLeft
        y.axisMaximum = 100f
        y.axisMinimum = 0f
        y.granularity = 1f
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        val leg: Legend = barChart.legend
        leg.isEnabled = false
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                getspin = parent?.selectedItem.toString()
                if (getspin.equals("Weekly report")) {
                    setTimeVisiblity(true)
                    setaxis(7, weekStats.toList())
                    c1.set(Calendar.DAY_OF_WEEK, 1).toString()
                    val startDay: String = weekformat.format(c1.time)
                    c1.set(Calendar.DAY_OF_WEEK, 7)
                    val endDay: String = weekformat.format(c1.time)
                    datePickertext.text = ("$startDay - $endDay")
                    currentStats = "week"
                    val getdate: Long = getmilis(sdfdate.format(c1.time))
                    setweek(getdate)
                } else if (getspin.equals("Monthly report")) {
                    setTimeVisiblity(true)
                    Log.e("TAG", "monthstats: " + monthstats.size)
                    setaxis(30, monthstats)
                    currentStats = "month"
                    datePickertext.text = (monthFormat.format(newDate.time))
                    val num = newDate.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val getdate: Long = getmilis(sdfdate.format(newDate.time))
                    setmonth(getdate, num)
                } else if (getspin.equals("Yearly report")) {
                    setTimeVisiblity(true)
                    currentStats = "year"
                    setaxis(12, yearStats.toList())
                    datePickertext.text = (yearFormat.format(newDate.time))
                    val getdate: Long = getmilis(sdfdate.format(newDate.time))
                    setyear(getdate)

                } else if (getspin.equals("Lifetime report")) {
                    setTimeVisiblity(false)
                    xaxisList = listOf()
                    currentStats = "lifetime"
                    setaxis(3, lifestats.toList())
                    datePickertext.text = ("Life Time")
                    val getdate: Long = getmilis(sdfdate.format(newDate.time))
                    setLifeTime(getdate)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        clstatistics_spinner.setOnClickListener() {
            spinner.performClick()
        }
        rightnav.setOnClickListener() {
            when (currentStats) {
                "week" -> {
                    setTimeVisiblity(true)
                    tempcal.set(Calendar.DAY_OF_WEEK, 7).toString()
                    tempcal.add(Calendar.DATE, 1)
                    val cal = Calendar.getInstance()
                    cal.time = tempcal.time
                    cal.set(Calendar.DAY_OF_WEEK, 1).toString()
                    val getdate: Long = getmilis(sdfdate.format(cal.time))
                    setweek(getdate)
                    val startDay: String = weekformat.format(cal.time)
                    cal.set(Calendar.DAY_OF_WEEK, 7)
                    tempcal.time = cal.time
                    val endDay: String = weekformat.format(cal.time)
                    datePickertext.text = ("$startDay - $endDay")
                }
                "month" -> {
                    setTimeVisiblity(true)
                    newDate.add(Calendar.MONTH, 1) //Adds a day
                    Log.e(" Increment MONTH ", "" + monthFormat.format(newDate.time))
                    datePickertext.text = (monthFormat.format(newDate.time))
                    val num = newDate.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val getdate: Long = getmilis(sdfdate.format(newDate.time))
                    setmonth(getdate, num)
                }
                "year" -> {
                    setTimeVisiblity(true)
                    newDate.add(Calendar.YEAR, 1) //Adds a day
                    Log.e("Increment year ", "" + yearFormat.format(newDate.time))
                    datePickertext.text = (yearFormat.format(newDate.time))
                    val getdate: Long = getmilis(sdfdate.format(newDate.time))
                    setyear(getdate)
                }
                "lifetime" -> {
                    setTimeVisiblity(false)
                    datePickertext.text = ("Life Time")
                }
            }
        }
        leftnav.setOnClickListener() {
            when (currentStats) {
                "week" -> {
                    setTimeVisiblity(true)
                    tempcal.set(Calendar.DAY_OF_WEEK, 1).toString()
                    tempcal.add(Calendar.DATE, -1)
                    val cal = Calendar.getInstance()
                    cal.time = tempcal.time
                    val getdate: Long = getmilis(sdfdate.format(cal.time))
                    cal.set(Calendar.DAY_OF_WEEK, 1).toString()
                    setweek(getdate)
                    tempcal.time = cal.time
                    val startDay: String = weekformat.format(cal.time)
                    cal.set(Calendar.DAY_OF_WEEK, 7)
                    val endDay: String = weekformat.format(cal.time)
                    datePickertext.text = ("$startDay - $endDay")
                }
                "month" -> {
                    setTimeVisiblity(true)
                    newDate.add(Calendar.MONTH, -1) //Adds a day
                    Log.e(" Decrement MONTH ", "" + monthFormat.format(newDate.time))
                    datePickertext.text = (monthFormat.format(newDate.time))
                    val num = newDate.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val getdate: Long = getmilis(sdfdate.format(newDate.time))
                    setmonth(getdate, num)
                }
                "year" -> {
                    setTimeVisiblity(true)
                    newDate.add(Calendar.YEAR, -1) //Adds a day
                    Log.e("Decrement year ", "" + yearFormat.format(newDate.time))
                    datePickertext.text = (yearFormat.format(newDate.time))
                    val getdate: Long = getmilis(sdfdate.format(newDate.time))
                    setyear(getdate)
                }
                "lifetime" -> {
                    setTimeVisiblity(false)
                    datePickertext.text = ("Life Time")
                }
            }
        }

        setchaindata()
    }

    private fun setchaindata() {
        DB = DatabaseHandler(this)
        val listMil = ArrayList<Long>()
        val chainData = ArrayList<Float>()
        listMil.clear()
        chainData.clear()
        val calendar = Calendar.getInstance()
        val getdate: Long = getmilis(sdfdate.format(calendar.time))
        val weekCal = Calendar.getInstance()
        weekCal.timeInMillis = getdate
        for (i in 1..7) {
            weekCal.set(Calendar.DAY_OF_WEEK, i).toString()
            Log.e("TAG", "Date: " + weekformat.format(weekCal.time))
            calendar.time = weekCal.time
            listMil.add(calendar.timeInMillis)
        }
        Log.e("TAG", "listMil: $listMil")
        for (i in 0..6) {
            if (DB.checkIfRecordExist(listMil[i])) {
                val getOldRecord = DB.getonevalue(listMil[i])
                if (getOldRecord.totaldrink!! >= getOldRecord.dailygoal!!) {
                    chainData.add(100f)
                } else {
                    val percentFloat =
                        ((getOldRecord.totaldrink!! * 100) / getOldRecord.dailygoal!!).toFloat()
                    chainData.add(percentFloat)
                }

            } else {
                chainData.add(0f)
            }
        }
        Log.e("TAG", "setchaindata: $chainData")
        var totalCount = 0
        if (chainData[0] == 100f) {
            Log.e("TAG", "setchaindata: chainData[0]")
            sunIMG.setImageResource(R.drawable.check)
            totalCount++
        } else {
            sunIMG.setImageResource(R.drawable.cancel)
        }
        if (chainData[1] == 100f) {
            Log.e("TAG", "setchaindata: chainData[1]")
            monIMG.setImageResource(R.drawable.check)
            totalCount++
        } else {
            monIMG.setImageResource(R.drawable.cancel)
        }
        if (chainData[2] == 100f) {
            Log.e("TAG", "setchaindata: chainData[2]")
            tueIMG.setImageResource(R.drawable.check)
            totalCount++
        } else {
            tueIMG.setImageResource(R.drawable.cancel)
        }
        if (chainData[3] == 100f) {
            Log.e("TAG", "setchaindata: chainData[3]")
            wedIMG.setImageResource(R.drawable.check)
        } else {
            wedIMG.setImageResource(R.drawable.cancel)
        }
        if (chainData[4] == 100f) {
            Log.e("TAG", "setchaindata: chainData[4]")
            thuIMG.setImageResource(R.drawable.check)
            totalCount++
        } else {
            thuIMG.setImageResource(R.drawable.cancel)
        }
        if (chainData[5] == 100f) {
            Log.e("TAG", "setchaindata: chainData[5]")
            friIMG.setImageResource(R.drawable.check)
            totalCount++
        } else {
            friIMG.setImageResource(R.drawable.cancel)
        }
        if (chainData[6] == 100f) {
            Log.e("TAG", "setchaindata: chainData[6]")
            satIMG.setImageResource(R.drawable.check)
            totalCount++
        } else {
            satIMG.setImageResource(R.drawable.cancel)
        }
        totalIMG.text = totalCount.toString()
        totalChain.text = totalCount.toString()
    }

    private fun setpiechart() {
        val noOfEmp = ArrayList<Any>()
        var total = 0
        pieLableName.clear()
        colorList.clear()
        piechartvalue.clear()
        if (waterLogTotal != 0f) {
            pieLableName.add("Water")
            colorList.add(Color.rgb(64, 89, 128))
            piechartvalue.add(waterLogTotal)
            total += waterLogTotal.toInt()
        }
        if (teaLogTotal != 0f) {
            pieLableName.add("Tea")
            colorList.add(Color.rgb(149, 165, 124))
            piechartvalue.add(teaLogTotal)
            total += teaLogTotal.toInt()
        }
        if (coffeeLogTotal != 0f) {
            pieLableName.add("Coffee")
            colorList.add(Color.rgb(217, 184, 162))
            piechartvalue.add(coffeeLogTotal)
            total += coffeeLogTotal.toInt()
        }
        if (milkLogTotal != 0f) {
            pieLableName.add("Milk")
            colorList.add(Color.rgb(191, 134, 134))
            piechartvalue.add(milkLogTotal)
            total += milkLogTotal.toInt()
        }
        if (fruitLogTotal != 0f) {
            pieLableName.add("Fruit Jui")
            colorList.add(Color.rgb(179, 48, 80))
            piechartvalue.add(fruitLogTotal)
            total += fruitLogTotal.toInt()
        }
        if (sodaLogTotal != 0f) {
            pieLableName.add("Soda")
            colorList.add(Color.rgb(193, 37, 82))
            piechartvalue.add(sodaLogTotal)
            total += sodaLogTotal.toInt()
        }
        if (mPrefs.getBoolean("PURCHASE", false)) {
            if (beerLogTotal != 0f) {
                pieLableName.add("Beer")
                colorList.add(Color.rgb(183, 27, 92))
                piechartvalue.add(beerLogTotal)
                total += beerLogTotal.toInt()
            }
            if (energyLogTotal != 0f) {
                pieLableName.add("Energy Drink")
                colorList.add(Color.rgb(193, 37, 99))
                piechartvalue.add(energyLogTotal)
                total += energyLogTotal.toInt()
            }
            if (lemonadeLogTotal != 0f) {
                pieLableName.add("Lemonade")
                colorList.add(Color.rgb(122, 73, 56))
                piechartvalue.add(lemonadeLogTotal)
                total += lemonadeLogTotal.toInt()
            }
        }
        if (waterLogTotal == 0f && teaLogTotal == 0f && coffeeLogTotal == 0f && milkLogTotal == 0f && fruitLogTotal == 0f && sodaLogTotal == 0f && beerLogTotal == 0f && energyLogTotal == 0f && lemonadeLogTotal == 0f) {
            noOfEmp.add(PieEntry(100f, 0))
        } else {
            noOfEmp.clear()
            for (i in 0 until piechartvalue.size) {
                noOfEmp.add(PieEntry(piechartvalue[i], i))
            }
        }
        val dataSet = PieDataSet(noOfEmp as List<PieEntry>, "")
        val data = PieData(dataSet)
        pieChart.description.isEnabled = false
        pieChart.holeRadius = 45f
        pieChart.transparentCircleRadius = 45f
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.legend.isEnabled = false
        pieChart.isRotationEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.setDrawSlicesUnderHole(false)

        pieChart.data = data
        val legend: Legend = pieChart.legend
        legend.textColor = mPrefs.getInt("charttextcolor", Color.BLACK)
        val entries = ArrayList<LegendEntry>()
        if (waterLogTotal == 0f && teaLogTotal == 0f && coffeeLogTotal == 0f && milkLogTotal == 0f && fruitLogTotal == 0f && sodaLogTotal == 0f && beerLogTotal == 0f && energyLogTotal == 0f && lemonadeLogTotal == 0f) {
            dataSet.setColors(Color.GRAY)
            dataSet.valueTextColor = Color.TRANSPARENT
            legend.isEnabled = false
            pieChart.centerText = getString(R.string.total) + "\n0"
            pieChart.setCenterTextColor(mPrefs.getInt("charttextcolor", Color.BLACK))
        } else {
            pieChart.setCenterTextColor(mPrefs.getInt("charttextcolor", Color.BLACK))
            if (total >= 1000f) {
                //  val set=().toFloat()
                pieChart.centerText = getString(R.string.total)+"\n ${total / 1000} L"
            } else {
                pieChart.centerText = getString(R.string.total)+"\n$total ml"
            }
            legend.isEnabled = true
            for (i in 0 until pieLableName.size) {
                val entry = LegendEntry()
                entry.formColor = colorList[i]
                entry.label = pieLableName[i]
                entries.add(entry)
            }
            legend.setCustom(entries)
            dataSet.colors = colorList
            data.setValueTextColor(Color.BLACK)
            dataSet.valueLinePart1OffsetPercentage = 70f
            dataSet.valueLinePart1Length = .2f
            dataSet.valueLinePart2Length = .2f
            dataSet.valueTextColor = mPrefs.getInt("charttextcolor", Color.BLACK)
            dataSet.selectionShift = 5f
            dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            dataSet.sliceSpace = 2.5f
            pieChart.highlightValues(null)
        }
        pieChart.invalidate()
        pieChart.notifyDataSetChanged()
    }

    private fun setaxis(r: Int, xaxisList: List<String>) {
        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 6f
        xAxis.textColor = mPrefs.getInt("charttextcolor", Color.BLACK)
        xAxis.labelCount = r
        xAxis.valueFormatter = IndexAxisValueFormatter(xaxisList)
        barEntriesArrayList = ArrayList()
        barEntriesArrayList.clear()
        barChart.notifyDataSetChanged()
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

    private fun setTimeVisiblity(check: Boolean) {
        if (check) {
            rightnav.visibility = View.VISIBLE
            rightnav.isEnabled = true
            leftnav.visibility = View.VISIBLE
            leftnav.isEnabled = true
            datePickertext.isEnabled = true
        } else {
            rightnav.visibility = View.INVISIBLE
            rightnav.isEnabled = false
            leftnav.visibility = View.INVISIBLE
            leftnav.isEnabled = false
            datePickertext.isEnabled = false
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

    private fun setweek(mili: Long) {
        setZero()
        DB = DatabaseHandler(this)
        weekMili.clear()
        weekData.clear()
        val calendar = Calendar.getInstance()
        val weekCal = Calendar.getInstance()
        weekCal.timeInMillis = mili
        for (i in 1..7) {
            weekCal.set(Calendar.DAY_OF_WEEK, i).toString()
            calendar.time = weekCal.time
            weekMili.add(calendar.timeInMillis)
        }
        for (i in 0..6) {
            if (DB.checkIfRecordExist(weekMili[i])) {
                val getOldRecord = DB.getonevalue(weekMili[i])
                val getDrawables = convertStringToArray(getOldRecord.drawable!!)
                val getLogval = convertStringToArray(getOldRecord.logvalue!!)
                for (k in 0 until getDrawables.size) {
                    when (getDrawables[k].replace("[\\D]".toRegex(), "").toInt()) {
                        R.drawable.water_glass -> {
                            waterLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        R.drawable.tea -> {
                            teaLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        R.drawable.coffee -> {
                            coffeeLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()

                        }
                        R.drawable.milk -> {
                            milkLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()

                        }
                        R.drawable.fruitjuice -> {
                            fruitLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()

                        }
                        R.drawable.sodecan -> {
                            sodaLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        R.drawable.beermag -> {
                            beerLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        R.drawable.enerydrink -> {
                            energyLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        R.drawable.lemonade -> {
                            lemonadeLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        else -> {
                            Log.e("TAG", "setweek:null ")
                        }
                    }
                }
                if (getOldRecord.totaldrink!! >= getOldRecord.dailygoal!!) {
                    weekData.add(100f)
                } else {
                    val percentFloat =
                        ((getOldRecord.totaldrink!! * 100) / getOldRecord.dailygoal!!).toFloat()
                    weekData.add(percentFloat)
                }

            } else {
                weekData.add(0f)
            }
        }

        barEntriesArrayList = ArrayList()
        barEntriesArrayList.clear()

        for (i in 0..6) {
            barEntriesArrayList.add(BarEntry(i.toFloat(), weekData[i]))
        }
        barDataSet = BarDataSet(barEntriesArrayList as List<BarEntry>, "Drink Water")
        barData = BarData(barDataSet)
        barData.barWidth = 0.3f
        barChart.data = barData
        barDataSet!!.setDrawValues(false)
        barDataSet!!.valueTextSize = 6f
        barDataSet!!.setColors(Color.BLUE)
        barDataSet!!.valueTextColor = mPrefs.getInt("charttextcolor", Color.BLACK)
        barChart.axisLeft.textColor = mPrefs.getInt("charttextcolor", Color.BLACK)
        barChart.invalidate()
        barChart.animateY(2000)
        barChart.notifyDataSetChanged()
        setpiechart()
    }

    private fun setZero() {
        waterLogTotal = 0f
        teaLogTotal = 0f
        coffeeLogTotal = 0f
        milkLogTotal = 0f
        fruitLogTotal = 0f
        sodaLogTotal = 0f
        beerLogTotal = 0f
        energyLogTotal = 0f
        lemonadeLogTotal = 0f
    }

    private fun setmonth(mili: Long, max: Int) {
        setZero()
        val maxx = max - 1
        DB = DatabaseHandler(this)
        monthMili.clear()
        monthData.clear()
        val calendar = Calendar.getInstance()
        val weekCal = Calendar.getInstance()
        weekCal.timeInMillis = mili
        weekCal.set(Calendar.DAY_OF_MONTH, 1)
        for (i in 1..max) {
            calendar.time = weekCal.time
            monthMili.add(calendar.timeInMillis)
            weekCal.add(Calendar.DATE, 1)
        }
        Log.e("monthMili", "" + monthMili)
        for (i in 0..maxx) {
            if (DB.checkIfRecordExist(monthMili[i])) {
                val getOldRecord = DB.getonevalue(monthMili[i])
                val getDrawables = convertStringToArray(getOldRecord.drawable!!)
                val getLogval = convertStringToArray(getOldRecord.logvalue!!)
                for (k in 0 until getDrawables.size) {
                    when (getDrawables[k].replace("[\\D]".toRegex(), "").toInt()) {
                        R.drawable.water_glass -> {
                            waterLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        R.drawable.tea -> {
                            teaLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        R.drawable.coffee -> {
                            coffeeLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()

                        }
                        R.drawable.milk -> {
                            milkLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()

                        }
                        R.drawable.fruitjuice -> {
                            fruitLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()

                        }
                        R.drawable.sodecan -> {
                            sodaLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        R.drawable.beermag -> {
                            beerLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        R.drawable.enerydrink -> {
                            energyLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        R.drawable.lemonade -> {
                            lemonadeLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                        }
                        else -> {
                            Log.e("TAG", "setweek:null ")
                        }
                    }
                }
                if (getOldRecord.totaldrink!! >= getOldRecord.dailygoal!!) {
                    monthData.add(100f)
                } else {
                    val percentFloat =
                        ((getOldRecord.totaldrink!! * 100) / getOldRecord.dailygoal!!).toFloat()
                    monthData.add(percentFloat)
                }

            } else {
                monthData.add(0f)
            }
        }
        barEntriesArrayList = ArrayList()
        barEntriesArrayList.clear()
        for (i in 0..maxx) {
            barEntriesArrayList.add(BarEntry(i.toFloat(), monthData[i]))
        }
        barDataSet = BarDataSet(barEntriesArrayList as List<BarEntry>, "Drink Water")
        barData = BarData(barDataSet)
        barData.barWidth = 0.5f
        barChart.data = barData
        barDataSet!!.setDrawValues(false)
        barDataSet!!.valueTextSize = 6f
        barDataSet!!.setColors(Color.BLUE)
        barDataSet!!.valueTextColor = mPrefs.getInt("charttextcolor", Color.BLACK)
        barChart.axisLeft.textColor = mPrefs.getInt("charttextcolor", Color.BLACK)
        barChart.invalidate()
        barChart.animateY(2000)
        barChart.notifyDataSetChanged()
        setpiechart()
    }

    private fun setyear(mili: Long) {
        setZero()
        var values: Float
        var monthAverage: Float
        DB = DatabaseHandler(this)
        yearData.clear()
        val calendar = Calendar.getInstance()
        val weekCal = Calendar.getInstance()
        weekCal.timeInMillis = mili
        for (i in 0..11) {
            monthAverage = 0f
            weekCal.set(Calendar.MONTH, i).toString()
            weekCal.set(Calendar.DAY_OF_MONTH, 1).toString()
            val num = weekCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val totalpercent = num * 100
            for (j in 1..num) {
                calendar.time = weekCal.time
                if (DB.checkIfRecordExist(calendar.timeInMillis)) {
                    val getOldRecord = DB.getonevalue(calendar.timeInMillis)
                    val getDrawables = convertStringToArray(getOldRecord.drawable!!)
                    val getLogval = convertStringToArray(getOldRecord.logvalue!!)
                    for (k in 0 until getDrawables.size) {
                        when (getDrawables[k].replace("[\\D]".toRegex(), "").toInt()) {
                            R.drawable.water_glass -> {
                                waterLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                            }
                            R.drawable.tea -> {
                                teaLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                            }
                            R.drawable.coffee -> {
                                coffeeLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                    .toInt()

                            }
                            R.drawable.milk -> {
                                milkLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()

                            }
                            R.drawable.fruitjuice -> {
                                fruitLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()

                            }
                            R.drawable.sodecan -> {
                                sodaLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                            }
                            R.drawable.beermag -> {
                                beerLogTotal += getLogval[k].replace("[\\D]".toRegex(), "").toInt()
                            }
                            R.drawable.enerydrink -> {
                                energyLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                    .toInt()
                            }
                            R.drawable.lemonade -> {
                                lemonadeLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                    .toInt()
                            }
                            else -> {
                                Log.e("TAG", "setweek:null ")
                            }
                        }
                    }
                    if (getOldRecord.totaldrink!! >= getOldRecord.dailygoal!!) {
                        yearData.add(100f)
                        monthAverage += 100f
                    } else {
                        val percentFloat =
                            ((getOldRecord.totaldrink!! * 100) / getOldRecord.dailygoal!!).toFloat()
                        monthAverage += percentFloat
                    }
                }
                weekCal.add(Calendar.DATE, 1)
            }
            values = ((monthAverage * 100) / totalpercent)
            yearData.add(values)
        }

        barEntriesArrayList = ArrayList()
        barEntriesArrayList.clear()
        for (i in 0..11) {
            barEntriesArrayList.add(BarEntry(i.toFloat(), yearData[i]))
        }
        barDataSet = BarDataSet(barEntriesArrayList as List<BarEntry>, "Drink Water")
        barData = BarData(barDataSet)
        barData.barWidth = 0.5f
        barChart.data = barData

        barDataSet!!.setDrawValues(false)
        barDataSet!!.valueTextSize = 6f
        barDataSet!!.setColors(Color.BLUE)
        barDataSet!!.valueTextColor = mPrefs.getInt("charttextcolor", Color.BLACK)
        barChart.axisLeft.textColor = mPrefs.getInt("charttextcolor", Color.BLACK)
        barChart.invalidate()
        barChart.animateY(2000)
        barChart.notifyDataSetChanged()
        setpiechart()
    }

    private fun setLifeTime(mili: Long) {
        val dialog = ProgressDialog.show(
            this, "",
            "Loading...", true
        )
        dialog.show()
        setZero()
        var values = 0f
        var monthAverage: Float
        DB = DatabaseHandler(this)
        val calendar = Calendar.getInstance()
        val weekCal = Calendar.getInstance()
        weekCal.timeInMillis = mili
        val getdate = (yearFormat.format(weekCal.time))
        Log.e("TAG", ":getdate:$getdate")
        for (i in 0..2) {
            val getdate = (yearFormat.format(weekCal.time))
            Log.e("TAG", "setLifeTime:getdate:$getdate")
            for (i in 0..11) {
                values = 0f
                monthAverage = 0f
                weekCal.set(Calendar.MONTH, i).toString()
                weekCal.set(Calendar.DAY_OF_MONTH, 1).toString()
                val num = weekCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val totalpercent = num * 100
                for (j in 1..num) {
                    calendar.time = weekCal.time
                    if (DB.checkIfRecordExist(calendar.timeInMillis)) {
                        Log.e("TAG", "setLifeTime: Exist" + weekformat.format(calendar.time))
                        val getOldRecord = DB.getonevalue(calendar.timeInMillis)
                        val getDrawables = convertStringToArray(getOldRecord.drawable!!)
                        val getLogval = convertStringToArray(getOldRecord.logvalue!!)
                        for (k in 0 until getDrawables.size) {
                            when (getDrawables[k].replace("[\\D]".toRegex(), "").toInt()) {
                                R.drawable.water_glass -> {
                                    waterLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                        .toInt()
                                }
                                R.drawable.tea -> {
                                    teaLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                        .toInt()
                                }
                                R.drawable.coffee -> {
                                    coffeeLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                        .toInt()

                                }
                                R.drawable.milk -> {
                                    milkLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                        .toInt()

                                }
                                R.drawable.fruitjuice -> {
                                    fruitLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                        .toInt()

                                }
                                R.drawable.sodecan -> {
                                    sodaLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                        .toInt()
                                }
                                R.drawable.beermag -> {
                                    beerLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                        .toInt()
                                }
                                R.drawable.enerydrink -> {
                                    energyLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                        .toInt()
                                }
                                R.drawable.lemonade -> {
                                    lemonadeLogTotal += getLogval[k].replace("[\\D]".toRegex(), "")
                                        .toInt()
                                }
                                else -> {
                                    Log.e("TAG", "setweek:null ")
                                }
                            }
                        }
                        if (getOldRecord.totaldrink!! >= getOldRecord.dailygoal!!) {
                            lifeTimeData.add(100f)
                            monthAverage += 100f
                        } else {
                            val percentFloat =
                                ((getOldRecord.totaldrink!! * 100) / getOldRecord.dailygoal!!).toFloat()
                            monthAverage += percentFloat
                        }
                    }
                    weekCal.add(Calendar.DATE, 1)
                }
                values += ((monthAverage * 100) / totalpercent)
            }
            lifeTimeData.add(values)
            //  weekCal.set(Calendar.YEAR, 1).toString()
        }
        Log.e("TAG", "setLifeTime: $lifeTimeData")


        Log.e("TAG", "setLifeTime:True ")
        barEntriesArrayList = ArrayList()
        barEntriesArrayList.clear()
        for (i in 0..2) {
            barEntriesArrayList.add(BarEntry(i.toFloat(), lifeTimeData[i]))
        }
        barDataSet = BarDataSet(barEntriesArrayList as List<BarEntry>, "Drink Water")
        barData = BarData(barDataSet)
        dialog.dismiss()
        barData.barWidth = 0.5f
        barChart.data = barData
        barDataSet!!.setDrawValues(false)
        barDataSet!!.valueTextSize = 6f
        barDataSet!!.setColors(Color.BLUE)
        barDataSet!!.valueTextColor = mPrefs.getInt("charttextcolor", Color.BLACK)
        barChart.axisLeft.textColor = mPrefs.getInt("charttextcolor", Color.BLACK)
        barChart.invalidate()
        barChart.animateY(2000)
        barChart.notifyDataSetChanged()
        setpiechart()

    }

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

}

