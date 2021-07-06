package com.smart.drink_reminder.AddCupRecyclerView

class AddCupData {
    var cuplevel: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(){}
    constructor(cuplevel: String?) {
        this.cuplevel = cuplevel
    }

}