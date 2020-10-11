package dev.chester_lloyd.moneymanager.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.chester_lloyd.moneymanager.*
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.TransactionDetails
import dev.chester_lloyd.moneymanager.ui.accounts.AccountTransactions
import dev.chester_lloyd.moneymanager.ui.accounts.AccountsFragment
import dev.chester_lloyd.moneymanager.ui.accounts.TransferFunds
import dev.chester_lloyd.moneymanager.ui.transactions.AddTransactionCheckRequirements
import dev.chester_lloyd.moneymanager.ui.transactions.TransactionsFragment
import kotlinx.android.synthetic.main.account.view.*
import kotlinx.android.synthetic.main.account.view.ivIcon
import kotlinx.android.synthetic.main.account.view.tvName
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.tvNoAccounts
import kotlinx.android.synthetic.main.transaction.view.*

/**
 * A [Fragment] subclass to show the dashboard.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    /**
     * An [onCreateView] method that sets up the FAB and view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        // Launch new transaction activity with fab
        val fab: FloatingActionButton = root.findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(context, AddTransactionCheckRequirements::class.java)
            startActivity(intent)
        }
        return root
    }

    /**
     * An [onResume] method that adds up to 3 accounts and up to 3 recent transactions to the
     * ListViews and updates the view
     */
    override fun onResume() {
        super.onResume()

        val format = MainActivity.getCurrencyFormat(context!!)
        if (format[0] == "" && format[3] == "") {
            // The app has not been set up, load setup page
            startActivity(Intent(context, SetupApp::class.java))
        } else {
            // Update page title and set active drawer item
            activity!!.toolbar.title = getString(R.string.menu_dashboard)
            activity!!.nav_view.setCheckedItem(R.id.nav_home)

            // Get accounts as an array list from database and add them to the list
            val dbManager = DBManager(context!!)
            val listAccounts = dbManager.selectAccounts("active", "3")
            addAccounts(listAccounts)

            // Show no accounts text
            if (listAccounts.isEmpty()) {
                this.tvNoAccounts.visibility = View.VISIBLE
            } else {
                this.tvNoAccounts.visibility = View.GONE
            }

            // Get transactions as an array list from database and add them to the recent list
            val listTransactions = dbManager.selectTransactions(0, "Categories", "3")
            addTransactions(listTransactions)
            dbManager.sqlDB!!.close()

            // Show no transactions text
            if (listTransactions.isEmpty()) {
                this.tvNoTransactions.visibility = View.VISIBLE
            } else {
                this.tvNoTransactions.visibility = View.GONE
            }
        }
    }

    /**
     * Add accounts to the dashboard
     *
     * @param accounts An [ArrayList] of [Account] objects.
     * @suppress InflateParams as the layout is inflating without a Parent
     */
    @SuppressLint("InflateParams")
    private fun addAccounts(accounts: ArrayList<Account>) {
        llAccounts.removeAllViews()
        for (item in 0 until accounts.size) {
            // Adds each account to a new row in a linear layout
            val rowView = layoutInflater.inflate(R.layout.account, null)
            val account = accounts[item]
            rowView.tvName.text = account.name
            rowView.tvBalance.text = MainActivity.stringBalance(context!!, account.balance)

            // Get the account's icon and colour
            val iconManager = IconManager(context!!)
            rowView.ivIcon.setImageResource(
                iconManager.getIconByID(iconManager.accountIcons, account.icon).drawable
            )
            rowView.ivIcon.setBackgroundResource(
                iconManager.getIconByID(iconManager.colourIcons, account.colour).drawable
            )

            // Add the account to the layout
            llAccounts.addView(rowView)

            // When an account is clicked
            rowView.setOnClickListener {
                val clickedAccount = accounts[item]

                // Setup an intent to send this across to view the account's transactions
                val intent = Intent(context, AccountTransactions::class.java)
                val bundle = Bundle()
                bundle.putInt("accountID", account.accountID)
                bundle.putString("name", account.name)
                bundle.putDouble("balance", account.balance)
                bundle.putInt("icon", account.icon)
                bundle.putInt("colour", account.colour)
                bundle.putBoolean("default", account.default)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

        // If there are more than 3 accounts, show a view more button
        val dbManager = DBManager(context!!)
        if (dbManager.selectAccounts("active", null).size > 3) {
            val buAccounts = Button(context)
            buAccounts.text = getString(R.string.view_more)
            buAccounts.setTextColor(ContextCompat.getColor(context!!, R.color.buttonLink))
            buAccounts.setBackgroundResource(0)
            buAccounts.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // When the view more accounts button is clicked, switch to this fragment
            buAccounts.setOnClickListener {
                val fragmentManager = parentFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, AccountsFragment())
                    .addToBackStack(null)
                    .commit()
                // Update page title and set active drawer item
                activity!!.toolbar.title = getString(R.string.menu_accounts)
                activity!!.nav_view.setCheckedItem(R.id.nav_accounts)
            }
            // Add button to the page
            llAccounts.addView(buAccounts)
        }
        dbManager.sqlDB!!.close()
    }

    /**
     * Add transactions to the dashboard
     *
     * @param transactions An [ArrayList] of [Transaction] objects.
     * @suppress InflateParams as the layout is inflating without a Parent
     */
    @SuppressLint("InflateParams")
    private fun addTransactions(transactions: ArrayList<Transaction>) {
        llTransactions.removeAllViews()
        for (item in 0 until transactions.size) {
            // Adds each transaction to a new row in a linear layout
            val rowView = layoutInflater.inflate(R.layout.transaction, null)
            val transaction = transactions[item]
            rowView.tvName.text = transaction.merchant
            rowView.tvDate.text = transaction.getDate(context!!, "DMY")
            rowView.tvAmount.text = MainActivity.stringBalance(context!!, transaction.amount)

            // Get the transaction's category icon and colour
            val iconManager = IconManager(context!!)
            rowView.ivIcon.setImageResource(
                iconManager.getIconByID(
                    iconManager.categoryIcons, transaction.category.icon
                ).drawable
            )
            rowView.ivIcon.setBackgroundResource(
                iconManager.getIconByID(
                    iconManager.colourIcons, transaction.category.colour
                ).drawable
            )

            // Add the transaction to the layout
            llTransactions.addView(rowView)

            // When a transactions is clicked
            rowView.setOnClickListener {
                val clickedTransaction = transactions[item]

                // Setup an intent to send this across to view this transactions details
                var intent = Intent(context, TransactionDetails::class.java)
                val bundle = Bundle()

                if (clickedTransaction.transferTransactionID != 0) {
                    // This is a transfer, show the transfer page
                    intent = Intent(context, TransferFunds::class.java)
                }

                bundle.putInt("transactionID", clickedTransaction.transactionID)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

        // If there are more than 3 transactions, show a view more button
        val dbManager = DBManager(context!!)
        if (dbManager.selectTransactions(0, "Categories", null).size > 3) {
            val buTransactions = Button(context)
            buTransactions.text = getString(R.string.view_more)
            buTransactions.setTextColor(ContextCompat.getColor(context!!, R.color.buttonLink))
            buTransactions.setBackgroundResource(0)
            buTransactions.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // When the view more transactions button is clicked, switch to this fragment
            buTransactions.setOnClickListener {
                val fragmentManager = parentFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, TransactionsFragment())
                    .addToBackStack(null)
                    .commit()
                // Update page title and set active drawer item
                activity!!.toolbar.title = getString(R.string.menu_transactions)
                activity!!.nav_view.setCheckedItem(R.id.nav_transactions)
            }

            // Add button to the page
            llTransactions.addView(buTransactions)
        }
        dbManager.sqlDB!!.close()
    }
}