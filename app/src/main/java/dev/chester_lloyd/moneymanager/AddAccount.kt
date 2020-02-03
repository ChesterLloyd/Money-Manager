package dev.chester_lloyd.moneymanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView

class AddAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_account)

//      Setup toolbar name and show a back button
        this.supportActionBar?.title = getString(R.string.button_add_account)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

//      Set up the category icon spinner
        val spinner = findViewById<Spinner>(R.id.spIcon)
        val imageName = arrayOf("Credit Card", "Gift Card", "Cash")
        val image = intArrayOf(R.drawable.ic_credit_card_white_24dp, R.drawable.ic_credit_card_white_24dp, R.drawable.ic_local_atm_white_24dp)
        val spinnerCustomAdapter = SpinnerCustomAdapter(applicationContext, image, imageName)
        spinner.adapter = spinnerCustomAdapter
    }

    class SpinnerCustomAdapter(internal var context: Context, internal var flags: IntArray, internal var Network: Array<String>) : BaseAdapter() { internal var inflter: LayoutInflater
        init {
            inflter = LayoutInflater.from(context)
        }
        override fun getCount(): Int {
            return flags.size
        }
        override fun getItem(i: Int): Any? {
            return null
        }
        override fun getItemId(i: Int): Long {
            return 0
        }
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var view = view
            view = inflter.inflate(R.layout.spinner_icon, null)
            val icon = view.findViewById(R.id.ivCategoryIcon) as ImageView
            val names = view.findViewById(R.id.tvCategoryName) as TextView
            icon.setImageResource(flags[i])
            names.text = Network[i]
            return view
        }
    }

//  Close activity once toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
