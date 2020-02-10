package dev.chester_lloyd.moneymanager.ui.transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import dev.chester_lloyd.moneymanager.*
import dev.chester_lloyd.moneymanager.ui.CurrencyValidator
import dev.chester_lloyd.moneymanager.ui.IconSpinner
import kotlinx.android.synthetic.main.activity_add_transaction.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddTransaction : AppCompatActivity() {

    var transaction = Transaction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

//      Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.button_add_transaction)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

//      Validate the amount field
        val amountValidator = CurrencyValidator(etAmount)

        val etAccountInput = etAmount as EditText
        etAccountInput.onFocusChangeListener = View.OnFocusChangeListener { v, gainFocus ->
            amountValidator.focusListener(gainFocus)
        }

        etAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//              Update balance before changes have been made (i.e user changed it)
                amountValidator.beforeTextChangedListener(s)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                amountValidator.onTextChangedListener(s)
            }
        })

//      Setup the date to the current device date
        val etDate = this.etDate
        updateDateInView()

//      Create a date picker, set values for class date value
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                transaction.date.set(Calendar.YEAR, year)
                transaction.date.set(Calendar.MONTH, monthOfYear)
                transaction.date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

//      When the date edit text has focus (clicked), open the date picker
        etDate.onFocusChangeListener = View.OnFocusChangeListener { v, gainFocus ->
                if (gainFocus) {
                    DatePickerDialog(this@AddTransaction,
                        dateSetListener,
                        // set to point to today's date when it loads up
                        transaction.date.get(Calendar.YEAR),
                        transaction.date.get(Calendar.MONTH),
                        transaction.date.get(Calendar.DAY_OF_MONTH)).show()
                    etDate.clearFocus()
            }
        }

//      Setup the category icon spinner
        val categories: ArrayList<Category> = dbManager(this).selectCategory()
        val spinner = findViewById<Spinner>(R.id.spCategory)

        val categoryName: MutableList<String> = ArrayList()
        val icon = IntArray(categories.size)
        val colour = IntArray(categories.size)
        for (category in 0..categories.size - 1) {
            categoryName.add(category, categories[category].name)
            icon[category] = categories[category].icon
            colour[category] = categories[category].colour
        }
        categoryName.toTypedArray()

        spinner.adapter = IconSpinner(
            applicationContext,
            icon,
            colour,
            categoryName.toTypedArray(),
            "icon"
        )

//      Add selected category to transaction object
        spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                transaction.category = categories[position]
            }
        }

//      Setup the account entry texts
        val accounts: ArrayList<Account> = dbManager(this).selectAccount()

        for (account in 0..accounts.size - 1) {
            val etAccount = EditText(this)
            etAccount.id = accounts[account].accountID
            etAccount.setHint(accounts[account].name)
            etAccount.inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL +
                    InputType.TYPE_NUMBER_FLAG_SIGNED

//          Validate the currency field
            val balanceValidator = CurrencyValidator(etAccount)

            val etAccountInput = etAccount as EditText
            etAccountInput.onFocusChangeListener = View.OnFocusChangeListener { v, gainFocus ->
                balanceValidator.focusListener(gainFocus)
            }

            etAccount.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int){
//                  Update balance before changes have been made (i.e user changed it)
                    balanceValidator.beforeTextChangedListener(s)
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    balanceValidator.onTextChangedListener(s)
                }
            })

            llAccounts.addView(etAccount)
        }

        fabAddTransaction.setOnClickListener {
            println("\n\n\n" + transaction.date + "\n\n\n")
            transaction.name = etName.text.toString()
            if (transaction.name == "") {
//              Transaction name is empty, show an error
                Toast.makeText(this, R.string.transaction_validation_name,
                    Toast.LENGTH_SHORT).show()
            } else if (etAmount.text.toString() == "") {
//              Transaction amount is empty, show an error
                Toast.makeText(this, R.string.transaction_validation_amount,
                    Toast.LENGTH_SHORT).show()
            } else if (etDate.text.toString() == "") {
//              Transaction date is empty, show an error
                Toast.makeText(this, R.string.transaction_validation_date,
                    Toast.LENGTH_SHORT).show()
            } else {
//              All data has been filled out, start saving
                transaction.amount = amountValidator.getBalance()

//              Get instance of the database manager class
                val dbManager = dbManager(this)

//              Get all payments as Payment objects
                var payments = ArrayList<Payment>()
                var totalPayments: Double = 0.0
                for (account in 0..accounts.size - 1) {
                    val accountValue = CurrencyValidator(
                        this.findViewById<EditText>(accounts[account].accountID))
                        .getBalance()
                    println(accountValue)

//                  Round to 2dp (since double would probably do: 20.0000004 or something)
                    val accountValue2DP: Double = String.format("%.2f", accountValue).toDouble()
                    totalPayments += accountValue2DP
                    payments.add(
                        Payment(
                            0, Transaction(), accounts[account],
                            accountValue
                        )
                    )
                }

                if (String.format("%.2f", totalPayments).toDouble() != transaction.amount) {
//                  Account payments do not make up the transaction amount, show an error
                    Toast.makeText(
                        this, R.string.transaction_validation_payments,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (transaction.transactionID == 0) {
//                      Insert this new transaction into the transactions table
                        val id = dbManager.insertTransaction(transaction)
                        if (id > 0) {
                            transaction.transactionID = id.toInt()
                            for (payment in 0..payments.size - 1) {
//                              For each payment method (Account)
                                if (payments[payment].amount > 0.0) {
//                                  Add a payment for this amount
                                    payments[payment].transaction = transaction
                                    val id = dbManager.insertPayment(payments[payment])
                                }
                            }
//                          Transaction saved to database, return to previous fragment
                            Toast.makeText(
                                this, R.string.transaction_insert_success,
                                Toast.LENGTH_LONG
                            ).show()
                            this.finish()
                        } else {
//                          Failed to save, show this error
                            Toast.makeText(
                                this, R.string.transaction_insert_fail,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
//                      Update this transaction in the database
                        var selectionArgs = arrayOf(transaction.transactionID.toString())
                        val id = dbManager.updateTransaction(transaction, "ID=?", selectionArgs)
                        if (id > 0) {
//                          Transaction updated in the database, return to previous fragment
                            Toast.makeText(
                                this, R.string.transaction_update_success,
                                Toast.LENGTH_LONG
                            ).show()
                            this.finish()
                        } else {
//                          Failed to save, show this error
                            Toast.makeText(
                                this, R.string.transaction_update_fail,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

//  Update date value with the class date variable
    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        etDate!!.setText(sdf.format(transaction.date.getTime()))
    }

//  Close activity once toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
