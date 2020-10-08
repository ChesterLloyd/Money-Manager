package dev.chester_lloyd.moneymanager.ui.accounts

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.DBManager
import dev.chester_lloyd.moneymanager.Account
import dev.chester_lloyd.moneymanager.MainActivity
import dev.chester_lloyd.moneymanager.ui.IconManager
import kotlinx.android.synthetic.main.account.view.*
import kotlinx.android.synthetic.main.fragment_accounts.*

/**
 * A [Fragment] subclass to show a ListView of accounts.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class AccountsFragment : Fragment() {

    private lateinit var accountsViewModel: AccountsViewModel

    /**
     * An [onCreateView] method that sets up the View and FAB
     *
     * @param inflater The LayoutInflater object
     * @param container The parent view.
     * @param savedInstanceState Fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true);

        accountsViewModel = ViewModelProvider(this)[AccountsViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_accounts, container, false)

        // Launch new account activity with FAB
        val fab: FloatingActionButton = root.findViewById(R.id.fab1)
        fab.setOnClickListener {
            val intent = Intent(context, AddAccount::class.java)
            startActivity(intent)
        }

        return root
    }

    /**
     * An [onResume] method that adds all active accounts to a ListView
     */
    override fun onResume() {
        super.onResume()
        val dbManager = DBManager(context!!)

        // Get accounts as an array list from database
        val listAccounts = dbManager.selectAccounts("active", null)
        dbManager.sqlDB!!.close()

        // Pass this to the list view adaptor and populate
        val accountsAdapter = AccountsAdapter(listAccounts)
        this.lvAccounts.adapter = accountsAdapter

        // When an account in the list is clicked
        this.lvAccounts.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // Get account object of item that is clicked
                val account = lvAccounts.getItemAtPosition(position) as Account

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

        // Show no accounts text
        if (listAccounts.isEmpty()) {
            this.tvNoAccounts.visibility = View.VISIBLE
        } else {
            this.tvNoAccounts.visibility = View.INVISIBLE
        }
    }

    /**
     * An [onCreateOptionsMenu] method that adds the accounts menu to the toolbar. This includes a
     * transfer funds button.
     *
     * @param menu The options menu to place items.
     * @param inflater The [MenuInflater].
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.accounts, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * An [onOptionsItemSelected] method that adds functionality when the menu buttons are clicked.
     *
     * @param item The menu item that was selected.
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menuTransfer -> {
            // Transfer icon clicked, go to transfer funds page
            val intent = Intent(context, TransferFunds::class.java)
            startActivity(intent)
            true
        }
        else -> {
            // Unknown action (not transfer funds) invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * An inner class that takes an array of [Account] objects and handles all operations of the
     * ListView.
     *
     * @param listAccountsAdapter An [ArrayList] of [Account] objects
     * @return [BaseAdapter]
     */
    inner class AccountsAdapter(private var listAccountsAdapter: ArrayList<Account>) :
        BaseAdapter() {

        /**
         * Creates a new row within the list view
         *
         * @param position Position of row in the ListView.
         * @param convertView A View object
         * @param parent The parent's ViewGroup
         * @return A View for a row in the ListView.
         * @suppress InflateParams as the layout is inflating without a Parent
         * @suppress ViewHolder as there is unconditional layout inflation from view adapter
         */
        @SuppressLint("InflateParams", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // Adds each account to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.account, null)
            val account = listAccountsAdapter[position]
            rowView.tvName.text = account.name
            rowView.tvBalance.text = MainActivity.stringBalance(context!!, account.balance)

            // Use IconManager to load the icons
            val iconManager = IconManager(context!!)
            rowView.ivIcon.setImageResource(
                iconManager.getIconByID(iconManager.accountIcons, account.icon).drawable
            )
            rowView.ivIcon.setBackgroundResource(
                iconManager.getIconByID(iconManager.colourIcons, account.colour).drawable
            )

            return rowView
        }

        /**
         * Get the [Account] object at a given [position] in the ListView.
         *
         * @param position Position of row in the ListView.
         * @return An [Account] object of the item at the given position.
         */
        override fun getItem(position: Int): Any {
            return listAccountsAdapter[position]
        }

        /**
         * Get the row ID associated with the specified [position] in the list.
         *
         * @param position The position of the item within the list whose row ID we want.
         * @return The ID of the item at the specified position.
         */
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        /**
         * Returns number of items in the ListView.
         *
         * @return The size of the ListView.
         */
        override fun getCount(): Int {
            return listAccountsAdapter.size
        }
    }
}