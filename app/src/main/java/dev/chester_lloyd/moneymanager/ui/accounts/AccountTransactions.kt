package dev.chester_lloyd.moneymanager.ui.accounts

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.Account
import dev.chester_lloyd.moneymanager.Transaction
import dev.chester_lloyd.moneymanager.dbManager
import kotlinx.android.synthetic.main.account.view.ivIcon
import kotlinx.android.synthetic.main.account.view.tvName
import kotlinx.android.synthetic.main.activity_account_transactions.*
import kotlinx.android.synthetic.main.transaction.view.*
import java.util.*
import kotlin.collections.ArrayList

class AccountTransactions : AppCompatActivity() {

    private var account = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_transactions)

//      Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.manage_account)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        account = Account(intent.getIntExtra("accountID", 0),
            intent.getStringExtra("name"),
            intent.getDoubleExtra("balance", 0.0),
            intent.getIntExtra("icon", 0),
            intent.getIntExtra("colour", 0))

        tvName.text = account.name
        tvBalance.text = account.getStringBalance(this)
        ivIcon.setImageResource(account.icon)
        ivIcon.setBackgroundResource(account.colour)


//      Get transactions as an array list from database
        var listTransactions = loadTransactions("%")

//      Pass this to the list view adaptor and populate
        val myTransactionsAdapter = myTransactionsAdapter(listTransactions)
        this.lvTransactions.adapter = myTransactionsAdapter
    }

    //setting menu in action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit,menu)
        return super.onCreateOptionsMenu(menu)
    }


    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menuEdit -> {
//          Edit icon clicked, go to edit page (pass all account details)
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
        R.id.menuDelete ->{
//          Delete icon clicked
//          Build an alert dialog to get user confirmation
            val alertDialog = AlertDialog.Builder(this)

            alertDialog.setMessage(resources.getString(R.string.alert_message_delete))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener {
                    // TODO Delete account and transactions
                    dialog, id -> finish()
                })
                .setNegativeButton(resources.getString(R.string.no), DialogInterface.OnClickListener {
//                  Do nothing, close box
                    dialog, id -> dialog.cancel()
                })

            val alert = alertDialog.create()
            alert.setTitle(resources.getString(R.string.alert_title_delete))
            alert.show()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

//  Close activity once toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


//  Read accounts from the database and return an array of Account objects
    fun loadTransactions(name:String):ArrayList<Transaction> {
        var listTransactions = ArrayList<Transaction>()

        var dbManager = dbManager(this!!)

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
        val cal:Calendar = Calendar.getInstance()
        cal.set(2020,2,1,12,0)
        val cal2:Calendar = Calendar.getInstance()
        cal2.set(2020,2,15,6,50)

        listTransactions.add(Transaction(1, 1, "Rent", -500.52,
            cal, R.drawable.ic_account_cash, R.drawable.ic_circle_green))
        listTransactions.add(Transaction(2, 2, "Phone", -20.00,
            cal2, R.drawable.ic_account_cash, R.drawable.ic_circle_paypal))

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
            rowView.tvDate.text = transaction.getDate(applicationContext, "DMY")
            rowView.tvAmount.text = transaction.getStringAmount(applicationContext)
            rowView.ivIcon.setImageResource(transaction.icon)
            rowView.ivIcon.setBackgroundResource(transaction.colour)
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
