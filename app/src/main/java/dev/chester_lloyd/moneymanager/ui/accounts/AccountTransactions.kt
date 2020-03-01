package dev.chester_lloyd.moneymanager.ui.accounts

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import dev.chester_lloyd.moneymanager.*
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.TransactionDetails
import kotlinx.android.synthetic.main.account.view.ivIcon
import kotlinx.android.synthetic.main.account.view.tvName
import kotlinx.android.synthetic.main.activity_account_transactions.*
import kotlinx.android.synthetic.main.transaction.view.*

/**
 * An [AppCompatActivity] subclass to show the transactions for a given account. This also displays
 * options to edit and delete the [account].
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class AccountTransactions : AppCompatActivity() {

    private var account = Account()

    /**
     * An [onCreate] method that sets up the supportActionBar, [account] and view
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_transactions)

        // Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.manage_account)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        account = Account(
            intent.getIntExtra("accountID", 0),
            intent.getStringExtra("name"),
            intent.getDoubleExtra("balance", 0.0),
            intent.getIntExtra("icon", 0),
            intent.getIntExtra("colour", 0)
        )

        tvName.text = account.name
        tvBalance.text = account.getStringBalance(this)

        val iconManager = IconManager(this)
        ivIcon.setImageResource(
            iconManager.getIconByID(
                iconManager.accountIcons, account.icon
            ).drawable
        )
        ivIcon.setBackgroundResource(
            iconManager.getIconByID(
                iconManager.colourIcons, account.colour
            ).drawable
        )
    }

    /**
     * An [onResume] method that adds all account transactions to a ListView and updates the view
     * to show any potential changes if the page is reloaded
     */
    override fun onResume() {
        super.onResume()
        val dbManager = DBManager(this)

        if (intent.getIntExtra("accountID", 0) > 0) {
            // Read current account from database
            account = dbManager.selectAccount(intent.getIntExtra("accountID", 0))
        }

        // Update entry fields with account info
        tvName.text = account.name
        tvBalance.text = account.getStringBalance(this)
        val iconManager = IconManager(this)
        ivIcon.setImageResource(
            iconManager.getIconByID(
                iconManager.accountIcons, account.icon
            ).drawable
        )
        ivIcon.setBackgroundResource(
            iconManager.getIconByID(
                iconManager.colourIcons, account.colour
            ).drawable
        )

        // Get transactions (as payments) as an array list from database
        val listPayments = dbManager
            .selectPayments(account.accountID, "accounts")
        dbManager.sqlDB!!.close()

        // Pass this to the list view adaptor and populate
        val transactionsAdapter = TransactionsAdapter(listPayments)
        this.lvTransactions.adapter = transactionsAdapter

        // When a transaction in the list is clicked, load the TransactionDetails activity
        this.lvTransactions.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                // Get transaction object of item that is clicked
                val payment = lvTransactions.getItemAtPosition(position) as Payment

                // Setup an intent to send this across to view the transaction's details
                val intent = Intent(this, TransactionDetails::class.java)
                val bundle = Bundle()
                bundle.putInt("transactionID", payment.transaction.transactionID)
                intent.putExtras(bundle)
                startActivity(intent)
            }
    }

    /**
     * An [onCreateOptionsMenu] method that adds the edit menu to the toolbar. This includes an
     * edit and delete button.
     *
     * @param menu The options menu to place items.
     * @return True to display the menu, or false to not show the menu.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * An [onOptionsItemSelected] method that adds functionality when the menu buttons are clicked.
     *
     * @param item The menu item that was selected.
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menuEdit -> {
            // Edit icon clicked, go to edit page (pass all account details)
            val intent = Intent(this, AddAccount::class.java)

            val bundle = Bundle()
            bundle.putInt("accountID", account.accountID)
            bundle.putString("name", account.name)
            bundle.putDouble("balance", account.balance)
            bundle.putInt("icon", account.icon)
            bundle.putInt("colour", account.colour)
            intent.putExtras(bundle)

            startActivity(intent)
            true
        }
        R.id.menuDelete -> {
            // Delete icon clicked, build an alert dialog to get user confirmation
            val alertDialog = AlertDialog.Builder(this)

            alertDialog.setMessage(resources.getString(R.string.alert_message_delete_account))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    finish()
                    // Delete the account
//                    DBManager(this).delete(
//                        DBManager(this).dbAccountTable,
//                        arrayOf(account.accountID.toString())
//                    )
                }
                .setNegativeButton(resources.getString(R.string.no)) {
                    // Do nothing, close box
                        dialog, _ ->
                    dialog.cancel()
                }

            val alert = alertDialog.create()
            alert.setTitle(resources.getString(R.string.alert_title_delete_account))
            alert.show()
            true
        }
        else -> {
            // Unknown action (not edit or delete) invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * An [onSupportNavigateUp] method that closes this activity (goes to previous page) once
     * toolbar back button is pressed.
     *
     * @return true if Up navigation completed successfully and this Activity was finished, false
     * otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * An inner class that takes an array of [Transaction] objects and handles all operations of the
     * ListView.
     *
     * @param listTransactionsAdapter An [ArrayList] of [Transaction] objects
     * @return [BaseAdapter]
     */
    inner class TransactionsAdapter(private var listTransactionsAdapter: ArrayList<Payment>) :
        BaseAdapter() {

        /**
         * Creates a new row within the list view
         *
         * @param position Position of row in the ListView.
         * @param convertView A View object
         * @param parent The parent's ViewGroup
         * @return A View for a row in the ListView.
         * @suppress InflateParams as the layout is inflating without a Parent
         * @suppress ViewHolder as there is unconditional layout inflation from view adapter
         */
        @SuppressLint("InflateParams", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // Adds each transaction to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.transaction, null)
            val payment = listTransactionsAdapter[position]
            rowView.tvName.text = payment.transaction.merchant
            rowView.tvDate.text = payment.transaction.getDate(applicationContext, "DMY")
            rowView.tvAmount.text = payment.getStringAmount(applicationContext)

            // Use IconManager to load the icons
            val iconManager = IconManager(applicationContext)
            rowView.ivIcon.setImageResource(
                iconManager.getIconByID(
                    iconManager.categoryIcons, payment.transaction.category.icon
                ).drawable
            )
            rowView.ivIcon.setBackgroundResource(
                iconManager.getIconByID(
                    iconManager.colourIcons, payment.transaction.category.colour
                ).drawable
            )

            return rowView
        }

        /**
         * Get the [Transaction] object at a given [position] in the ListView.
         *
         * @param position Position of row in the ListView.
         * @return A [Transaction] object of the item at the given position.
         */
        override fun getItem(position: Int): Any {
            return listTransactionsAdapter[position]
        }

        /**
         * Get the row ID associated with the specified [position] in the list.
         *
         * @param position The position of the item within the list whose row ID we want.
         * @return The ID of the item at the specified position.
         */
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        /**
         * Returns number of items in the ListView.
         *
         * @return The size of the ListView.
         */
        override fun getCount(): Int {
            return listTransactionsAdapter.size
        }
    }
}
