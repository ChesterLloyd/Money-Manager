package dev.chester_lloyd.moneymanager.ui.transactions

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.Transaction
import dev.chester_lloyd.moneymanager.dbManager
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.TransactionDetails
import kotlinx.android.synthetic.main.fragment_transaction_tab.*
import kotlinx.android.synthetic.main.transaction.view.*
import kotlin.collections.ArrayList

class TransactionTabFragment(private val tab: Int) : Fragment() {

    private lateinit var viewModel: TransactionTabFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_tab, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TransactionTabFragmentViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

//      Get transactions as an array list from database
        var listTransactions = dbManager(context!!).selectTransaction(tab, "Categories")

//      If there are no transactions under this category, show a message
        if (listTransactions.isEmpty()) {
            tvMessage.visibility = View.VISIBLE
        } else {
//          There are transactions to show, hide message and show transactions
            tvMessage.visibility = View.GONE
//          Pass this to the list view adaptor and populate
            val myTransactionsAdapter = myTransactionsAdapter(listTransactions)
            lvTransactions.adapter = myTransactionsAdapter

//          When a transaction in the list is clicked
            lvTransactions.onItemClickListener = object : AdapterView.OnItemClickListener {
                override fun onItemClick(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
//                  Get account object of item that is clicked
                    val transaction = lvTransactions.getItemAtPosition(position) as Transaction

//                  Setup an intent to send this across to view the account's transactions
                    val intent = Intent(context, TransactionDetails::class.java)

                    val bundle = Bundle()
                    bundle.putInt("transactionID", transaction.transactionID)
                    intent.putExtras(bundle)

                    startActivity(intent)
                }
            }
        }
    }

    inner class myTransactionsAdapter: BaseAdapter {
        var listTransactionsAdapter = ArrayList<Transaction>()
        constructor(listTransactionsAdapter:ArrayList<Transaction>):super() {
            this.listTransactionsAdapter = listTransactionsAdapter
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//          Adds each transaction to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.transaction, null)
            val transaction = listTransactionsAdapter[position]
            rowView.tvName.text = transaction.name
            rowView.tvDate.text = transaction.getDate(context!!, "DMY")
            rowView.tvAmount.text = transaction.getStringAmount(context!!)

            val iconManager = IconManager(context!!)
            rowView.ivIcon.setImageResource(iconManager.getIconByID(
                iconManager.categoryIcons, transaction.category.icon).drawable)
            rowView.ivIcon.setBackgroundResource(iconManager.getIconByID(
                iconManager.colourIcons, transaction.category.colour).drawable)

            return rowView
        }

        override fun getItem(position: Int): Any {
            return listTransactionsAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listTransactionsAdapter.size
        }
    }
}
