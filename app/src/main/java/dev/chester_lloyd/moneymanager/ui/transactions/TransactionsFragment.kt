package dev.chester_lloyd.moneymanager.ui.transactions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.chester_lloyd.moneymanager.*
import dev.chester_lloyd.moneymanager.ui.accounts.AddAccount
import kotlinx.android.synthetic.main.fragment_transactions.*
import kotlinx.android.synthetic.main.fragment_transactions.view.*
import kotlinx.android.synthetic.main.transaction.view.*
import java.util.*
import kotlin.collections.ArrayList

class TransactionsFragment : Fragment() {

    private lateinit var transactionsViewModel: TransactionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        transactionsViewModel =
            ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_transactions, container, false)

//      Get transactions as an array list from database
        var listTransactions = loadTransactions("%")

//      Pass this to the list view adaptor and populate
        val myTransactionsAdapter = myTransactionsAdapter(listTransactions)
        root.lvTransactions.adapter = myTransactionsAdapter

//      Launch new transaction activity with fab
        val fab: FloatingActionButton = root.findViewById(R.id.fab1)
        fab.setOnClickListener {
//            val intent = Intent(context, AddTransaction::class.java)
//            val intent = Intent(context, Main2Activity::class.java)

//            startActivity(intent)
        }

        return root
    }






//  Read transactions from the database and return an array of Transaction objects
    fun loadTransactions(name:String):ArrayList<Transaction> {
        var listTransactions = ArrayList<Transaction>()

        var dbManager = dbManager(context!!)

//        val projection = arrayOf("ID", "Name", "Balance", "Icon", "Colour")
//        val selectionArgs = arrayOf(name)
//
//        // Each ? represents an arg in array
//        val cursor = dbManager.query("Accounts", projection, "Name like ?", selectionArgs, "Name")
//
//        if (cursor.moveToFirst()) {
//            do {
//                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
//                val name = cursor.getString(cursor.getColumnIndex("Name"))
//                val balance = cursor.getDouble(cursor.getColumnIndex("Balance"))
//                val icon = cursor.getInt(cursor.getColumnIndex("Icon"))
//                val colour = cursor.getInt(cursor.getColumnIndex("Colour"))
//
//                listAccounts.add(Account(ID, name, balance, icon, colour))
//            } while (cursor.moveToNext())
//        }

//      TODO READ transactions from DB
//      USe some generated dates and transactions for now
        val cal: Calendar = Calendar.getInstance()
        cal.set(2020,2,1,12,0)
        val cal2: Calendar = Calendar.getInstance()
        cal2.set(2020,2,15,6,50)

        listTransactions.add(Transaction(1, Category(1, "Bills", R.drawable.ic_category_places_hotel, R.drawable.ic_circle_green), "Rent", cal, -500.53))
        listTransactions.add(Transaction(2, Category(2, "Phone", R.drawable.ic_category_computer_phone, R.drawable.ic_circle_dark_blue), "VOXI", cal2, -20.00))

        return listTransactions
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