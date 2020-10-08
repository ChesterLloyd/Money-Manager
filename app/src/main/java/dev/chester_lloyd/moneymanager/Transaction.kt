package dev.chester_lloyd.moneymanager

import android.content.Context
import java.util.*

/**
 * A [Class] that stores all the information about a transaction that the user has saved.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class Transaction {
    var transactionID: Int = 0
    var category = Category()
    var merchant: String? = null
    var details: String? = null
    var amount: Double = 0.00
    var date: Calendar = Calendar.getInstance()
    var transferTransactionID: Int = 0

    /**
     * Creates a complete [Transaction] when all necessary fields have been provided.
     *
     * @param transactionID The ID of the transaction.
     * @param category The [Category] that this transaction is listed under.
     * @param merchant The store/person who handled the transaction.
     * @param details Optional additional details associated about this transaction.
     * @param date The date that the transaction took place.
     * @param amount The total amount that the transaction is worth.
     * @param transferTransaction The transaction that is involved when the transaction is a
     * transfer from one account to another. Thi is optional as it is likely not ll transactions
     * are transfers.
     */
    constructor(
        transactionID: Int, category: Category, merchant: String, details: String?,
        date: Calendar, amount: Double, transferTransaction: Transaction?
    ) {
        this.transactionID = transactionID
        this.category = category
        this.merchant = merchant
        this.details = details
        this.date = date
        this.amount = amount
        if (transferTransaction != null) {
            this.transferTransactionID = transferTransaction.transactionID
        }
    }

    /**
     * Creates an empty [Transaction] and sets its ID to 0.
     */
    constructor() {
        this.transactionID = 0
    }

    /**
     * Gets the [date] as a formatted [String].
     *
     * @param context Context.
     * @param format The format the date should take.
     * @return The [amount] with a currency symbol.
     */
    fun getDate(context: Context, format: String): String {
        return if (format == "DMY") {
            context.resources.getString(
                R.string.date_DMY,
                date.get(Calendar.DAY_OF_MONTH).toString(),
                (date.get(Calendar.MONTH) + 1).toString(),
                date.get(Calendar.YEAR).toString()
            )
        } else {
            date.toString()
        }
    }
}