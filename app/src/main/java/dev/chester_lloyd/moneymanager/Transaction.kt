package dev.chester_lloyd.moneymanager

import android.content.Context
import java.util.*

class Transaction {
    var transactionID: Int = 0
    var category = Category()
    var merchant: String? = null
    var details: String? = null
    var amount: Double = 0.00
    var date: Calendar = Calendar.getInstance()

    constructor(transactionID: Int, category: Category, merchant: String, details: String?,
                date: Calendar, amount: Double) {
        this.transactionID = transactionID
        this.category = category
        this.merchant = merchant
        this.details = details
        this.date = date
        this.amount = amount
    }

    constructor() {
        this.transactionID = 0
    }

    fun getStringAmount(context: Context) :String {
//      Place - sign before the pound if it is negative
        return if (amount.toString().first() == '-') {
            val splitBalance = amount.toString().split("-")
            "- " + context.resources.getString(R.string.balance_text, splitBalance[1].toDouble())
        } else {
            context.resources.getString(R.string.balance_text, amount)
        }
    }

    fun getDate(context: Context, format: String) :String {
//      Get date in a nice format
        var stringDate = ""
        stringDate = if (format == "DMY") {
            context.resources.getString(R.string.date_DMY,
                date.get(Calendar.DAY_OF_MONTH).toString(),
                (date.get(Calendar.MONTH) + 1).toString(),
                date.get(Calendar.YEAR).toString())
        } else {
            date.toString()
        }
        return stringDate
    }
}