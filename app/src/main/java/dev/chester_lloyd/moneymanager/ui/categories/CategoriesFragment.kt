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

class CategoriesFragment : Fragment() {

    private lateinit var categoriesViewModel: CategoriesViewModel

//  When the fragment resumes (on first load or after adding a category) do
    override fun onResume() {
        super.onResume()

//      Get categories as an array list from database
        val listCategories = DBManager(context!!).selectCategory()

//      Pass this to the list view adaptor and populate
        val categoriesAdapter = CategoriesAdapter(listCategories)
        this.lvCategories.adapter = categoriesAdapter

//      When a category in the list is clicked
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_categories, container, false)

//      Launch new category activity with fab
        val fab: FloatingActionButton = root.findViewById(R.id.fab1)
        fab.setOnClickListener {
            val intent = Intent(context, AddCategory::class.java)
            startActivity(intent)
        }

        return root
    }


    inner class CategoriesAdapter(private var listCategoriesAdapter: ArrayList<Category>) :
        BaseAdapter() {

        @SuppressLint("InflateParams", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//          Adds each category to a new row in a list view
            val rowView = layoutInflater.inflate(R.layout.category, null)
            val category = listCategoriesAdapter[position]
            rowView.tvName.text = category.name

            val iconManager = IconManager(context!!)
            rowView.ivIcon.setImageResource(
                iconManager.getIconByID(iconManager.categoryIcons, category.icon).drawable)
            rowView.ivIcon.setBackgroundResource(
                iconManager.getIconByID(iconManager.colourIcons, category.colour).drawable)

            return rowView
        }

        override fun getItem(position: Int): Any {
            return listCategoriesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listCategoriesAdapter.size
        }
    }
}