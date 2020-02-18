package dev.chester_lloyd.moneymanager.ui.accounts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.dbManager
import dev.chester_lloyd.moneymanager.Account
import dev.chester_lloyd.moneymanager.ui.IconManager
import kotlinx.android.synthetic.main.account.view.*
import kotlinx.android.synthetic.main.fragment_accounts.*

class AccountsFragment : Fragment() {

    private lateinit var accountsViewModel: AccountsViewModel

//  When the fragment resumes (on first load or after adding an account) do
    override fun onResume() {
        super.onResume()

//      Get accounts as an array list from database
        var listAccounts = loadAccounts("%")

//      Pass this to the list view adaptor and populate
        val myAccountsAdapter = MyAccountsAdapter(listAccounts)
        this.lvAccounts.adapter = myAccountsAdapter

//      When an account in the list is clicked
        this.lvAccounts.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

//              Get account object of item that is clicked
                val account = lvAccounts.getItemAtPosition(position) as Account

//              Setup an intent to send this across to view the account's transactions
                val intent = Intent(context, AccountTransactions::class.java)

                val bundle = Bundle()
                bundle.putInt("accountID", account.accountID)
                bundle.putString("name", account.name)
                bundle.putDouble("balance", account.balance)
                bundle.putInt("icon", account.icon)
                bundle.putInt("colour", account.colour)
                intent.putExtras(bundle)

                startActivity(intent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountsViewModel =
            ViewModelProviders.of(this).get(AccountsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_accounts, container, false)

//      Launch new account activity with fab
        val fab: FloatingActionButton = root.findViewById(R.id.fab1)
        fab.setOnClickListener {
            val intent = Intent(context, AddAccount::class.java)
            startActivity(intent)
        }

        return root
    }

//  Read accounts from the database and return an array of Account objects
    fun loadAccounts(name:String):ArrayList<Account> {
        var listAccounts = ArrayList<Account>()

        var dbManager = dbManager(context!!)

        val projection = arrayOf("ID", "Name", "Balance", "Icon", "Colour")
        val selectionArgs = arrayOf(name)

        // Each ? represents an arg in array
        val cursor = dbManager.query("Accounts", projection, "Name like ?", selectionArgs, "Name")

        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val name = cursor.getString(cursor.getColumnIndex("Name"))
                val balance = cursor.getDouble(cursor.getColumnIndex("Balance"))
                val icon = cursor.getInt(cursor.getColumnIndex("Icon"))
                val colour = cursor.getInt(cursor.getColumnIndex("Colour"))

                listAccounts.add(Account(ID, name, balance, icon, colour))
            } while (cursor.moveToNext())
        }

        return listAccounts
    }


    inner class MyAccountsAdapter:BaseAdapter {
        var listAccountsAdapter = ArrayList<Account>()
        constructor(listAccountsAdapter:ArrayList<Account>):super() {
            this.listAccountsAdapter = listAccountsAdapter

        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//          Adds each account to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.account, null)
            val account = listAccountsAdapter[position]
            rowView.tvName.text = account.name
            rowView.tvBalance.text = account.getStringBalance(context!!)

            val iconManager = IconManager(context!!)
            rowView.ivIcon.setImageResource(
                iconManager.getIconByID(iconManager.accountIcons, account.icon).drawable)
            rowView.ivIcon.setBackgroundResource(
                iconManager.getIconByID(iconManager.colourIcons, account.colour).drawable)

            return rowView
        }

        override fun getItem(position: Int): Any {
            return listAccountsAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listAccountsAdapter.size
        }
    }
}