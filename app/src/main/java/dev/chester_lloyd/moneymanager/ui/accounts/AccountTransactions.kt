package dev.chester_lloyd.moneymanager.ui.accounts

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import dev.chester_lloyd.moneymanager.Account
import dev.chester_lloyd.moneymanager.R

class AccountTransactions : AppCompatActivity() {

    private var account = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_transactions)

//      Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.account_transactions)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)


        account = Account(intent.getIntExtra("accountID", 0),
            intent.getStringExtra("name"),
            intent.getDoubleExtra("balance", 0.0),
            intent.getIntExtra("icon", 0),
            intent.getIntExtra("colour", 0))
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
}
