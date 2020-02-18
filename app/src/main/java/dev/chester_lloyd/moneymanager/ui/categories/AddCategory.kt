package dev.chester_lloyd.moneymanager.ui.categories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.dbManager
import dev.chester_lloyd.moneymanager.Category
import dev.chester_lloyd.moneymanager.ui.IconManager
import dev.chester_lloyd.moneymanager.ui.IconSpinner
import kotlinx.android.synthetic.main.activity_add_account.*

class AddCategory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

//      Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.button_add_category)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val iconManager = IconManager(this)

//      Set up the category icon spinner
        val iconSpinner = findViewById<Spinner>(R.id.spIcon)
        iconSpinner.adapter = IconSpinner(
            applicationContext,
            iconManager.categoryIcons, null, "icon"
        )

//      Set up the category color spinner
        val colourSpinner = findViewById<Spinner>(R.id.spColour)
        colourSpinner.adapter = IconSpinner(
            applicationContext,
            null, iconManager.colourIcons, "colour"
        )

        val category = Category()
        category.categoryID = intent.getIntExtra("categoryID", 0)

//      If the category ID > 0 (not a new one) then auto fill these fields with the saved values
        if (category.categoryID > 0) {
            etName.setText(intent.getStringExtra("name"))

            spIcon.setSelection(iconManager.getIconPositionID(
                iconManager.categoryIcons,
                intent.getIntExtra("icon", 0)))

            spColour.setSelection(iconManager.getIconPositionID(
                iconManager.colourIcons,
                intent.getIntExtra("colour", 0)))
        }

        fabAddAccount.setOnClickListener {
            category.name = etName.text.toString()

            if (category.name == "") {
//              Category name is empty, show an error
                Toast.makeText(this, R.string.category_validation_name, Toast.LENGTH_SHORT)
                    .show()
            } else {
//              All data has been filled out, start saving
//              Get instance of the database manager class
                val dbManager = dbManager(this)

                if (category.categoryID == 0) {
//                  Insert this new category into the categories table
                    val id = dbManager.insertCategory(category)
                    if (id > 0) {
//                      Category saved to database, return to previous categories fragment
                        Toast.makeText(this, R.string.category_insert_success, Toast.LENGTH_LONG)
                            .show()
                        this.finish()
                    } else {
//                      Failed to save, show this error
                        Toast.makeText(this, R.string.category_insert_fail, Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
//                  Update this category in the database
                    var selectionArgs = arrayOf(category.categoryID.toString())
                    val id = dbManager.updateCategory(category, "ID=?", selectionArgs)
                    if (id > 0) {
//                      Category updated in the database, return to previous categories fragment
                        Toast.makeText(this, R.string.category_update_success, Toast.LENGTH_LONG).show()
                        this.finish()
                    } else {
//                      Failed to save, show this error
                        Toast.makeText(this, R.string.category_update_fail, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

//      Add selected icon to category object
        spIcon?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                category.icon = iconManager.categoryIcons[position].id
            }
        }

//      Add selected colour to category object
        spColour?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                category.colour = iconManager.colourIcons[position].id
            }
        }
    }

    //  Close activity once toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
