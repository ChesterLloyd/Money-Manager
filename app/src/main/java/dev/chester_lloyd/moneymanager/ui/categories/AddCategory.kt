package dev.chester_lloyd.moneymanager.ui.categories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.dbManager
import dev.chester_lloyd.moneymanager.Account
import dev.chester_lloyd.moneymanager.Category
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

//      Set up the account icon spinner
        val spinner = findViewById<Spinner>(R.id.spIcon)
        val imageName = resources.getStringArray(R.array.category_names)
        val image = intArrayOf(
            R.drawable.ic_category_bar,
            R.drawable.ic_category_bills_lightbulb,
            R.drawable.ic_category_bills_lightbulb_2,
            R.drawable.ic_category_bills_power,
            R.drawable.ic_category_business,
            R.drawable.ic_category_cake,
            R.drawable.ic_category_camera,
            R.drawable.ic_category_computer_cloud,
            R.drawable.ic_category_computer_laptop,
            R.drawable.ic_category_computer_phone,
            R.drawable.ic_category_computer_servers,
            R.drawable.ic_category_computer_storage,
            R.drawable.ic_category_fridge,
            R.drawable.ic_category_people_child,
            R.drawable.ic_category_people_people,
            R.drawable.ic_category_people_person,
            R.drawable.ic_category_places_cafe,
            R.drawable.ic_category_places_cinema,
            R.drawable.ic_category_places_dining,
            R.drawable.ic_category_places_event,
            R.drawable.ic_category_places_florist,
            R.drawable.ic_category_places_hotel,
            R.drawable.ic_category_places_mail,
            R.drawable.ic_category_places_pharmacy,
            R.drawable.ic_category_places_pizza,
            R.drawable.ic_category_places_receipt,
            R.drawable.ic_category_places_restaurant,
            R.drawable.ic_category_places_school,
            R.drawable.ic_category_shopping_basket,
            R.drawable.ic_category_shopping_cart,
            R.drawable.ic_category_shopping_estore,
            R.drawable.ic_category_shopping_store,
            R.drawable.ic_category_sports_golf,
            R.drawable.ic_category_sports_swim,
            R.drawable.ic_category_stationary,
            R.drawable.ic_category_stationary_printer,
            R.drawable.ic_category_subscription_dvr,
            R.drawable.ic_category_subscription_movie,
            R.drawable.ic_category_subscription_music,
            R.drawable.ic_category_subscription_ondemand,
            R.drawable.ic_category_subscription_radio,
            R.drawable.ic_category_subscription_subscriptions,
            R.drawable.ic_category_subscriptions_book,
            R.drawable.ic_category_ticket,
            R.drawable.ic_category_transport_bus,
            R.drawable.ic_category_transport_car,
            R.drawable.ic_category_transport_flight,
            R.drawable.ic_category_transport_motorbike,
            R.drawable.ic_category_transport_station_ev,
            R.drawable.ic_category_transport_station_gas,
            R.drawable.ic_category_transport_subway,
            R.drawable.ic_category_transport_taxi,
            R.drawable.ic_category_transport_train,
            R.drawable.ic_category_work
        )
        spinner.adapter = IconSpinner(
            applicationContext,
            image,
            imageName,
            "icon"
        )

//      Set up the account color spinner
        val colourSpinner = findViewById<Spinner>(R.id.spColour)
        val colourName = resources.getStringArray(R.array.colour_names)
        val colour = intArrayOf(
            R.drawable.ic_circle_green,
            R.drawable.ic_circle_dark_blue,
            R.drawable.ic_circle_paypal
        )
        colourSpinner.adapter = IconSpinner(
            applicationContext,
            colour,
            colourName,
            "colour"
        )

        val category = Category()

        category.categoryID = intent.getIntExtra("categoryID", 0)

//      If the category ID > 0 (not a new one) then auto fill these fields with the saved values
        if (category.categoryID > 0) {
            etName.setText(intent.getStringExtra("name"))
            spIcon.setSelection(image.indexOf(intent.getIntExtra("icon", 0)))
            spColour.setSelection(colour.indexOf(intent.getIntExtra("colour", 0)))
        }

        fabAddAccount.setOnClickListener {
            category.name = etName.text.toString()

            if (category.name == "") {
//              Category name is empty, show an error
                Toast.makeText(this, "Category name cannot be blank", Toast.LENGTH_SHORT)
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
                category.icon = image[position]
            }
        }

//      Add selected colour to category object
        spColour?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                category.colour = colour[position]
            }
        }
    }

    //  Close activity once toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
