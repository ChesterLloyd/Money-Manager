package dev.chester_lloyd.moneymanager

class Account {
    var accountID:Int?=null
    var name:String?=null
    var balance:Double?=null
    var icon:Int?=R.drawable.ic_account_cash
    var circle:Int?=R.drawable.ic_circle_green

    constructor(accountID:Int, name:String, balance:Double, icon:Int?, circle:Int?) {
        this.accountID = accountID
        this.name = name
        this.balance = balance
//      Preset to green circle and cash icon if these are null
        if (icon != null) {
            this.icon = icon
        }
        if (circle != null) {
            this.circle = circle
        }
    }
}