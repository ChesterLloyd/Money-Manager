package dev.chester_lloyd.moneymanager

import android.content.Context
import dev.chester_lloyd.moneymanager.ui.IconManager
import lecho.lib.hellocharts.model.SliceValue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class PieManager(context: Context) : DBManager(context) {

    private val context: Context = context
    val iconManager = IconManager(context)

    //  Function that selects payments based on a transaction/account ID as a list of Payment objects
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
                val payment = Payment(selectTransaction(transactionID), selectAccount(accountID),
                    amount
                )

                pieData.add(SliceValue(
                    (payment.amount.absoluteValue).toFloat(),
                    (iconManager.getIconByID(
                        iconManager.colourIcons,
                        payment.transaction.category.colour
                    ).colour!!)
                ).setLabel(payment.transaction.category.name + " " + payment.getStringAmount(context)))

            } while (cursor.moveToNext())
        }
        cursor.close()
        return pieData
    }

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