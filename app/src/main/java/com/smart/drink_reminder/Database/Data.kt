package com.smart.drink_reminder.Database

class Data {
    var id: Int? = null
        get() = field
        set(value) {
            field = value
        }
    var name:String?=null
        get() = field
        set(value) {
            field = value
        }

    var value: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor() {}
    constructor(id: Int?, name: String?, value: String?) {
        this.id = id
        this.name = name
        this.value = value
    }

}