package dev.chester_lloyd.moneymanager.ui.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.chester_lloyd.moneymanager.DBManager
import dev.chester_lloyd.moneymanager.databinding.FragmentTransactionTabBinding
import dev.chester_lloyd.moneymanager.ui.ListViewManager

/**
 * A [Fragment] subclass to show a tabbed layout containing pie charts.
 *
 * @param tab The ID of the current tab that's selected
 * @author Chester Lloyd
 * @since 1.0
 */
class TransactionTabFragment(private val tab: Int = 0) : Fragment() {

    private var _binding: FragmentTransactionTabBinding? = null
    private val binding get() = _binding!!

    /**
     * An [onCreateView] method that sets up the View
     *
     * @param inflater The LayoutInflater object
     * @param container The parent view.
     * @param savedInstanceState Fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentTransactionTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * An [onResume] method that creates a ListView of transactions based on a category.
     */
    override fun onResume() {
        super.onResume()

        // Get transactions as an array list from database
        val dbManager = DBManager(requireContext())
        val listTransactions = dbManager.selectTransactions(tab.toString(), "Categories", null, false)
        dbManager.sqlDB!!.close()

        // If there are no transactions under this category, show a message
        if (listTransactions.isEmpty()) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            // There are transactions to show, hide message and show transactions
            binding.tvMessage.visibility = View.GONE

            // Pass this to the list view adaptor and populate
            binding.lvTransactions.adapter = ListViewManager(
                listTransactions.toTypedArray(),
                layoutInflater,
                requireContext(),
                "transactions"
            )
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
