package dev.chester_lloyd.moneymanager.ui.accounts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.dbManager
import dev.chester_lloyd.moneymanager.Account
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

        var balance = ""

        etBalance.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//              Update balance before changes have been made (i.e user changed it)
                balance = s.toString()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//              Get old position of decimal point
                val oldDecimalPos = balance.indexOf('.')
                var newBalance = ""

//              Where to put the cursor if text is replaced
                var cursorPos = 0
                var decimalCount = 0;

                var diff = balance.length - s.length;

//              Check if balance contains multiple - or . or over 2dp
                for (i in 0..s.length - 1) {
                    if (s[i] == '-') {
//                      Check if current character is a - sign
                        if (i == 0) {
//                          Check if this was found at the start, if so add to output string
                            newBalance += s[i]
                        } else {
//                          If not, update cursor position to here as this char was removed
                            cursorPos = i
                        }
                    } else if (s[i] == '.') {
//                      Check if current character is a . sign

                        if (decimalCount == 0) {
//                          Check if no decimal points have been added to the output yet

                            if (i >= oldDecimalPos) {
//                              We are adding the decimal at the position of the old one
//                              (or the last in the input), so add it
                                decimalCount++
                                newBalance += s[i]
                            } else if (i == oldDecimalPos - diff) {
//                              Some characters have been removed before it, so add this one
                                decimalCount++
                                newBalance += s[i]
                            } else {
//                              Do not add this decimal point, update cursor position to here
                                cursorPos = i
                            }
                        } else {
//                          More than 1 decimal point being added, update cursor position to here
                            cursorPos = i
                        }
                    } else {
                        // This is an allowed digit, keep it
                        newBalance += s[i]
                    }
                }

                if (decimalCount == 1) {
//                  Check if a decimal point is present first
                    val splitBalance = newBalance.split(".")
                    if (splitBalance[1].length > 2) {
//                      If there are more than 2 numbers after dp, remove any past the 2
                        newBalance = splitBalance[0] + "." + splitBalance[1].dropLast((splitBalance[1].length - 2))
                        cursorPos = newBalance.length
                    }
                }

//              Update balance and cursor position
                if (etBalance.text.toString() != newBalance) {
                    etBalance.setText(newBalance)
                    etBalance.setSelection(cursorPos)
                }
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
                Toast.makeText(this, "Account name cannot be blank", Toast.LENGTH_SHORT).show()
            } else if (etBalance.text.toString() == "") {
//              Account balance is empty, show an error
                Toast.makeText(this, "Account balance cannot be blank", Toast.LENGTH_SHORT).show()
            } else {
//              All data has been filled out, start saving
                account.balance = etBalance.text.toString().toDouble()
                println(account.toString())

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
