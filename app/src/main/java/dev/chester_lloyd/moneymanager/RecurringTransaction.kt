package dev.chester_lloyd.moneymanager

import android.content.Context
import android.widget.Toast
import java.util.*

/**
 * A [Class] that stores all the information about a recurring transaction that the user has saved.
 *
 * @author Chester Lloyd
 * @since 1.5
 */
class RecurringTransaction {
    var recurringTransactionID: Int = 0
    var transactionID: Int = 0
    var start: Calendar = Calendar.getInstance()
    var end: Calendar = Calendar.getInstance()
    var frequencyUnit: Int = 0
    var frequencyPeriod: String = ""

    /**
     * Creates a complete [RecurringTransaction] when all necessary fields have been provided.
     *
     * @param recurringTransactionID The ID of the recurring transaction.
     * @param transactionID The ID of the transaction that is set to recur.
     * @param start The date that the transaction starts to recur (date of first transaction in the
     * set).
     * @param end The date that the transaction stops recurring (date of last transaction in the
     * set).
     * @param frequencyUnit The integer value of how often the transaction repeats. e.g if it
     * repeats every 30 days, this would be "30".
     * @param frequencyPeriod The string value of how often the transaction repeats. e.g if it
     * repeats every 30 days, this would be "days".
     */
    constructor(
        recurringTransactionID: Int,
        transactionID: Int,
        start: Calendar,
        end: Calendar,
        frequencyUnit: Int,
        frequencyPeriod: String
    ) {
        this.recurringTransactionID = recurringTransactionID
        this.transactionID = transactionID
        this.start = start
        this.end = end
        this.frequencyUnit = frequencyUnit
        this.frequencyPeriod = frequencyPeriod
        setEndDate()
    }

    /**
     * Creates an empty [RecurringTransaction] and sets its ID to 0.
     */
    constructor() {
        this.recurringTransactionID = 0
        setEndDate()
    }

    /**
     * Sets the default end date to tomorrow.
     */
    private fun setEndDate() {
        this.end.add(Calendar.DATE, 1)
    }

    /**
     * Validates inputs when adding a transaction
     *
     * @param context The context.
     * @param transaction The transaction set to recur
     * @param frequencyUnit The integer value of how often the transaction repeats.
     * @return True if no errors found.
     */
    fun validateWithTransaction(
        context: Context,
        transaction: Transaction,
        frequencyUnit: Int
    ): Boolean {
        this.frequencyUnit = frequencyUnit
        when {
            this.frequencyUnit <= 0 -> {
                // Frequency unit is not valid, show an error
                Toast.makeText(
                    context, R.string.transaction_recurring_validation_unit,
                    Toast.LENGTH_SHORT
                ).show()
            }
            this.frequencyPeriod == "" -> {
                // Frequency period is not valid, show an error
                Toast.makeText(
                    context, R.string.transaction_recurring_validation_period,
                    Toast.LENGTH_SHORT
                ).show()
            }
            this.end.timeInMillis < transaction.date.timeInMillis -> {
                // End date must be after the transaction date, show an error
                Toast.makeText(
                    context, R.string.transaction_recurring_validation_end_before,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                return true
            }
        }
        return false
    }
}