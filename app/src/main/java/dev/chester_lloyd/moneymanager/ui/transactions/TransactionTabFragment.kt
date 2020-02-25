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
import androidx.lifecycle.ViewModelProvider
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.Transaction
import dev.chester_lloyd.moneymanager.DBManager
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
        viewModel = ViewModelProvider(this)[TransactionTabFragmentViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

//      Get transactions as an array list from database
        val listTransactions = DBManager(context!!).selectTransaction(tab, "Categories", null)

//      If there are no transactions under this category, show a message
        if (listTransactions.isEmpty()) {
            tvMessage.visibility = View.VISIBLE
        } else {
//          There are transactions to show, hide message and show transactions
            tvMessage.visibility = View.GONE
//          Pass this to the list view adaptor and populate
            val myTransactionsAdapter = TransactionsAdapter(listTransactions)
            lvTransactions.adapter = myTransactionsAdapter

//          When a transaction in the list is clicked
            lvTransactions.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
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

    inner class TransactionsAdapter(private var listTransactionsAdapter: ArrayList<Transaction>) :
        BaseAdapter() {

        @SuppressLint("InflateParams", "ViewHolder")
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
