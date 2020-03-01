package dev.chester_lloyd.moneymanager

import android.content.Context

/**
 * A [Class] that stores all the information about a payment that the user has saved.
 * @param transaction The [Transaction] that the transaction was paid to or by.
 * @param account The [Account] that the payment was paid to or by.
 * @param amount The amount of money this payment is worth.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class Payment(
    var transaction: Transaction,
    var account: Account,
    var amount: Double
) {

    /**
     * Gets the [amount] as a String with a currency symbol.
     *
     * @param context Context.
     * @return The [amount] with a currency symbol.
     */
    fun getStringAmount(context: Context): String {
//      Place - sign before the pound if it is negative
        return if (amount.toString().first() == '-') {
            val splitBalance = amount.toString().split("-")
            "- " + context.resources.getString(R.string.balance_text, splitBalance[1].toDouble())
        } else {
            context.resources.getString(R.string.balance_text, amount)
        }
    }
}