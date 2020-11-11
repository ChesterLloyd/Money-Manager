package dev.chester_lloyd.moneymanager.ui.recurring_transactions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.chester_lloyd.moneymanager.*
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.ListViewManager
import kotlinx.android.synthetic.main.activity_recurring_transaction_details.*

/**
 * An [AppCompatActivity] subclass to show the transactions for a given category. This also displays
 * options to edit and delete the [category].
 *
 * @author Chester Lloyd
 * @since 1.5
 */
class RecurringTransactionDetails : AppCompatActivity() {

    private var recurringTransaction = RecurringTransaction()

    /**
     * An [onCreate] method that sets up the supportActionBar, [recurringTransaction] and view.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recurring_transaction_details)

        // Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.menu_recurring_transactions)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recurringTransaction.recurringTransactionID =
            intent.getIntExtra("recurringTransactionID", 0)
    }

    /**
     * An [onResume] method that adds all recurring transaction information to the view to show
     * any potential changes if the page is reloaded.
     */
    override fun onResume() {
        super.onResume()

        if (recurringTransaction.recurringTransactionID > 0) {
            // Read current recurring transaction data from database
            val dbManager = DBManager(this)
            recurringTransaction =
                dbManager.selectRecurringTransaction(recurringTransaction.recurringTransactionID)
            dbManager.sqlDB!!.close()
        }

        // Update top transaction with info
        tvName.text = recurringTransaction.transactions[0].merchant
        tvAmount.text =
            MainActivity.stringBalance(this, recurringTransaction.transactions[0].amount)

        val iconManager = IconManager(this)
        ivIcon.setImageResource(
            iconManager.getIconByID(
                iconManager.categoryIcons, recurringTransaction.transactions[0].category.icon
            ).drawable
        )
        ivIcon.setBackgroundResource(
            iconManager.getIconByID(
                iconManager.colourIcons, recurringTransaction.transactions[0].category.colour
            ).drawable
        )

        // Update details with info
        tvFrequency.text = this.getString(
            R.string.frequency_repeats_every,
            recurringTransaction.getFrequencyString()
        )
        tvNextDue.text = recurringTransaction.getFormattedNextDueDate(this)
        tvUntil.text = recurringTransaction.getFormattedEndDate(this)

        // Pass this to the list view adaptor and populate
        this.lvTransactions.adapter = ListViewManager(
            recurringTransaction.transactions.toTypedArray(),
            layoutInflater,
            this,
            "recurring transaction"
        )
    }

    /**
     * An [onOptionsItemSelected] method that closes this activity (goes to previous page) once
     * toolbar back button is pressed.
     *
     * @return true if Up navigation completed successfully and this Activity was finished, false
     * otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
