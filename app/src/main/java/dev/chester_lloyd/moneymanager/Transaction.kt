package dev.chester_lloyd.moneymanager

import android.content.Context
import kotlinx.android.synthetic.main.transaction.view.*
import java.util.*

class Transaction {
    var transactionID: Int = 0
    var category = Category()
    var name: String? = null
    var amount: Double = 0.00
    var date: Calendar = Calendar.getInstance()

    constructor(transactionID: Int, category: Category, name: String, date: Calendar, amount: Double) {
        this.transactionID = transactionID
        this.category = category
        this.name = name
        this.date = date
        this.amount = amount
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
                date.get(Calendar.DAY_OF_MONTH).toString(),
                (date.get(Calendar.MONTH) + 1).toString(),
                date.get(Calendar.YEAR).toString())
        } else {
            stringDate = date.toString()
        }
        return stringDate
    }
}