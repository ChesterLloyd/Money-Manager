package dev.chester_lloyd.moneymanager.ui.categories

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.chester_lloyd.moneymanager.*
import dev.chester_lloyd.moneymanager.MainActivity.Companion.TRANSFER_CATEGORY_ID
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.TransactionDetails
import kotlinx.android.synthetic.main.activity_category_transaction.*
import kotlinx.android.synthetic.main.transaction.view.*

/**
 * An [AppCompatActivity] subclass to show the transactions for a given category. This also displays
 * options to edit and delete the [category].
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class CategoryTransaction : AppCompatActivity() {

    private var category = Category()

    /**
     * An [onCreate] method that sets up the supportActionBar, [category] and view
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_transaction)

        // Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.manage_category)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        category = Category(
            intent.getIntExtra("categoryID", 0),
            intent.getStringExtra("name"),
            intent.getIntExtra("icon", 0),
            intent.getIntExtra("colour", 0)
        )

        tvName.text = category.name

        val iconManager = IconManager(this)
        ivIcon.setImageResource(
            iconManager.getIconByID(
                iconManager.categoryIcons, category.icon
            ).drawable
        )
        ivIcon.setBackgroundResource(
            iconManager.getIconByID(
                iconManager.colourIcons, category.colour
            ).drawable
        )

        // Get transactions as an array list from database
        val dbManager = DBManager(this)
        val listTransactions = dbManager
            .selectTransactions(category.categoryID, "Categories", null)
        dbManager.sqlDB!!.close()

        // Pass this to the list view adaptor and populate
        val myTransactionsAdapter = TransactionsAdapter(listTransactions)
        this.lvTransactions.adapter = myTransactionsAdapter
    }

    /**
     * An [onResume] method that adds all category transactions to a ListView and updates the view
     * to show any potential changes if the page is reloaded
     */
    override fun onResume() {
        super.onResume()

        if (intent.getIntExtra("categoryID", 0) > 0) {
            // Read current account from database
            val dbManager = DBManager(this)
            category = dbManager.selectCategory(intent.getIntExtra("categoryID", 0))
            dbManager.sqlDB!!.close()
        }

        // Update entry fields with account info
        tvName.text = category.name
        val iconManager = IconManager(this)
        ivIcon.setImageResource(
            iconManager.getIconByID(
                iconManager.categoryIcons, category.icon
            ).drawable
        )
        ivIcon.setBackgroundResource(
            iconManager.getIconByID(
                iconManager.colourIcons, category.colour
            ).drawable
        )

        // When a transaction in the list is clicked
        this.lvTransactions.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // Get transaction object of item that is clicked
                val transaction = lvTransactions.getItemAtPosition(position) as Transaction

                // Setup an intent to send this across to view the transaction's details
                val intent = Intent(this, TransactionDetails::class.java)
                val bundle = Bundle()
                bundle.putInt("transactionID", transaction.transactionID)
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
            // Edit icon clicked, go to edit page (pass all category details)
            val intent = Intent(this, AddCategory::class.java)

            val bundle = Bundle()
            bundle.putInt("categoryID", category.categoryID)
            bundle.putString("name", category.name)
            bundle.putInt("icon", category.icon)
            bundle.putInt("colour", category.colour)
            intent.putExtras(bundle)

            startActivity(intent)
            true
        }
        R.id.menuDelete -> {
            // Delete icon clicked
            if (category.categoryID == TRANSFER_CATEGORY_ID) {
                // Attempting to delete transfer
                val toast = Toast.makeText(
                    applicationContext,
                    "You cannot delete this category",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            } else {
                // Build an alert dialog to get user confirmation
                val alertDialog = AlertDialog.Builder(this)

                alertDialog.setMessage(resources.getString(R.string.alert_message_delete_category))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog, id ->
                        finish()
                        // Delete the category
                        DBManager(this).delete(
                            DBManager(this).dbCategoryTable,
                            arrayOf(category.categoryID.toString())
                        )
                    }
                    .setNegativeButton(resources.getString(R.string.no)) {
                        // Do nothing, close box
                            dialog, _ ->
                        dialog.cancel()
                    }

                val alert = alertDialog.create()
                alert.setTitle(resources.getString(R.string.alert_title_delete_category))
                alert.show()
            }
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
     * An inner class that takes an array of [Transaction] objects and handles all operations of the
     * ListView.
     *
     * @param listTransactionsAdapter An [ArrayList] of [Transaction] objects
     * @return [BaseAdapter]
     */
    inner class TransactionsAdapter(private var listTransactionsAdapter: ArrayList<Transaction>) :
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
            val transaction = listTransactionsAdapter[position]
            rowView.tvName.text = transaction.merchant
            rowView.tvDate.text = transaction.getDate(applicationContext, "DMY")
            rowView.tvAmount.text = MainActivity.stringBalance(applicationContext, transaction.amount)

            val iconManager = IconManager(applicationContext)
            rowView.ivIcon.setImageResource(
                iconManager.getIconByID(
                    iconManager.categoryIcons, transaction.category.icon
                ).drawable
            )
            rowView.ivIcon.setBackgroundResource(
                iconManager.getIconByID(
                    iconManager.colourIcons, transaction.category.colour
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
