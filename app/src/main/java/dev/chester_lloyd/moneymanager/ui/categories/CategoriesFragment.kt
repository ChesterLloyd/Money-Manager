package dev.chester_lloyd.moneymanager.ui.categories

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
import dev.chester_lloyd.moneymanager.Category
import dev.chester_lloyd.moneymanager.ui.IconManager
import kotlinx.android.synthetic.main.category.view.*
import kotlinx.android.synthetic.main.fragment_categories.*

/**
 * A [Fragment] subclass to show a ListView of categories.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class CategoriesFragment : Fragment() {

    private lateinit var categoriesViewModel: CategoriesViewModel

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
        categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_categories, container, false)

        // Launch new category activity with FAB
        val fab: FloatingActionButton = root.findViewById(R.id.fab1)
        fab.setOnClickListener {
            val intent = Intent(context, AddCategory::class.java)
            startActivity(intent)
        }

        return root
    }

    /**
     * An [onResume] method that adds all of the categories to a ListView
     */
    override fun onResume() {
        super.onResume()
        val dbManager = DBManager(context!!)

        // Get categories as an array list from database
        val listCategories = dbManager.selectCategories()
        dbManager.sqlDB!!.close()

        // Pass this to the list view adaptor and populate
        val categoriesAdapter = CategoriesAdapter(listCategories)
        this.lvCategories.adapter = categoriesAdapter

        // When a category in the list is clicked
        this.lvCategories.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // value of item that is clicked
                val category = lvCategories.getItemAtPosition(position) as Category
                val intent = Intent(context, CategoryTransaction::class.java)

                val bundle = Bundle()
                bundle.putInt("categoryID", category.categoryID)
                bundle.putString("name", category.name)
                bundle.putInt("icon", category.icon)
                bundle.putInt("colour", category.colour)
                intent.putExtras(bundle)

                startActivity(intent)
            }
    }

    /**
     * An inner class that takes an array of [Category] objects and handles all operations of the
     * ListView.
     *
     * @param listCategoriesAdapter An [ArrayList] of [Category] objects
     * @return [BaseAdapter]
     */
    inner class CategoriesAdapter(private var listCategoriesAdapter: ArrayList<Category>) :
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
            // Adds each category to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.category, null)
            val category = listCategoriesAdapter[position]
            rowView.tvName.text = category.name

            // Use IconManager to load the icons
            val iconManager = IconManager(context!!)
            rowView.ivIcon.setImageResource(
                iconManager.getIconByID(iconManager.categoryIcons, category.icon).drawable
            )
            rowView.ivIcon.setBackgroundResource(
                iconManager.getIconByID(iconManager.colourIcons, category.colour).drawable
            )

            return rowView
        }

        /**
         * Get the [Category] object at a given [position] in the ListView.
         *
         * @param position Position of row in the ListView.
         * @return An [Category] object of the item at the given position.
         */
        override fun getItem(position: Int): Any {
            return listCategoriesAdapter[position]
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
            return listCategoriesAdapter.size
        }
    }
}