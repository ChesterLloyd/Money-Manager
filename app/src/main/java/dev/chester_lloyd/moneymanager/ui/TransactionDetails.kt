package dev.chester_lloyd.moneymanager.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import dev.chester_lloyd.moneymanager.DBManager
import dev.chester_lloyd.moneymanager.Payment
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.Transaction
import dev.chester_lloyd.moneymanager.ui.transactions.AddTransaction
import kotlinx.android.synthetic.main.account.view.*
import kotlinx.android.synthetic.main.activity_transation_details.*
import kotlinx.android.synthetic.main.transaction.view.ivIcon
import kotlinx.android.synthetic.main.transaction.view.tvName

class TransactionDetails : AppCompatActivity() {

    private var transaction = Transaction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transation_details)

//      Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.transaction_details)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        transaction = DBManager(this).selectTransaction(intent
            .getIntExtra("transactionID", 0))

        tvName.text = transaction.merchant
        tvAmount.text = transaction.getStringAmount(this)

        val iconManager = IconManager(this)
        ivIcon.setImageResource(iconManager.getIconByID(
            iconManager.categoryIcons, transaction.category.icon).drawable)
        ivIcon.setBackgroundResource(iconManager.getIconByID(
            iconManager.colourIcons, transaction.category.colour).drawable)
    }

//  If we have come back (after updating) show potential updated account status
    override fun onResume() {
        super.onResume()

        if (intent.getIntExtra("transactionID", 0) > 0) {
//          Read current transaction from database
            transaction = DBManager(this).selectTransaction(intent
                .getIntExtra("transactionID", 0))
        }

//      Update entry fields with account info
        tvName.text = transaction.merchant
        tvAmount.text = transaction.getStringAmount(this)
        val iconManager = IconManager(this)
        ivIcon.setImageResource(iconManager.getIconByID(
            iconManager.categoryIcons, transaction.category.icon).drawable)
        ivIcon.setBackgroundResource(iconManager.getIconByID(
            iconManager.colourIcons, transaction.category.colour).drawable)

//      Show details text if user has written it
        if (transaction.details != null) {
            tvDetails.text = transaction.details
            tvDetails.visibility = View.VISIBLE
        }

//      Get payments as an array list from database
        val listPayments = DBManager(this)
            .selectPayment(transaction.transactionID)

//      Pass this to the list view adaptor and populate
        val myPaymentsAdapter = PaymentsAdapter(listPayments)
        this.lvPayments.adapter = myPaymentsAdapter
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
            val intent = Intent(this, AddTransaction::class.java)
            val bundle = Bundle()
            bundle.putInt("transactionID", transaction.transactionID)
            intent.putExtras(bundle)
            startActivity(intent)
            true
        }
        R.id.menuDelete ->{
//          Delete icon clicked
//          Build an alert dialog to get user confirmation
            val alertDialog = AlertDialog.Builder(this)

            alertDialog.setMessage(resources.getString(R.string.alert_message_delete_transaction))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, id -> finish()
//                  Delete the account
//                    dbManager(this).delete("Accounts","ID=?",
//                        arrayOf(account.accountID.toString()))
                }
                .setNegativeButton(resources.getString(R.string.no)) {
//                  Do nothing, close box
                        dialog, id -> dialog.cancel()
                }

            val alert = alertDialog.create()
            alert.setTitle(resources.getString(R.string.alert_title_delete_transaction))
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

    inner class PaymentsAdapter(private var listPaymentsAdapter: ArrayList<Payment>) :
        BaseAdapter() {

        @SuppressLint("InflateParams", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//          Adds each payment to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.account, null)
            val payment = listPaymentsAdapter[position]

            val iconManager = IconManager(applicationContext)
            rowView.ivIcon.setImageResource(iconManager.getIconByID(
                iconManager.accountIcons, payment.account.icon).drawable)
            rowView.ivIcon.setBackgroundResource(iconManager.getIconByID(
                iconManager.colourIcons, payment.account.colour).drawable)

            rowView.tvName.text = payment.account.name
            rowView.tvBalance.text = payment.getStringAmount(applicationContext)
            return rowView
        }

        override fun getItem(position: Int): Any {
            return listPaymentsAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listPaymentsAdapter.size
        }
    }
}
