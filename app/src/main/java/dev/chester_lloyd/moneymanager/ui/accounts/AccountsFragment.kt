package dev.chester_lloyd.moneymanager.ui.accounts

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.DBManager
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
        val listAccounts = DBManager(context!!).selectAccount("active", null)

//      Pass this to the list view adaptor and populate
        val accountsAdapter = AccountsAdapter(listAccounts)
        this.lvAccounts.adapter = accountsAdapter

//      When an account in the list is clicked
        this.lvAccounts.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountsViewModel = ViewModelProvider(this)[AccountsViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_accounts, container, false)

//      Launch new account activity with fab
        val fab: FloatingActionButton = root.findViewById(R.id.fab1)
        fab.setOnClickListener {
            val intent = Intent(context, AddAccount::class.java)
            startActivity(intent)
        }

        return root
    }


    inner class AccountsAdapter(private var listAccountsAdapter: ArrayList<Account>) :
        BaseAdapter() {

        @SuppressLint("InflateParams", "ViewHolder")
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