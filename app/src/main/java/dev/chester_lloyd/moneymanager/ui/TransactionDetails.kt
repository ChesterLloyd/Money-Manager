package dev.chester_lloyd.moneymanager.ui

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
import dev.chester_lloyd.moneymanager.DBManager
import dev.chester_lloyd.moneymanager.Payment
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.Transaction
import dev.chester_lloyd.moneymanager.ui.categories.CategoryTransaction
import dev.chester_lloyd.moneymanager.ui.transactions.AddTransaction
import kotlinx.android.synthetic.main.account.view.*
import kotlinx.android.synthetic.main.activity_transation_details.*
import kotlinx.android.synthetic.main.transaction.view.ivIcon
import kotlinx.android.synthetic.main.transaction.view.tvName

/**
 * An [AppCompatActivity] subclass to show the payments for a transaction. This also displays
 * options to edit and delete the [Transaction].
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class TransactionDetails : AppCompatActivity() {

    private var transaction = Transaction()

    /**
     * An [onCreate] method that sets up the supportActionBar, [transaction] and view
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transation_details)

        // Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.transaction_details)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dbManager = DBManager(this)
        transaction = dbManager.selectTransaction(
            intent
                .getIntExtra("transactionID", 0)
        )
        dbManager.sqlDB!!.close()

        tvName.text = transaction.merchant
        tvAmount.text = transaction.getStringAmount(this)

        val iconManager = IconManager(this)
        ivIcon.setImageResource(
            iconManager.getIconByID(
                iconManager.categoryIcons, transaction.category.icon
            ).drawable
        )
        ivIcon.setBackgroundResource(
            iconManager.getIconByID(
                iconManager.colourIcons, transaction.category.colour
            ).drawable
        )
    }

//  If we have come back (after updating) show potential updated account status
    /**
     * An [onResume] method that adds all of the transaction's payments to a ListView and updates
     * the view to show any potential changes if the page is reloaded
     */
    override fun onResume() {
        super.onResume()
        val dbManager = DBManager(this)

        if (intent.getIntExtra("transactionID", 0) > 0) {
            // Read current transaction from database
            transaction = dbManager.selectTransaction(
                intent
                    .getIntExtra("transactionID", 0)
            )
        }

        // Update entry fields with account info
        tvName.text = transaction.merchant
        tvAmount.text = transaction.getStringAmount(this)
        val iconManager = IconManager(this)
        ivIcon.setImageResource(
            iconManager.getIconByID(
                iconManager.categoryIcons, transaction.category.icon
            ).drawable
        )
        ivIcon.setBackgroundResource(
            iconManager.getIconByID(
                iconManager.colourIcons, transaction.category.colour
            ).drawable
        )

        // Show details text if user has written it
        if (transaction.details != null) {
            tvDetails.text = transaction.details
            tvDetails.visibility = View.VISIBLE
        }

        // Get payments as an array list from database
        val listPayments = dbManager
            .selectPayments(transaction.transactionID, "transaction")

        // Pass this to the list view adaptor and populate
        val myPaymentsAdapter = PaymentsAdapter(listPayments)
        this.lvPayments.adapter = myPaymentsAdapter

        // When a transaction in the list is clicked
        this.lvPayments.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // Get payment object of item that is clicked
                val payment = lvPayments.getItemAtPosition(position) as Payment

                // value of item that is clicked
                val intent = Intent(this, CategoryTransaction::class.java)

                val bundle = Bundle()
                bundle.putInt("categoryID", payment.transaction.category.categoryID)
                bundle.putString("name", payment.transaction.category.name)
                bundle.putInt("icon", payment.transaction.category.icon)
                bundle.putInt("colour", payment.transaction.category.colour)
                intent.putExtras(bundle)

                startActivity(intent)
            }
        dbManager.sqlDB!!.close()
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
            val intent = Intent(this, AddTransaction::class.java)
            val bundle = Bundle()
            bundle.putInt("transactionID", transaction.transactionID)
            intent.putExtras(bundle)
            startActivity(intent)
            true
        }
        R.id.menuDelete -> {
            // Delete icon clicked, build an alert dialog to get user confirmation
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setMessage(resources.getString(R.string.alert_message_delete_transaction))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    finish()
                    // Delete the transaction
                    DBManager(this).deleteTransaction(
                        arrayOf(transaction.transactionID.toString())
                    )
                }
                .setNegativeButton(resources.getString(R.string.no)) {
                    // Do nothing, close box
                        dialog, _ ->
                    dialog.cancel()
                }

            val alert = alertDialog.create()
            alert.setTitle(resources.getString(R.string.alert_title_delete_transaction))
            alert.show()
            true
        }
        else -> {
            // Unknown action (not edit or delete) invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
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

    /**
     * An inner class that takes an array of [Payment] objects and handles all operations of the
     * ListView.
     *
     * @param listPaymentsAdapter An [ArrayList] of [Payment] objects
     * @return [BaseAdapter]
     */
    inner class PaymentsAdapter(private var listPaymentsAdapter: ArrayList<Payment>) :
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
            // Adds each payment to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.account, null)
            val payment = listPaymentsAdapter[position]

            val iconManager = IconManager(applicationContext)
            rowView.ivIcon.setImageResource(
                iconManager.getIconByID(
                    iconManager.accountIcons, payment.account.icon
                ).drawable
            )
            rowView.ivIcon.setBackgroundResource(
                iconManager.getIconByID(
                    iconManager.colourIcons, payment.account.colour
                ).drawable
            )

            rowView.tvName.text = payment.account.name
            rowView.tvBalance.text = payment.getStringAmount(applicationContext)
            return rowView
        }

        /**
         * Get the [Payment] object at a given [position] in the ListView.
         *
         * @param position Position of row in the ListView.
         * @return A [Payment] object of the item at the given position.
         */
        override fun getItem(position: Int): Any {
            return listPaymentsAdapter[position]
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
            return listPaymentsAdapter.size
        }
    }
}
