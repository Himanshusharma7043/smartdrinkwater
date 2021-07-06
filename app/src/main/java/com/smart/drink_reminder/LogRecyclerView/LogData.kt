package com.smart.drink_reminder.LogRecyclerView

class LogData {
    var mili:Long?=null
        get() = field
        set(value) {
            field = value
        }
    var drawable: String? = null
        get() = field
        set(value) {
            field = value
        }
    var logvalue: String? = null
        get() = field
        set(value) {
            field = value
        }
    var time: String? = null
        get() = field
        set(value) {
            field = value
        }

    var totaldrink: Int? = null
        get() = field
        set(value) {
            field = value
        }
    var dailygoal: Int? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(){}
    constructor(mili: Long?, drawable: String?, logvalue: String?, time: String?, totaldrink: Int?, dailygoal: Int?) {
        this.mili = mili
        this.drawable = drawable
        this.logvalue = logvalue
        this.time = time
        this.totaldrink = totaldrink
        this.dailygoal = dailygoal
    }

}