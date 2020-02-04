package dev.chester_lloyd.moneymanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.graphics.Color
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

//      Set up the account icon spinner
        val spinner = findViewById<Spinner>(R.id.spIcon)
        val imageName = resources.getStringArray(R.array.account_names)
        val image = intArrayOf(R.drawable.ic_account_airplane,
            R.drawable.ic_account_bank, R.drawable.ic_account_business,
            R.drawable.ic_account_cash, R.drawable.ic_account_credit_card,
            R.drawable.ic_account_dollar, R.drawable.ic_account_gift_card,
            R.drawable.ic_account_joint, R.drawable.ic_account_membership_card,
            R.drawable.ic_account_paypal, R.drawable.ic_account_travel_card,
            R.drawable.ic_account_wallet)
        val spinnerCustomAdapter = SpinnerCustomAdapter(applicationContext, image, imageName, "icon")
        spinner.adapter = spinnerCustomAdapter

//      Set up the account color spinner
        val colourSpinner = findViewById<Spinner>(R.id.spColour)
        val colourName = resources.getStringArray(R.array.colour_names)
        val colour = intArrayOf(R.drawable.ic_circle_green,
            R.drawable.ic_circle_dark_blue, R.drawable.ic_circle_paypal)
        val spinnerCustomAdapter2 = SpinnerCustomAdapter(applicationContext, colour, colourName, "colour")
        colourSpinner.adapter = spinnerCustomAdapter2
    }

    class SpinnerCustomAdapter(internal var context: Context, internal var icons: IntArray, internal var name: Array<String>, internal var spinnerType: String) : BaseAdapter() { internal var inflter: LayoutInflater
        init {
            inflter = LayoutInflater.from(context)
        }
        override fun getCount(): Int {
            return icons.size
        }
        override fun getItem(i: Int): Any? {
            return null
        }
        override fun getItemId(i: Int): Long {
            return 0
        }
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var view = view

            if (spinnerType == "icon") {
                view = inflter.inflate(R.layout.spinner_icon, null)
                val icon = view.findViewById(R.id.ivAccountIcon) as ImageView
                val names = view.findViewById(R.id.tvIconName) as TextView
                icon.setImageResource(icons[i])
                names.text = name[i]

            } else {
                view = inflter.inflate(R.layout.spinner_colour, null)
                val colour = view.findViewById(R.id.ivAccountColour) as ImageView
                val names = view.findViewById(R.id.tvColourName) as TextView
                //colour.setBackgroundColor(Color.rgb(200,83,81))
                colour.setBackgroundResource(icons[i])

                names.text = name[i]
            }
            return view

        }
    }

//  Close activity once toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
