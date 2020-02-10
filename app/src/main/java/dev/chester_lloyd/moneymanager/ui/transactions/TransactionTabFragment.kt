package dev.chester_lloyd.moneymanager.ui.transactions

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.chester_lloyd.moneymanager.Category
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.Transaction
import dev.chester_lloyd.moneymanager.dbManager
import kotlinx.android.synthetic.main.fragment_transaction_tab.*
import kotlinx.android.synthetic.main.fragment_transaction_tab.view.*
import kotlinx.android.synthetic.main.fragment_transactions.*
import kotlinx.android.synthetic.main.transaction.view.*
import java.util.*
import kotlin.collections.ArrayList

class TransactionTabFragment(tab: Int) : Fragment() {

    val tab = tab

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
        // TODO: Use the ViewModel

//        tvTab.setText("TAB " + tab)
//
////      Get transactions as an array list from database
//        var listTransactions = loadTransactions("%")
//
////      Pass this to the list view adaptor and populate
//        val myTransactionsAdapter = myTransactionsAdapter(listTransactions)
//        lvTransactions.adapter = myTransactionsAdapter


    }

    override fun onResume() {
        super.onResume()

//      Get transactions as an array list from database
        var listTransactions = dbManager(context!!).selectTransaction(tab, "Categories")

//      Pass this to the list view adaptor and populate
        val myTransactionsAdapter = myTransactionsAdapter(listTransactions)
        lvTransactions.adapter = myTransactionsAdapter
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
            rowView.ivIcon.setImageResource(transaction.category.icon)
            rowView.ivIcon.setBackgroundResource(transaction.category.colour)
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
