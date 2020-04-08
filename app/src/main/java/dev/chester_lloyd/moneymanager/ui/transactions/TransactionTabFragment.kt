package dev.chester_lloyd.moneymanager.ui.transactions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.Transaction
import dev.chester_lloyd.moneymanager.DBManager
import dev.chester_lloyd.moneymanager.MainActivity
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.TransactionDetails
import kotlinx.android.synthetic.main.fragment_transaction_tab.*
import kotlinx.android.synthetic.main.transaction.view.*
import kotlin.collections.ArrayList

/**
 * A [Fragment] subclass to show a tabbed layout containing pie charts.
 *
 * @param tab The ID of the current tab that's selected
 * @author Chester Lloyd
 * @since 1.0
 */
class TransactionTabFragment(private val tab: Int) : Fragment() {

    /**
     * An [onCreateView] method that sets up the View
     *
     * @param inflater The LayoutInflater object
     * @param container The parent view.
     * @param savedInstanceState Fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_tab, container, false)
    }

    /**
     * An [onResume] method that creates a ListView of transactions based on a category.
     */
    override fun onResume() {
        super.onResume()

        // Get transactions as an array list from database
        val dbManager = DBManager(context!!)
        val listTransactions = dbManager.selectTransactions(tab,
            "Categories", null)
        dbManager.sqlDB!!.close()

        // If there are no transactions under this category, show a message
        if (listTransactions.isEmpty()) {
            tvMessage.visibility = View.VISIBLE
        } else {
            // There are transactions to show, hide message and show transactions
            tvMessage.visibility = View.GONE
            // Pass this to the list view adaptor and populate
            val myTransactionsAdapter = TransactionsAdapter(listTransactions)
            lvTransactions.adapter = myTransactionsAdapter

            // When a transaction in the list is clicked
            lvTransactions.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    // Get account object of item that is clicked
                    val transaction = lvTransactions.getItemAtPosition(position) as Transaction

                    // Setup an intent to send this across to view the account's transactions
                    val intent = Intent(context, TransactionDetails::class.java)

                    val bundle = Bundle()
                    bundle.putInt("transactionID", transaction.transactionID)
                    intent.putExtras(bundle)

                    startActivity(intent)
                }
        }
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
            rowView.tvDate.text = transaction.getDate(context!!, "DMY")
            rowView.tvAmount.text = MainActivity.stringBalance(context!!, transaction.amount)

            val iconManager = IconManager(context!!)
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
