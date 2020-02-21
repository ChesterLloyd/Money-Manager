package dev.chester_lloyd.moneymanager

import android.content.Context

class Payment(
    var transaction: Transaction,
    var account: Account,
    var amount: Double
) {

    fun getStringAmount(context: Context) :String {
//      Place - sign before the pound if it is negative
        return if (amount.toString().first() == '-') {
            val splitBalance = amount.toString().split("-")
            "- " + context.resources.getString(R.string.balance_text, splitBalance[1].toDouble())
        } else {
            context.resources.getString(R.string.balance_text, amount)
        }
    }

    fun getEditTextAmount(context: Context) :String {
//      Add a symbol to amount
        if (amount > 0.0) {
            val splitBalance = amount.toString().split(".")
            if (splitBalance.size == 2 && splitBalance[1].length == 1) {
                return "£" + amount.toString() + "0"
            }
            return "£$amount"
        } else {
            return "£0.0"
        }
    }
}