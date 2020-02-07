package dev.chester_lloyd.moneymanager

import android.content.Context

class Account {
    var accountID: Int = 0
    var name: String? = null
    var balance: Double = 0.00
    var icon: Int = 0
    var colour: Int = 0

    constructor(accountID: Int, name: String, balance: Double, icon: Int, colour: Int) {
        this.accountID = accountID
        this.name = name
        this.balance = balance
        this.icon = icon
        this.colour = colour
    }

    constructor() {
        this.accountID = 0
    }


    fun getStringBalance(context: Context) :String {
//      Place - sign before the pound if it is negative
        if (balance.toString().first() == '-') {
            val splitBalance = balance.toString().split("-")
            return "- " + context.resources.getString(R.string.balance_text, splitBalance[1].toDouble())
        } else {
            return context.resources.getString(R.string.balance_text, balance)
        }
    }


//  Return object as a string
    override fun toString() :String {
        return String.format("ID: %d, Name: %s, Balance: %.2f, Icon: %d, Colour: %d", accountID, name, balance, icon, colour)
    }
}