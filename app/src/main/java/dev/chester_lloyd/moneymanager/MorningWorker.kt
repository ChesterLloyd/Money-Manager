package dev.chester_lloyd.moneymanager

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dev.chester_lloyd.moneymanager.MainActivity.Companion.getTimesToday
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A class to run in the background each morning. This is responsible for adding recurring
 * transactions when they are due. The [doWork] method runs once a day at 6AM.
 *
 * @param appContext Context.
 * @param workerParams Parameters for a [Worker].
 * @author Chester Lloyd
 * @since 1.5
 */
@Suppress("NAME_SHADOWING")
class MorningWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    /**
     * The work that is performed. This will add all recurring transaction that occur today.
     *
     * @return The outcome of the work completing successfully.
     */
    override fun doWork(): Result {
        // Set this task to repeat same time tomorrow
        val currentDate = Calendar.getInstance()

        if (MainActivity.morningWorkerDate.before(currentDate)) {
            MainActivity.morningWorkerDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = MainActivity.morningWorkerDate.timeInMillis - currentDate.timeInMillis
        val dailyWorkRequest = OneTimeWorkRequestBuilder<MorningWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(dailyWorkRequest)

        val dbManager = DBManager(applicationContext)
        val recurringTransactions = dbManager.selectRecurringTransactions(null, null)
        dbManager.sqlDB!!.close()

        for (recurringTransaction in recurringTransactions) {
            val timesToday = getTimesToday()
            if (recurringTransaction.next.timeInMillis >= timesToday[0].timeInMillis &&
                recurringTransaction.next.timeInMillis <= timesToday[1].timeInMillis
            ) {
                // This transaction is due to recur today, add it now
                val dbManager = DBManager(applicationContext)

                val transaction = Transaction()
                transaction.category = recurringTransaction.category
                transaction.merchant = recurringTransaction.name
                transaction.amount = recurringTransaction.amount
                transaction.date = currentDate
                val transactionID = dbManager.insertTransaction(transaction)
                transaction.transactionID = transactionID.toInt()

                val payment = Payment(
                    transaction,
                    recurringTransaction.account,
                    recurringTransaction.amount
                )
                dbManager.insertPayment(payment)

                dbManager.insertRecursRecord(
                    recurringTransaction.recurringTransactionID,
                    transaction.transactionID
                )

                dbManager.sqlDB!!.close()

                //@todo: Fire a notification
            }
        }

        return Result.success()
    }
}