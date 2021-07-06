package com.smart.drink_reminder.Database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.smart.drink_reminder.LogRecyclerView.LogData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

@SuppressLint("Recycle")
class DatabaseHandler(context: Context) : SQLiteOpenHelper(

    context,
    "DRINK_WATER",
    null,
    1
)

{
    private var instance: DatabaseHandler? = null
    private var mContext: Context = context
    private var DATABASE_NAME="DRINK_WATER"
    @Synchronized
    fun getInstance(context: Context?): DatabaseHandler? {

        if (instance == null) instance = DatabaseHandler(context!!)
        return instance
    }
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("create Table drinktable(mili INTEGER primary key,drawable TEXT, logvalue TEXT, time TEXT, totaldrink INTEGER, dailygoal INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop Table if exists drinktable")
    }

    fun addContact(log: LogData) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("mili", log.mili)
        values.put("drawable", log.drawable)
        values.put("logvalue", log.logvalue)
        values.put("time", log.time)
        values.put("totaldrink",log.totaldrink)
        values.put("dailygoal",log.dailygoal)
        // Inserting Row
        db.insert("drinktable", null, values)
        //db.close() // Closing database connection
    }


    fun updateContact(log: LogData): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("mili", log.mili)
        values.put("drawable", log.drawable)
        values.put("logvalue", log.logvalue)
        values.put("time", log.time)
        values.put("totaldrink",log.totaldrink)
        values.put("dailygoal",log.dailygoal)
        // updating row
        return db.update(
            "drinktable",
            values,
            "mili=${log.mili}",
            null
        )
    }


    fun getdata(): Cursor {
        val DB = this.writableDatabase
        return DB.rawQuery("Select * from drinktable", null)
    }

    fun checkIfRecordExist(mili:Long): Boolean {
        try {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM drinktable WHERE mili = $mili", null)
            if (cursor.moveToFirst()) {
                return true //record Exists
            }
        } catch (errorException: Exception) {
            // db.close();
        }
        return false
    }


    fun getonevalue(id: Long): LogData {
        val db = this.readableDatabase
        val selectQuery = ("SELECT  * FROM drinktable  WHERE mili = $id")
        //   Log.d(LOG, selectQuery)
        val c = db.rawQuery(selectQuery, null)
        if (c != null)
            c.moveToFirst()

        val dd = LogData()
        dd.mili = (c!!.getLong(c.getColumnIndex("mili")))
        dd.drawable =
            (c.getString(c.getColumnIndex("drawable")))
        dd.logvalue =
            (c.getString(c.getColumnIndex("logvalue")))
        dd.time =
            (c.getString(c.getColumnIndex("time")))
        dd.totaldrink =
            (c.getInt(c.getColumnIndex("totaldrink")))
        dd.dailygoal =
            (c.getInt(c.getColumnIndex("dailygoal")))
        return dd
    }

    fun backup(outFileName: String?) {
        //database path
        val inFileName = mContext.getDatabasePath(DATABASE_NAME).toString()
        try {
            val dbFile = File(inFileName)
            val fis = FileInputStream(dbFile)
            // Open the empty db as the output stream
            val output: OutputStream = FileOutputStream(outFileName)
            // Transfer bytes from the input file to the output file
            val buffer = ByteArray(1024)
            var length: Int
            while (fis.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }
            // Close the streams
            output.flush()
            output.close()
            fis.close()
            Toast.makeText(mContext, "Backup Completed", Toast.LENGTH_SHORT).show()
            Log.e("TAG", " backup: " )
        } catch (e: java.lang.Exception) {
            Toast.makeText(mContext, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show()
            Log.e("TAG", "not backup: "+e.message )
            e.printStackTrace()
        }

        //for calling backup in activity

//        val outFileName: String ="/data/data/com.water.drink/databases"
//        //"${Environment.getExternalStorageDirectory()} ${File.separator}" + resources.getString(R.string.app_name) + File.separator
//        val out: String = "$outFileName drinkbackup.db"
//        DB.backup(out)
    }

    fun importDB(inFileName: String?) {
        val outFileName = mContext.getDatabasePath("DRINK_WATER").toString()
        try {
            val dbFile = File(inFileName!!)
            val fis = FileInputStream(dbFile)
            // Open the empty db as the output stream
            val output: OutputStream = FileOutputStream(outFileName)
            // Transfer bytes from the input file to the output file
            val buffer = ByteArray(1024)
            var length: Int
            while (fis.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }
            // Close the streams
            output.flush()
            output.close()
            fis.close()
            Toast.makeText(mContext, "Import Completed", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Toast.makeText(mContext, "Unable to import database. Retry", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

}