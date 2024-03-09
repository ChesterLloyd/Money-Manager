package dev.chester_lloyd.moneymanager.ui.transactions

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dev.chester_lloyd.moneymanager.*
import dev.chester_lloyd.moneymanager.databinding.ActivityAddTransactionCheckRequirementsBinding
import dev.chester_lloyd.moneymanager.ui.accounts.AddAccount
import dev.chester_lloyd.moneymanager.ui.categories.AddCategory

/**
 * An [AppCompatActivity] subclass to redirect to add an [Account] and/or [Category] prior
 * to adding a [Transaction].
 *
 * @author Chester Lloyd
 * @since 1.1
 */
class AddTransactionCheckRequirements : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionCheckRequirementsBinding
    private var noAccounts = false
    private var noCategories = false

    /**
     * An [onCreate] method that sets up the supportActionBar, buttons, FAB and view.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionCheckRequirementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.button_add_transaction)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Launch new account activity with button
        binding.buAddAccount.setOnClickListener {
            val intent = Intent(applicationContext, AddAccount::class.java)
            startActivity(intent)
        }

        // Launch new category activity with button
        binding.buAddCategory.setOnClickListener {
            val intent = Intent(applicationContext, AddCategory::class.java)
            startActivity(intent)
        }

        // Launch new transaction activity with button
        binding.buAddTransaction.setOnClickListener {
            val intent = Intent(applicationContext, AddTransaction::class.java)
            this.startActivityForResult(intent, 0)
        }
    }

    /**
     * An [onResume] method that decides which buttons and text to provide
     */
    override fun onResume() {
        super.onResume()

        // Get accounts as an array list from database and and verify if we have at least one
        val dbManager = DBManager(this)
        val listAccounts = dbManager.selectAccounts("active", null)
        val listCategories = dbManager.selectCategories()
        dbManager.sqlDB!!.close()

        when {
            listAccounts.isEmpty() -> {
                this.noAccounts = true
                binding.llNoAccounts.visibility = View.VISIBLE
                binding.llNoCategories.visibility = View.GONE
                binding.llAddTransaction.visibility = View.GONE
            }
            listCategories.isEmpty() -> {
                this.noCategories = true
                binding.llNoAccounts.visibility = View.GONE
                binding.llNoCategories.visibility = View.VISIBLE
                binding.llAddTransaction.visibility = View.GONE
            }
            else -> {
                binding.llNoAccounts.visibility = View.GONE
                binding.llNoCategories.visibility = View.GONE
                binding.llAddTransaction.visibility = View.VISIBLE
            }
        }
        if (!noAccounts && !noCategories) {
            val intent = Intent(applicationContext, AddTransaction::class.java)
            this.startActivityForResult(intent, 0)
        }
    }

    /**
     * An [onActivityResult] method that will close this activity if the user has added a transaction
     * or pressed a back button.
     */
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            this.finish()
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
        this.finish()
        return true
    }
}
