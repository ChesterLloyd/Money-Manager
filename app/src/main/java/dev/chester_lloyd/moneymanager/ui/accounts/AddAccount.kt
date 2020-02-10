package dev.chester_lloyd.moneymanager.ui.accounts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.chester_lloyd.moneymanager.Account
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.dbManager
import dev.chester_lloyd.moneymanager.ui.CurrencyValidator
import dev.chester_lloyd.moneymanager.ui.IconSpinner
import kotlinx.android.synthetic.main.activity_add_account.*

class AddAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_account)

//      Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.button_add_account)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

//      Set up the account icon spinner
        val spinner = findViewById<Spinner>(R.id.spIcon)
        val imageName = resources.getStringArray(R.array.account_names)
        val image = intArrayOf(
            R.drawable.ic_account_bank,
            R.drawable.ic_account_business,
            R.drawable.ic_account_cash,
            R.drawable.ic_account_credit_card,
            R.drawable.ic_account_dollar,
            R.drawable.ic_account_gift_card,
            R.drawable.ic_account_joint,
            R.drawable.ic_account_membership_card,
            R.drawable.ic_account_paypal,
            R.drawable.ic_account_travel_card,
            R.drawable.ic_account_wallet
        )
        spinner.adapter = IconSpinner(
            applicationContext,
            image,
            IntArray(0),
            imageName,
            "icon"
        )

//      Set up the account color spinner
        val colourSpinner = findViewById<Spinner>(R.id.spColour)
        val colourName = resources.getStringArray(R.array.colour_names)
        val colour = intArrayOf(
            R.drawable.ic_circle_green,
            R.drawable.ic_circle_dark_blue,
            R.drawable.ic_circle_paypal
        )
        colourSpinner.adapter = IconSpinner(
            applicationContext,
            colour,
            IntArray(0),
            colourName,
            "colour"
        )

//      Validate the balance field
        val balanceValidator = CurrencyValidator(etBalance)

        val etBalanceInput = findViewById(R.id.etBalance) as EditText
        etBalanceInput.onFocusChangeListener = OnFocusChangeListener { v, gainFocus ->
            balanceValidator.focusListener(gainFocus)
        }

        etBalance.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//              Update balance before changes have been made (i.e user changed it)
                balanceValidator.beforeTextChangedListener(s)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                balanceValidator.onTextChangedListener(s)
            }
        })

        val account = Account()

        account.accountID = intent.getIntExtra("accountID", 0)

//      If the account ID > 0 (not a new one) then auto fill these fields with the saved values
        if (account.accountID > 0) {
            etName.setText(intent.getStringExtra("name"))
            etBalance.setText(intent.getDoubleExtra("balance", 0.0).toString())

            spIcon.setSelection(image.indexOf(intent.getIntExtra("icon", 0)))
            spColour.setSelection(colour.indexOf(intent.getIntExtra("colour", 0)))
        }

        fabAddAccount.setOnClickListener {
            account.name = etName.text.toString()

            if (account.name == "") {
//              Account name is empty, show an error
                Toast.makeText(this, R.string.account_validation_name, Toast.LENGTH_SHORT).show()
            } else if (etBalance.text.toString() == "") {
//              Account balance is empty, show an error
                Toast.makeText(this, R.string.account_validation_balance, Toast.LENGTH_SHORT).show()
            } else {
//              All data has been filled out, start saving
                account.balance = balanceValidator.getBalance()

//              Get instance of the database manager class
                val dbManager = dbManager(this)

                if (account.accountID == 0) {
//                  Insert this new account into the accounts table
                    val id = dbManager.insertAccount(account)
                    if (id > 0) {
//                      Account saved to database, return to previous accounts fragment
                        Toast.makeText(this, R.string.account_insert_success, Toast.LENGTH_LONG).show()
                        this.finish()
                    } else {
//                      Failed to save, show this error
                        Toast.makeText(this, R.string.account_insert_fail, Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
//                  Update this account in the database
                    var selectionArgs = arrayOf(account.accountID.toString())
                    val id = dbManager.updateAccount(account, "ID=?", selectionArgs)
                    if (id > 0) {
//                      Account updated in the database, return to previous accounts fragment
                        Toast.makeText(this, R.string.account_update_success, Toast.LENGTH_LONG).show()
                        this.finish()
                    } else {
//                      Failed to save, show this error
                        Toast.makeText(this, R.string.account_update_fail, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

//      Add selected icon to account object
        spIcon?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                account.icon = image[position]
            }
        }

//      Add selected colour to account object
        spColour?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                account.colour = colour[position]
            }
        }
    }

//  Close activity once toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
