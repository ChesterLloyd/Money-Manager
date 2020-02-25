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
}