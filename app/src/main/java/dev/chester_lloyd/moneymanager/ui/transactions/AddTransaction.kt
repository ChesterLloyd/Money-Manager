package dev.chester_lloyd.moneymanager.ui.transactions

//import android.R


import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import dev.chester_lloyd.moneymanager.Account
import dev.chester_lloyd.moneymanager.Category
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.dbManager
import dev.chester_lloyd.moneymanager.ui.CurrencyValidatior
import dev.chester_lloyd.moneymanager.ui.IconSpinner
import kotlinx.android.synthetic.main.activity_add_transaction.*


class AddTransaction : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

//      Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.button_add_transaction)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

//      Setup the account entry texts
        val accounts: ArrayList<Account> = dbManager(this).selectAccount()

        for (account in 0..accounts.size - 1) {
            val etAccount = EditText(this)
            etAccount.setHint(accounts[account].name)
            etAccount.inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL +
                    InputType.TYPE_NUMBER_FLAG_SIGNED

//          Validate the currency field
            val balanceValidator = CurrencyValidatior(etAccount)

            val etAccountInput = etAccount as EditText
            etAccountInput.onFocusChangeListener = View.OnFocusChangeListener { v, gainFocus ->
                balanceValidator.focusListener(gainFocus)
            }

            etAccount.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//                  Update balance before changes have been made (i.e user changed it)
                    balanceValidator.beforeTextChangedListener(s)
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    balanceValidator.onTextChangedListener(s)
                }
            })

            llAccounts.addView(etAccount)
        }
    }

//  Close activity once toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
