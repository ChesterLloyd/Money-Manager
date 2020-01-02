package dev.chester_lloyd.moneymanager.ui.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import dev.chester_lloyd.moneymanager.Account
import dev.chester_lloyd.moneymanager.R
import kotlinx.android.synthetic.main.account.view.*
import kotlinx.android.synthetic.main.fragment_accounts.view.*

class AccountsFragment : Fragment() {

    private lateinit var accountsViewModel: AccountsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountsViewModel =
            ViewModelProviders.of(this).get(AccountsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_accounts, container, false)

        var listAccounts = ArrayList<Account>()
        listAccounts.add(Account(1,"Cash",30.23,null,null))
        listAccounts.add(Account(2,"PayPal",56.00, R.drawable.ic_paypal_brands, R.drawable.ic_circle_paypal))
        val myAccountsAdapter = MyAccountsAdapter(listAccounts)
        root.lvAccounts.adapter = myAccountsAdapter

        return root
    }

    inner class MyAccountsAdapter:BaseAdapter {
        var listAccountsAdapter = ArrayList<Account>()
        constructor(listAccountsAdapter:ArrayList<Account>):super() {
            this.listAccountsAdapter = listAccountsAdapter

        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//          Adds each account to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.account,null)
            val account = listAccountsAdapter[position]
            rowView.tvName.text = account.name
            rowView.tvBalance.text = getString(R.string.balance_text, account.balance)
            rowView.ivIcon.setImageResource(account.icon!!)
            rowView.ivIcon.setBackgroundResource(account.circle!!)
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