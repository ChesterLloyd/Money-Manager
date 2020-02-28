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

        val iconManager = IconManager(this)
        ivIcon.setImageResource(iconManager.getIconByID(
            iconManager.accountIcons, account.icon).drawable)
        ivIcon.setBackgroundResource(iconManager.getIconByID(
            iconManager.colourIcons, account.colour).drawable)
    }

//  If we have come back (after updating) show potential updated account status
    override fun onResume() {
        super.onResume()

        if (intent.getIntExtra("accountID", 0) > 0) {
//          Read current account from database
            val dbManager = DBManager(this)
            account = dbManager.selectAccount(intent.getIntExtra("accountID", 0))
        }

//      Update entry fields with account info
        tvName.text = account.name
        tvBalance.text = account.getStringBalance(this)
        val iconManager = IconManager(this)
        ivIcon.setImageResource(iconManager.getIconByID(
            iconManager.accountIcons, account.icon).drawable)
        ivIcon.setBackgroundResource(iconManager.getIconByID(
            iconManager.colourIcons, account.colour).drawable)

//      Get transactions (as payments) as an array list from database
        val listPayments = DBManager(this)
            .selectPayment(account.accountID, "accounts")

//      Pass this to the list view adaptor and populate
        val transactionsAdapter = TransactionsAdapter(listPayments)
        this.lvTransactions.adapter = transactionsAdapter

//      When a transaction in the list is clicked
        this.lvTransactions.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
//              Get transaction object of item that is clicked
                val payment = lvTransactions.getItemAtPosition(position) as Payment

//              Setup an intent to send this across to view the transaction's details
                val intent = Intent(this, TransactionDetails::class.java)
                val bundle = Bundle()
                bundle.putInt("transactionID", payment.transaction.transactionID)
                intent.putExtras(bundle)
                startActivity(intent)
            }
    }

//  Settings menu in action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit,menu)
        return super.onCreateOptionsMenu(menu)
    }

//  Actions on click menu items
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

            alertDialog.setMessage(resources.getString(R.string.alert_message_delete_account))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, id -> finish()
//                  Delete the account
                    DBManager(this).delete(DBManager(this).dbAccountTable,
                        arrayOf(account.accountID.toString()))
                }
                .setNegativeButton(resources.getString(R.string.no)) {
//                  Do nothing, close box
                    dialog, id -> dialog.cancel()
                }

            val alert = alertDialog.create()
            alert.setTitle(resources.getString(R.string.alert_title_delete_account))
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

    inner class TransactionsAdapter(private var listTransactionsAdapter: ArrayList<Payment>) :
        BaseAdapter() {

        @SuppressLint("InflateParams", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//          Adds each transaction to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.transaction, null)
            val payment = listTransactionsAdapter[position]
            rowView.tvName.text = payment.transaction.merchant
            rowView.tvDate.text = payment.transaction.getDate(applicationContext, "DMY")
//            rowView.tvAmount.text = payment.getStringAmount(applicationContext)
            rowView.tvAmount.text = payment.getStringAmount(applicationContext)

            val iconManager = IconManager(applicationContext)
            rowView.ivIcon.setImageResource(iconManager.getIconByID(
                iconManager.categoryIcons, payment.transaction.category.icon).drawable)
            rowView.ivIcon.setBackgroundResource(iconManager.getIconByID(
                iconManager.colourIcons, payment.transaction.category.colour).drawable)

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
