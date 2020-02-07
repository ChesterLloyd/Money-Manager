package dev.chester_lloyd.moneymanager

import android.content.Context
import kotlinx.android.synthetic.main.transaction.view.*
import java.util.*

class Transaction {
    var transactionID: Int = 0
    var categoryID: Int = 0
    var name: String? = null
    var amount: Double = 0.00
    var date: Calendar = Calendar.getInstance()
    var icon: Int = 0
    var colour: Int = 0

    constructor(transactionID: Int, categoryID: Int, name: String, amount: Double, date: Calendar, icon: Int, colour: Int) {
        this.transactionID = transactionID
        this.categoryID = categoryID
        this.name = name
        this.amount = amount
        this.date = date
        this.icon = icon
        this.colour = colour
    }

    constructor() {
        this.transactionID = 0
    }


    fun getStringAmount(context: Context) :String {
//      Place - sign before the pound if it is negative
        if (amount.toString().first() == '-') {
            val splitBalance = amount.toString().split("-")
            return "- " + context.resources.getString(R.string.balance_text, splitBalance[1].toDouble())
        } else {
            return context.resources.getString(R.string.balance_text, amount)
        }
    }

    fun getDate(context: Context, format: String) :String {
//      Get date in a nice format
        var stringDate = "";
        if (format == "DMY") {
            stringDate = context.resources.getString(R.string.date_DMY,
                date.get(Calendar.DAY_OF_WEEK).toString(),
                date.get(Calendar.MONTH).toString(), date.get(Calendar.YEAR).toString())
        } else {
            stringDate = date.toString()
        }
        return stringDate
    }
}