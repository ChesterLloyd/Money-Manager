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
import androidx.appcompat.app.AppCompatActivity
import dev.chester_lloyd.moneymanager.Category
import dev.chester_lloyd.moneymanager.DBManager
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.Transaction
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.TransactionDetails
import kotlinx.android.synthetic.main.activity_category_transaction.*
import kotlinx.android.synthetic.main.transaction.view.*

class CategoryTransaction : AppCompatActivity() {

    private var category = Category()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_transaction)

//      Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.manage_category)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        category = Category(intent.getIntExtra("categoryID", 0),
            intent.getStringExtra("name"),
            intent.getIntExtra("icon", 0),
            intent.getIntExtra("colour", 0))

        tvName.text = category.name

        val iconManager = IconManager(this)
        ivIcon.setImageResource(iconManager.getIconByID(
            iconManager.categoryIcons, category.icon).drawable)
        ivIcon.setBackgroundResource(iconManager.getIconByID(
            iconManager.colourIcons, category.colour).drawable)

//      Get transactions as an array list from database
        val listTransactions = DBManager(this)
            .selectTransaction(category.categoryID, "Categories", null)

//      Pass this to the list view adaptor and populate
        val myTransactionsAdapter = TransactionsAdapter(listTransactions)
        this.lvTransactions.adapter = myTransactionsAdapter
    }

//  If we have come back (after updating) show potential updated account status
    override fun onResume() {
        super.onResume()

        if (intent.getIntExtra("categoryID", 0) > 0) {
//          Read current account from database
            val dbManager = DBManager(this)
            category = dbManager.selectCategory(intent.getIntExtra("categoryID", 0))
        }

//      Update entry fields with account info
        tvName.text = category.name
        val iconManager = IconManager(this)
        ivIcon.setImageResource(iconManager.getIconByID(
            iconManager.categoryIcons, category.icon).drawable)
        ivIcon.setBackgroundResource(iconManager.getIconByID(
            iconManager.colourIcons, category.colour).drawable)

//      When a transaction in the list is clicked
        this.lvTransactions.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
//              Get transaction object of item that is clicked
                val transaction = lvTransactions.getItemAtPosition(position) as Transaction

//              Setup an intent to send this across to view the transaction's details
                val intent = Intent(this, TransactionDetails::class.java)
                val bundle = Bundle()
                bundle.putInt("transactionID", transaction.transactionID)
                intent.putExtras(bundle)
                startActivity(intent)
            }
    }

//  Setting menu in action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit,menu)
        return super.onCreateOptionsMenu(menu)
    }

// Actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menuEdit -> {
//          Edit icon clicked, go to edit page (pass all category details)
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
        R.id.menuDelete ->{
//          Delete icon clicked
//          Build an alert dialog to get user confirmation
            val alertDialog = AlertDialog.Builder(this)

            alertDialog.setMessage(resources.getString(R.string.alert_message_delete_category))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, id -> finish()
//                  Delete the category
                    DBManager(this).delete(DBManager(this).dbCategoryTable,
                        arrayOf(category.categoryID.toString()))
                }
                .setNegativeButton(resources.getString(R.string.no)) {
//                  Do nothing, close box
                    dialog, id -> dialog.cancel()
                }

            val alert = alertDialog.create()
            alert.setTitle(resources.getString(R.string.alert_title_delete_category))
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

    inner class TransactionsAdapter(private var listTransactionsAdapter: ArrayList<Transaction>) :
        BaseAdapter() {

        @SuppressLint("InflateParams", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//          Adds each transaction to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.transaction, null)
            val transaction = listTransactionsAdapter[position]
            rowView.tvName.text = transaction.merchant
            rowView.tvDate.text = transaction.getDate(applicationContext, "DMY")
            rowView.tvAmount.text = transaction.getStringAmount(applicationContext)

            val iconManager = IconManager(applicationContext)
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
