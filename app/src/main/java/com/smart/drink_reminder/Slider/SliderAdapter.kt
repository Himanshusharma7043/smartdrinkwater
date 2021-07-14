package com.smart.drink_reminder.Slider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.smart.drink_reminder.R

class SliderAdapter (private val context: Context, private val list: List<SliderData>) :
    PagerAdapter(){
    override fun getCount(): Int {
        return list.size
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.slider_layout, container, false)
        val sliderIMG:ImageView=view.findViewById(R.id.sliderIMG)
        val noAdsLayout:LinearLayout=view.findViewById(R.id.noAdsLayout)
        val drinkText:TextView=view.findViewById(R.id.txt1)
        container.addView(view, 0)
        if (!list[position].takeText!!){
            sliderIMG.visibility=View.VISIBLE
            drinkText.visibility=View.VISIBLE
            drinkText.text=list[position].drinkText
            drinkText.setTextColor(list[position].txtColor!!)
            noAdsLayout.visibility=View.GONE
            sliderIMG.setImageResource(list[position].imgID!!)
        }else{
            sliderIMG.visibility=View.GONE
            drinkText.visibility=View.GONE
            noAdsLayout.visibility=View.VISIBLE
        }
        return view
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}