package com.smart.drink_reminder.AddCupRecyclerView

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.smart.drink_reminder.Database.DatabaseHandler
import com.smart.drink_reminder.R

class CupAdapter(private var context: Context, private var cuplist: List<String>) :
    RecyclerView.Adapter<CupAdapter.ViewHolder>() {

    private var listData: ArrayList<String> = cuplist as ArrayList<String>
    var strSeparator = ","
    lateinit var DB: DatabaseHandler
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v: View = inflater.inflate(R.layout.addcup_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = cuplist[position]
        holder.cuplevel.text = list
        holder.delete_cup.setOnClickListener() {
            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
            deleteItem(position)
        }
    }

    fun deleteItem(index: Int) {
        DB = DatabaseHandler(context)
        listData.removeAt(index)
        notifyDataSetChanged()
        notifyItemRemoved(index)
        notifyDataSetChanged()
        notifyItemRangeChanged(index, listData.size)
        Log.e("List Data",""+listData)
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
    override fun getItemCount(): Int {
        return cuplist.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cuplevel: TextView = itemView.findViewById(R.id.addcupitemlevel)
        var delete_cup: ImageView = itemView.findViewById(R.id.delete_cup)
    }
}