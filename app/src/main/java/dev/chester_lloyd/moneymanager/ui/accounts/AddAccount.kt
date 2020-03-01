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
import dev.chester_lloyd.moneymanager.DBManager
import dev.chester_lloyd.moneymanager.ui.CurrencyValidator
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.IconSpinner
import kotlinx.android.synthetic.main.activity_add_account.*

/**
 * An [AppCompatActivity] subclass to add or edit an [Account].
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class AddAccount : AppCompatActivity() {

    /**
     * An [onCreate] method that sets up the supportActionBar, icon spinners, balance validator,
     * FAB and view.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_account)

        val iconManager = IconManager(this)

        // Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.button_add_account)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up the account icon spinner
        val iconSpinner = findViewById<Spinner>(R.id.spIcon)
        iconSpinner.adapter = IconSpinner(
            applicationContext,
            iconManager.accountIcons, null, "icon"
        )

        // Set up the account color spinner
        val colourSpinner = findViewById<Spinner>(R.id.spColour)
        colourSpinner.adapter = IconSpinner(
            applicationContext,
            null, iconManager.colourIcons, "colour"
        )

        // Validate the balance field
        val balanceValidator = CurrencyValidator(etBalance)

        val etBalanceInput = findViewById<EditText>(R.id.etBalance)
        etBalanceInput.onFocusChangeListener = OnFocusChangeListener { _, gainFocus ->
            balanceValidator.focusListener(gainFocus)
        }

        etBalance.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Update balance before changes have been made (i.e user changed it)
                balanceValidator.beforeTextChangedListener(s)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                balanceValidator.onTextChangedListener(s)
            }
        })

        val account = Account()
        account.accountID = intent.getIntExtra("accountID", 0)

        // If the account ID > 0 (not a new one) then auto fill these fields with the saved values
        if (account.accountID > 0) {
            this.supportActionBar?.title = getString(R.string.edit_account)
            tvDesc.setText(R.string.text_edit_account_desc)
            etName.setText(intent.getStringExtra("name"))
            etBalance.setText(
                CurrencyValidator.getEditTextAmount(
                    intent.getDoubleExtra(
                        "balance", 0.0
                    )
                )
            )

            spIcon.setSelection(
                iconManager.getIconPositionID(
                    iconManager.accountIcons,
                    intent.getIntExtra("icon", 0)
                )
            )

            spColour.setSelection(
                iconManager.getIconPositionID(
                    iconManager.colourIcons,
                    intent.getIntExtra("colour", 0)
                )
            )
        }

        // Save or update the account on FAB click
        fabAddAccount.setOnClickListener {
            account.name = etName.text.toString()

            if (account.name == "") {
                // Account name is empty, show an error
                Toast.makeText(this, R.string.account_validation_name, Toast.LENGTH_SHORT).show()
            } else if (etBalance.text.toString() == "") {
                // Account balance is empty, show an error
                Toast.makeText(this, R.string.account_validation_balance, Toast.LENGTH_SHORT).show()
            } else {
                // All data has been filled out, start saving
                account.balance = balanceValidator.getBalance()

                // Get instance of the database manager class
                val dbManager = DBManager(this)

                if (account.accountID == 0) {
                    // Insert this new account into the accounts table
                    val id = dbManager.insertAccount(account)
                    if (id > 0) {
                        // Account saved to database, return to previous accounts fragment
                        Toast.makeText(this, R.string.account_insert_success, Toast.LENGTH_LONG)
                            .show()
                        this.finish()
                    } else {
                        // Failed to save, show this error
                        Toast.makeText(this, R.string.account_insert_fail, Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    // Update this account in the database
                    val selectionArgs = arrayOf(account.accountID.toString())
                    val id = dbManager.updateAccount(account, "ID=?", selectionArgs)
                    if (id > 0) {
                        // Account updated in the database, return to previous accounts fragment
                        Toast.makeText(this, R.string.account_update_success, Toast.LENGTH_LONG)
                            .show()
                        this.finish()
                    } else {
                        // Failed to save, show this error
                        Toast.makeText(this, R.string.account_update_fail, Toast.LENGTH_LONG)
                            .show()
                    }
                }
                dbManager.sqlDB!!.close()
            }
        }

        // Add selected icon to account object
        spIcon?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                account.icon = iconManager.accountIcons[position].id
            }
        }

        // Add selected colour to account object
        spColour?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                account.colour = iconManager.colourIcons[position].id
            }
        }
    }

    /**
     * An [onSupportNavigateUp] method that closes this activity (goes to previous page) once
     * toolbar back button is pressed.
     *
     * @return true if Up navigation completed successfully and this Activity was finished, false
     * otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
