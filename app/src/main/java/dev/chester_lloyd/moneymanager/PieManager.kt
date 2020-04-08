package dev.chester_lloyd.moneymanager

import android.content.Context
import dev.chester_lloyd.moneymanager.ui.IconManager
import lecho.lib.hellocharts.model.SliceValue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

/**
 * An [DBManager] subclass to handle collecting data from the database when creating pie charts.
 *
 * @param context Context.
 * @author Chester Lloyd
 * @since 1.0
 */
class PieManager(private val context: Context) : DBManager(context) {

    val iconManager = IconManager(context)

    /**
     * Gets the total amount for each category for a given month.
     *
     * @param month The month to get data for.
     * @param direction Can be In for income or Out for expenses.
     * @return [SliceValue] data that can be used to construct a pie chart.
     */
    fun categoryMonth(month: String, direction: String): ArrayList<SliceValue> {
        val pieData = ArrayList<SliceValue>()
        var selectionArgs = arrayOfNulls<String>(0)
        var query = "SELECT P.${colID}, P.${colTransactionID}, " +
                "P.${colAccountID}, P.${colAmount}, SUM(P.${colAmount}) AS total FROM $dbPaymentsTable P " +
                "JOIN $dbTransactionTable T ON T.${colID} = P.${colTransactionID} " +
                "JOIN $dbAccountTable A ON A.${colID} = P.${colAccountID} "

        when (direction) {
            "in" -> query += "WHERE P.${colAmount} > 0 "
            "out" -> query += "WHERE P.${colAmount} < 0 "
        }

        if (month != "all") {
            query += "AND strftime('%m', T.${colDate}) = ? "
            selectionArgs = arrayOf(month)
        }

        query += "GROUP BY T.${colCategoryID} ORDER BY total DESC"

        val cursor = sqlDB!!.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            do {
                val transactionID = cursor.getInt(cursor.getColumnIndex(colTransactionID))
                val accountID = cursor.getInt(cursor.getColumnIndex(colAccountID))
                val amount = cursor.getDouble(cursor.getColumnIndex("total"))
                val payment = Payment(
                    selectTransaction(transactionID), selectAccount(accountID),
                    amount
                )

                pieData.add(
                    SliceValue(
                        (payment.amount.absoluteValue).toFloat(),
                        (iconManager.getIconByID(
                            iconManager.colourIcons,
                            payment.transaction.category.colour
                        ).colour!!)
                    ).setLabel(
                        payment.transaction.category.name + " " +
                                MainActivity.stringBalance(context, payment.amount)
                    )
                )

            } while (cursor.moveToNext())
        }
        cursor.close()
        return pieData
    }

    /**
     * Gets the date of a transaction in each month.
     *
     * @param direction Can be In for income or Out for expenses.
     * @return [Calendar] of only 1 transaction for each month that there exists a transaction.
     */
    fun getAllDates(direction: String): ArrayList<Calendar> {
        val dates = ArrayList<Calendar>()
        val selectionArgs = arrayOfNulls<String>(0)
        var query = "SELECT T.${colDate} FROM $dbTransactionTable T "

        when (direction) {
            "in" -> query += "WHERE T.${colAmount} > 0 "
            "out" -> query += "WHERE T.${colAmount} < 0 "
        }
        query += "GROUP BY strftime('%m', T.${colDate}) " +
                "ORDER BY T.${colDate} DESC"

        val cursor = sqlDB!!.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(cursor.getColumnIndex(colDate))
                val cal = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                cal.time = sdf.parse(date)

                dates.add(cal)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return dates
    }
}