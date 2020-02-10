package dev.chester_lloyd.moneymanager

import android.content.Context

class Payment {
    var paymentID: Int = 0
    var transaction = Transaction()
    var account = Account()
    var amount = 0.0

    constructor(paymentID: Int, transaction: Transaction, account: Account, amount: Double) {
        this.paymentID = paymentID
        this.transaction = transaction
        this.account = account
        this.amount = amount
    }

    constructor() {
        this.paymentID = 0
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

    fun getEditTextAmount(context: Context) :String {
//      Add a symbol to amount
        if (amount > 0.0) {
            val splitBalance = amount.toString().split(".")
            if (splitBalance.size == 2 && splitBalance[1].length == 1) {
                return "£" + amount.toString() + "0"
            }
            return "£" + amount.toString()
        } else {
            return "£0.0"
        }
    }
}