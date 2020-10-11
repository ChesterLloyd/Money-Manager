package dev.chester_lloyd.moneymanager.ui.accounts

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import dev.chester_lloyd.moneymanager.*
import dev.chester_lloyd.moneymanager.MainActivity.Companion.TRANSFER_CATEGORY_ID
import dev.chester_lloyd.moneymanager.ui.CurrencyValidator
import dev.chester_lloyd.moneymanager.ui.Icon
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.IconSpinner
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.activity_transfer_funds.*
import kotlinx.android.synthetic.main.activity_transfer_funds.etAmount
import kotlinx.android.synthetic.main.activity_transfer_funds.etDate
import kotlinx.android.synthetic.main.activity_transfer_funds.tvDesc
import kotlinx.android.synthetic.main.activity_transfer_funds.tvSuffix
import kotlinx.android.synthetic.main.activity_transfer_funds.tvSymbol
import java.text.SimpleDateFormat
import java.util.*

/**
 * An [AppCompatActivity] subclass to transfer funds between [Account]s.
 *
 * @author Chester Lloyd
 * @since 1.2
 */
@Suppress("NAME_SHADOWING")
class TransferFunds : AppCompatActivity() {

    private var transferDate: Calendar = Calendar.getInstance()
    private var accountSource = Account()
    private var accountDestination = Account()
    private var transactionSource = Transaction()
    private var transactionDestination = Transaction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_funds)

        // Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.transfer_funds)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Add the currency symbol and suffix to the amount row
        val format = MainActivity.getCurrencyFormat(this)
        tvSymbol.text = format[0]
        tvSuffix.text = format[3]

        val dbManager = DBManager(this)

        // Get transaction from database, if ID given (to edit)
        val transactionID = intent.getIntExtra("transactionID", 0)

        if (transactionID > 0) {
            transactionSource = dbManager.selectTransaction(transactionID)
            transactionDestination =
                dbManager.selectTransaction(transactionSource.transferTransactionID)
            accountSource = dbManager.getAccountByTransaction(transactionSource)!!
            accountDestination = dbManager.getAccountByTransaction(transactionDestination)!!
        }

        // Setup the date to the current device date
        val etDate = this.etDate
        updateDateInView()

        // Create a date picker, set values for class date value
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                transferDate.set(Calendar.YEAR, year)
                transferDate.set(Calendar.MONTH, monthOfYear)
                transferDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        // When the date edit text has focus (clicked), open the date picker
        etDate.onFocusChangeListener = View.OnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                DatePickerDialog(
                    this@TransferFunds,
                    dateSetListener,
                    // set to point to today's date when it loads up
                    transferDate.get(Calendar.YEAR),
                    transferDate.get(Calendar.MONTH),
                    transferDate.get(Calendar.DAY_OF_MONTH)
                ).show()
                etDate.clearFocus()
            }
        }

        // Setup the source and destination icon spinners
        val accounts: ArrayList<Account> = dbManager.selectAccounts("active", null)

        // If we are editing a transaction, make sure to add any deleted accounts that were involved
        if (transactionID > 0) {
            var accountSourcePresent = false
            var accountDestinationPresent = false
            for (account in 0 until accounts.size) {
                if (accounts[account].accountID == accountSource.accountID) {
                    accountSourcePresent = true
                } else if (accounts[account].accountID == accountDestination.accountID) {
                    accountDestinationPresent = true
                }
            }

            if (!accountSourcePresent) {
                accounts.add(accountSource)
            }
            if (!accountDestinationPresent) {
                accounts.add(accountDestination)
            }
        }

        val iconManager = IconManager(this)
        val icons = arrayOfNulls<Icon>(accounts.size)
        val backgrounds = arrayOfNulls<Icon>(accounts.size)

        for (account in 0 until accounts.size) {
            icons[account] = Icon(
                account,
                iconManager.getIconByID(
                    iconManager.accountIcons,
                    accounts[account].icon
                ).drawable,
                accounts[account].name!!,
                accounts[account].colour
            )

            backgrounds[account] = Icon(
                account,
                iconManager.getIconByID(
                    iconManager.colourIcons,
                    accounts[account].colour
                ).drawable,
                "",
                null
            )
        }

        val sourceSpinner = findViewById<Spinner>(R.id.spSource)
        sourceSpinner.adapter = IconSpinner(
            applicationContext, icons.requireNoNulls(), backgrounds.requireNoNulls(), "icon"
        )
        val destinationSpinner = findViewById<Spinner>(R.id.spDestination)
        destinationSpinner.adapter = IconSpinner(
            applicationContext, icons.requireNoNulls(), backgrounds.requireNoNulls(), "icon"
        )

        // Add selected account to transaction source/destination object
        spSource.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                accountSource = accounts[position]
            }
        }
        spDestination.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                accountDestination = accounts[position]
            }
        }

        // Validate the amount field
        val amountValidator = CurrencyValidator(etAmount)
        etAmount.keyListener = DigitsKeyListener.getInstance("0123456789${format[2]}")
        etAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Update balance before changes have been made (i.e user changed it)
                amountValidator.beforeTextChangedListener(s)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                amountValidator.onTextChangedListener(s, format[2])
            }
        })

        if (transactionID > 0) {
            this.supportActionBar?.title = getString(R.string.edit_transaction)
            tvDesc.setText(R.string.text_edit_transaction_desc)
            etAmount.setText(CurrencyValidator.getEditTextAmount(transactionSource.amount, format[2]))
            updateDateInView()

            for (account in 0 until accounts.size) {
                if (accounts[account].accountID == accountSource.accountID) {
                    spSource.setSelection(account)
                } else if (accounts[account].accountID == accountDestination.accountID) {
                    spDestination.setSelection(account)
                }
            }
        }

        // Save or update the transactions on FAB click
        fabTransferFunds.setOnClickListener {
            if (etAmount.text.toString() == "") {
                // Transaction amount is empty, show an error
                Toast.makeText(
                    this, R.string.transaction_validation_amount,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (etAmount.text.toString() == format[2]) {
                // Transaction amount is the decimal sign only, show an error
                Toast.makeText(
                    this, R.string.transaction_validation_amount_invalid,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (amountValidator.getBalance(format[2]) == 0.0) {
                // Transaction amount is zero (or the currency equivalent), show an error
                Toast.makeText(
                    this, R.string.transaction_validation_amount_zero,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (etDate.text.toString() == "") {
                // Transaction date is empty, show an error
                Toast.makeText(
                    this, R.string.transaction_validation_date,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (accountSource.accountID == accountDestination.accountID) {
                // Source and destination are the same, show an error
                Toast.makeText(
                    this, R.string.transfer_same_accounts,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // All data has been filled out, start saving
                val dbManager = DBManager(this)

                // Update source transaction with selected details
                transactionSource.category = dbManager.selectCategory(TRANSFER_CATEGORY_ID)
                transactionSource.merchant = this.resources.getString(
                    R.string.transfer_to,
                    accountSource.name,
                    accountDestination.name
                )
                transactionSource.details = this.resources.getString(
                    R.string.transfer_from_to,
                    accountSource.name,
                    accountDestination.name
                )
                transactionSource.amount = (amountValidator.getBalance(format[2]) * -1)

                // Update destination transaction with selected details
                transactionDestination.category = transactionSource.category
                transactionDestination.merchant = this.resources.getString(
                    R.string.transfer_to,
                    accountDestination.name,
                    accountSource.name
                )
                transactionDestination.details = this.resources.getString(
                    R.string.transfer_from_to,
                    accountDestination.name,
                    accountSource.name
                )
                transactionDestination.amount = (transactionSource.amount * -1)
                transactionDestination.date = transactionSource.date

                var saveSuccess = false
                if (transactionSource.transactionID == 0) {
                    // Insert two new transactions into the transactions table for the transfer
                    val idSource = dbManager.insertTransaction(transactionSource)
                    if (idSource > 0) {
                        transactionSource.transactionID = idSource.toInt()

                        // Update destination transaction with ID of newly saved source transaction
                        transactionDestination.transferTransactionID = idSource.toInt()

                        val idDest = dbManager.insertTransaction(transactionDestination)
                        if (idDest > 0) {
                            transactionDestination.transactionID = idDest.toInt()

                            // Update source transaction with ID of newly saved destination transaction
                            transactionSource.transferTransactionID =
                                transactionDestination.transactionID
                            val selectionArgs = arrayOf(transactionSource.transactionID.toString())
                            dbManager.updateTransaction(
                                transactionSource, "ID=?",
                                selectionArgs
                            )

                            // Add source and destination payments
                            dbManager.insertPayment(
                                Payment(
                                    transactionSource,
                                    accountSource,
                                    transactionSource.amount
                                )
                            )
                            dbManager.insertPayment(
                                Payment(
                                    transactionDestination,
                                    accountDestination,
                                    transactionDestination.amount
                                )
                            )
                            saveSuccess = true
                            dbManager.sqlDB!!.close()

                            // Transaction saved to database, return to previous fragment
                            Toast.makeText(
                                this, R.string.transaction_insert_success,
                                Toast.LENGTH_LONG
                            ).show()
                            setResult(RESULT_OK)
                            this.finish()
                        }
                    }
                    if (!saveSuccess) {
                        dbManager.sqlDB!!.close()
                        // Failed to save, show this error
                        Toast.makeText(
                            this, R.string.transaction_insert_fail,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // Update this transfer in the database
                    val paymentSource = dbManager.selectPayments(
                        accountSource.accountID,
                        "account"
                    )[0]
                    paymentSource.amount = transactionSource.amount
                    val paymentDestination = dbManager.selectPayments(
                        accountDestination.accountID,
                        "account"
                    )[0]
                    paymentDestination.amount = transactionDestination.amount

                    val selectionArgsSource = arrayOf(transactionSource.transactionID.toString())
                    val idSource = dbManager.updateTransaction(
                        transactionSource, "ID=?",
                        selectionArgsSource
                    )
                    if (idSource > 0) {
                        // Update the source payment
                        println("UPDATING source payment tid ${transactionSource.transactionID} aid ${paymentSource.account.accountID}")
                        dbManager.updatePayment(
                            paymentSource,
                            "TransactionID=? AND AccountID=?",
                            arrayOf(
                                transactionSource.transactionID.toString(),
                                paymentSource.account.accountID.toString()
                            )
                        )

                        val selectionArgsDest = arrayOf(transactionDestination.transactionID.toString())
                        val idDest = dbManager.updateTransaction(
                            transactionDestination, "ID=?",
                            selectionArgsDest
                        )
                        if (idDest > 0) {
                            // Update the destination payment
                            println("UPDATING dest payment tid ${transactionDestination.transactionID} aid ${paymentDestination.account.accountID}")
                            dbManager.updatePayment(
                                paymentDestination,
                                "TransactionID=? AND AccountID=?",
                                arrayOf(
                                    transactionDestination.transactionID.toString(),
                                    paymentDestination.account.accountID.toString()
                                )
                            )

                            saveSuccess = true
                            dbManager.sqlDB!!.close()

                            // Transactions updated in the database, return to previous fragment
                            Toast.makeText(
                                this, R.string.transaction_update_success,
                                Toast.LENGTH_LONG
                            ).show()
                            setResult(RESULT_OK)
                            this.finish()
                        }

                        if (!saveSuccess) {
                            // Failed to update, show this error
                            Toast.makeText(
                                this, R.string.transaction_update_fail,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
        dbManager.sqlDB!!.close()
    }

    /**
     * Update [etDate] value with the class date variable.
     */
    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        etDate!!.setText(sdf.format(transferDate.time))
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