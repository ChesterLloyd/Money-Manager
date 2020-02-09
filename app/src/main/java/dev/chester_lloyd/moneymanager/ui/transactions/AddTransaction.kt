package dev.chester_lloyd.moneymanager.ui.transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Spinner
import dev.chester_lloyd.moneymanager.Category
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.dbManager
import dev.chester_lloyd.moneymanager.ui.IconSpinner

class AddTransaction : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

//      Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.button_add_transaction)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

//      Set up the account icon spinner
        val categories: ArrayList<Category> = dbManager(this).selectCategory()
        val spinner = findViewById<Spinner>(R.id.spCategory)

        val categoryName: MutableList<String> = ArrayList()
        val icon = IntArray(categories.size)
        val colour = IntArray(categories.size)
        for (category in 0..categories.size - 1) {
            categoryName.add(category, categories[category].name)
            icon[category] = categories[category].icon
            colour[category] = categories[category].colour
        }
        categoryName.toTypedArray()

        spinner.adapter = IconSpinner(
            applicationContext,
            icon,
            colour,
            categoryName.toTypedArray(),
            "icon"
        )
    }

//  Close activity once toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
