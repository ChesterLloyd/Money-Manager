package dev.chester_lloyd.moneymanager.ui.accounts

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.chester_lloyd.moneymanager.Account
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.DBManager
import dev.chester_lloyd.moneymanager.databinding.FragmentAccountsBinding
import dev.chester_lloyd.moneymanager.ui.ListViewManager
import java.util.ArrayList

/**
 * A [Fragment] subclass to show a ListView of accounts.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class AccountsFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!
    private var listViewPosition = 0
    private var listAccounts = ArrayList<Account>()

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
    ): View {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.accounts, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.menuTransfer -> {
                        // Transfer icon clicked
                        if (listAccounts.size >= 2) {
                            // Go to transfer funds page
                            val intent = Intent(context, TransferFunds::class.java)
                            startActivity(intent)
                        } else {
                            // Not enough accounts to make a transfer
                            Toast.makeText(
                                context,
                                getString(R.string.transfer_min_accounts),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        true
                    }
                    else -> {
                        // Unknown action (not transfer funds) invoke the superclass to handle it.
                        false
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        super.onCreate(savedInstanceState)
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Launch new account activity with FAB
        val fab: FloatingActionButton = view.findViewById(R.id.fab1)
        fab.setOnClickListener {
            val intent = Intent(context, AddAccount::class.java)
            startActivity(intent)
        }

        return view
    }

    /**
     * An [onResume] method that adds all active accounts to a ListView
     */
    override fun onResume() {
        super.onResume()
        val dbManager = DBManager(requireContext())

        // Get accounts as an array list from database
        listAccounts = dbManager.selectAccounts("active", null)
        dbManager.sqlDB!!.close()

        // Pass this to the list view adaptor and populate
        binding.lvAccounts.adapter = ListViewManager(
            listAccounts.toTypedArray(),
            layoutInflater,
            requireContext(),
            "categories"
        )
        binding.lvAccounts.setSelection(listViewPosition)

        // Show no accounts text
        if (listAccounts.isEmpty()) {
            binding.tvNoAccounts.visibility = View.VISIBLE
        } else {
            binding.tvNoAccounts.visibility = View.INVISIBLE
        }
    }

    /**
     * An [onPause] method that stores the position of the ListView.
     */
    override fun onPause() {
        super.onPause()
        listViewPosition = binding.lvAccounts.firstVisiblePosition
    }
}