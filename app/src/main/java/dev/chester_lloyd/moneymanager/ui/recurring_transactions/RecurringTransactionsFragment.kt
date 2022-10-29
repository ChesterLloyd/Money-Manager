package dev.chester_lloyd.moneymanager.ui.recurring_transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.chester_lloyd.moneymanager.DBManager
import dev.chester_lloyd.moneymanager.databinding.FragmentRecurringTransactionsBinding
import dev.chester_lloyd.moneymanager.ui.ListViewManager

/**
 * A [Fragment] subclass to show a ListView of recurring transactions.
 *
 * @author Chester Lloyd
 * @since 1.5
 */
class RecurringTransactionsFragment : Fragment() {

    private var _binding: FragmentRecurringTransactionsBinding? = null
    private val binding get() = _binding!!

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
        super.onCreate(savedInstanceState)
        _binding = FragmentRecurringTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * An [onResume] method that adds all of the recurring transactions to a ListView
     */
    override fun onResume() {
        super.onResume()
        val dbManager = DBManager(requireContext())

        // Get recurring transactions as an array list from database
        val listRecurringTransactions = dbManager.selectRecurringTransactions(null, null, null)
        dbManager.sqlDB!!.close()

        // Pass this to the list view adaptor and populate
        binding.lvRecurringTransactions.adapter = ListViewManager(
            listRecurringTransactions.toTypedArray(),
            layoutInflater,
            requireContext(),
            "recurring transactions"
        )

        // Show no recurring transactions text
        if (listRecurringTransactions.isEmpty()) {
            binding.tvNoRecurringTransactions.visibility = View.VISIBLE
        } else {
            binding.tvNoRecurringTransactions.visibility = View.INVISIBLE
        }
    }

    /**
     * An [onDestroyView] method that cleans up references to the binding.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}