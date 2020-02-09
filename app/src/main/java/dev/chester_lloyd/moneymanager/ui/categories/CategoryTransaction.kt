package dev.chester_lloyd.moneymanager.ui.categories

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
import dev.chester_lloyd.moneymanager.Category
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.Transaction
import dev.chester_lloyd.moneymanager.dbManager
import kotlinx.android.synthetic.main.activity_category_transaction.*
import kotlinx.android.synthetic.main.transaction.view.*
import java.util.*
import kotlin.collections.ArrayList

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
        ivIcon.setImageResource(category.icon)
        ivIcon.setBackgroundResource(category.colour)


//      Get transactions as an array list from database
        var listTransactions = loadTransactions("%")

//      Pass this to the list view adaptor and populate
        val myTransactionsAdapter = myTransactionsAdapter(listTransactions)
        this.lvTransactions.adapter = myTransactionsAdapter
    }

//  If we have come back (after updating) show potential updated account status
    override fun onResume() {
        super.onResume()

        if (intent.getIntExtra("categoryID", 0) > 0) {
//          Read current account from database
            val dbManager = dbManager(this!!)
            category = dbManager.selectCategory(intent.getIntExtra("categoryID", 0))
        }

//      Update entry fields with account info
        tvName.text = category.name
        ivIcon.setImageResource(category.icon)
        ivIcon.setBackgroundResource(category.colour)
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
                .setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener {
                    dialog, id -> finish()
//                  Delete the category
                    dbManager(this).delete("Categories","ID=?",
                        arrayOf(category.categoryID.toString()))
                })
                .setNegativeButton(resources.getString(R.string.no), DialogInterface.OnClickListener {
//                  Do nothing, close box
                        dialog, id -> dialog.cancel()
                })

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


//  Read transactions from the database and return an array of Transaction objects
    fun loadTransactions(name:String):ArrayList<Transaction> {
        var listTransactions = ArrayList<Transaction>()
        var dbManager = dbManager(this!!)

        var listCategories = ArrayList<Category>()


        val projection = arrayOf("ID", "Name", "Icon", "Colour")
        val selectionArgs = arrayOf(name)

        // Each ? represents an arg in array
        val cursor = dbManager.query(dbManager.dbCategoryTable, projection, "Name like ?", selectionArgs, "Name")

        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val name = cursor.getString(cursor.getColumnIndex("Name"))
                val icon = cursor.getInt(cursor.getColumnIndex("Icon"))
                val colour = cursor.getInt(cursor.getColumnIndex("Colour"))

                listCategories.add(Category(ID, name, icon, colour))
            } while (cursor.moveToNext())
        }





//        val projection = arrayOf("ID", "Name", "Balance", "Icon", "Colour")
//        val selectionArgs = arrayOf(name)
//
//        // Each ? represents an arg in array
//        val cursor = dbManager.query("Transactions", projection, "Name like ?", selectionArgs, "Name")
//
//        if (cursor.moveToFirst()) {
//            do {
//                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
//                val name = cursor.getString(cursor.getColumnIndex("Name"))
//                val balance = cursor.getDouble(cursor.getColumnIndex("Balance"))
//                val icon = cursor.getInt(cursor.getColumnIndex("Icon"))
//                val colour = cursor.getInt(cursor.getColumnIndex("Colour"))
//
//                listTransactions.add(Transaction(ID, name, balance, icon, colour))
//            } while (cursor.moveToNext())
//        }

//      TODO READ transactions from DB
//      USe some generated dates and transactions for now
        val cal:Calendar = Calendar.getInstance()
        cal.set(2020,2,1,12,0)
        val cal2:Calendar = Calendar.getInstance()
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
            rowView.tvDate.text = transaction.getDate(applicationContext, "DMY")
            rowView.tvAmount.text = transaction.getStringAmount(applicationContext)
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