package dev.chester_lloyd.moneymanager.ui.categories

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.DBManager
import dev.chester_lloyd.moneymanager.databinding.FragmentCategoriesBinding
import dev.chester_lloyd.moneymanager.ui.ListViewManager

/**
 * A [Fragment] subclass to show a ListView of categories.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private var listViewPosition = 0

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
        super.onCreate(savedInstanceState)
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val view = binding.root

        // Launch new category activity with FAB
        val fab: FloatingActionButton = view.findViewById(R.id.fab1)
        fab.setOnClickListener {
            val intent = Intent(context, AddCategory::class.java)
            startActivity(intent)
        }

        return view
    }

    /**
     * An [onResume] method that adds all of the categories to a ListView
     */
    override fun onResume() {
        super.onResume()
        val dbManager = DBManager(requireContext())

        // Get categories as an array list from database
        val listCategories = dbManager.selectCategories()
        dbManager.sqlDB!!.close()

        // Pass this to the list view adaptor and populate
        binding.lvCategories.adapter = ListViewManager(
            listCategories.toTypedArray(),
            layoutInflater,
            requireContext(),
            "categories"
        )
        binding.lvCategories.setSelection(listViewPosition)

        // Show no categories text
        if (listCategories.isEmpty()) {
            binding.tvNoCategories.visibility = View.VISIBLE
        } else {
            binding.tvNoCategories.visibility = View.INVISIBLE
        }
    }

    /**
     * An [onPause] method that stores the position of the ListView.
     */
    override fun onPause() {
        super.onPause()
        listViewPosition = binding.lvCategories.firstVisiblePosition
    }

    /**
     * An [onDestroyView] method that cleans up references to the binding.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}