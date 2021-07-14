package com.smart.drink_reminder.Slider

class SliderData {
     var imgID: Int? = null
        get() = field
        set(value) {
            field = value
        }
    var takeText: Boolean? = null
        get() = field
        set(value) {
            field = value
        }
    var drinkText: String? = null
        get() = field
        set(value) {
            field = value
        }
    var txtColor: Int? = null
        get() = field
        set(value) {
            field = value
        }
    constructor(){}
    constructor(imgID: Int?,takeText:Boolean,drinkText: String?,txtColor: Int?) {
        this.imgID = imgID
        this.takeText = takeText
        this.drinkText = drinkText
        this.txtColor = txtColor
    }

}