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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.chester_lloyd.moneymanager.*
import dev.chester_lloyd.moneymanager.databinding.AccountBinding
import dev.chester_lloyd.moneymanager.databinding.FragmentDashboardBinding
import dev.chester_lloyd.moneymanager.databinding.TransactionBinding
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.TransactionDetails
import dev.chester_lloyd.moneymanager.ui.accounts.AccountTransactions
import dev.chester_lloyd.moneymanager.ui.accounts.AccountsFragment
import dev.chester_lloyd.moneymanager.ui.accounts.TransferFunds
import dev.chester_lloyd.moneymanager.ui.transactions.AddTransactionCheckRequirements
import dev.chester_lloyd.moneymanager.ui.transactions.TransactionsFragment

/**
 * A [Fragment] subclass to show the dashboard.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    /**
     * An [onCreateView] method that sets up the FAB and view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = binding.root

        // Launch new transaction activity with fab
        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener {
            val intent = Intent(context, AddTransactionCheckRequirements::class.java)
            startActivity(intent)
        }

        return view
    }

    /**
     * An [onResume] method that adds up to 3 accounts and up to 3 recent transactions to the
     * ListViews and updates the view
     */
    override fun onResume() {
        super.onResume()

        val format = MainActivity.getCurrencyFormat(requireContext())
        if (format[0] == "" && format[3] == "") {
            // The app has not been set up, load setup page
            startActivity(Intent(context, SetupApp::class.java))
        } else {
            // Update page title and set active drawer item
            requireActivity().actionBar?.title = getString(R.string.menu_dashboard)
//            requireActivity().nav_view.setCheckedItem(R.id.nav_home)

            // Get accounts as an array list from database and add them to the list
            val dbManager = DBManager(requireContext())
            val listAccounts = dbManager.selectAccounts("active", "3")
            addAccounts(listAccounts)

            // Show no accounts text
            if (listAccounts.isEmpty()) {
                binding.tvNoAccounts.visibility = View.VISIBLE
            } else {
                binding.tvNoAccounts.visibility = View.GONE
            }

            // Get transactions as an array list from database and add them to the recent list
            val listTransactions =
                dbManager.selectTransactions(0.toString(), "Categories", "3", false)
            addTransactions(listTransactions)
            dbManager.sqlDB!!.close()

            // Show no transactions text
            if (listTransactions.isEmpty()) {
                binding.tvNoTransactions.visibility = View.VISIBLE
            } else {
                binding.tvNoTransactions.visibility = View.GONE
            }
        }
    }

    /**
     * An [onDestroyView] method that cleans up references to the binding.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Add accounts to the dashboard
     *
     * @param accounts An [ArrayList] of [Account] objects.
     * @suppress InflateParams as the layout is inflating without a Parent
     */
    @SuppressLint("InflateParams")
    private fun addAccounts(accounts: ArrayList<Account>) {
        binding.llAccounts.removeAllViews()
        for (item in 0 until accounts.size) {
            // Adds each account to a new row in a linear layout
            val rowView = AccountBinding.inflate(layoutInflater)
            val account = accounts[item]
            rowView.tvName.text = account.name
            rowView.tvBalance.text = MainActivity.stringBalance(requireContext(), account.balance)

            // Get the account's icon and colour
            val iconManager = IconManager(requireContext())
            rowView.ivIcon.setImageResource(
                iconManager.getIconByID(iconManager.accountIcons, account.icon).drawable
            )
            rowView.ivIcon.setBackgroundResource(
                iconManager.getIconByID(iconManager.colourIcons, account.colour).drawable
            )

            // Add the account to the layout
            binding.llAccounts.addView(rowView.root)

            // When an account is clicked
            rowView.root.setOnClickListener {
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
        val dbManager = DBManager(requireContext())
        if (dbManager.selectAccounts("active", null).size > 3) {
            val buAccounts = Button(context)
            buAccounts.text = getString(R.string.view_more)
            buAccounts.setTextColor(ContextCompat.getColor(requireContext(), R.color.buttonLink))
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
                requireActivity().actionBar?.title = getString(R.string.menu_accounts)
//                requireActivity().nav_view.setCheckedItem(R.id.nav_accounts)
            }
            // Add button to the page
            binding.llAccounts.addView(buAccounts)
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
        binding.llTransactions.removeAllViews()
        for (item in 0 until transactions.size) {
            // Adds each transaction to a new row in a linear layout
            val rowView = TransactionBinding.inflate(layoutInflater)
            val transaction = transactions[item]
            rowView.tvName.text = transaction.merchant
            rowView.tvDate.text = MainActivity.getFormattedDate(requireContext(), transaction.date)
            rowView.tvAmount.text = MainActivity.stringBalance(requireContext(), transaction.amount)

            // Show positive amounts for transfers as we're only show the one transaction
            if (transaction.transferTransactionID > 0) {
                rowView.tvAmount.text =
                    MainActivity.stringBalance(requireContext(), (transaction.amount * -1))
            }

            // Get the transaction's category icon and colour
            val iconManager = IconManager(requireContext())
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
            binding.llTransactions.addView(rowView.root)

            // When a transactions is clicked
            rowView.root.setOnClickListener {
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
        val dbManager = DBManager(requireContext())
        if (dbManager.selectTransactions(0.toString(), "Categories", null, false).size > 3) {
            val buTransactions = Button(context)
            buTransactions.text = getString(R.string.view_more)
            buTransactions.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.buttonLink
                )
            )
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
                requireActivity().actionBar?.title = getString(R.string.menu_transactions)
//                requireActivity().nav_view.setCheckedItem(R.id.nav_transactions)
            }

            // Add button to the page
            binding.llTransactions.addView(buTransactions)
        }
        dbManager.sqlDB!!.close()
    }
}