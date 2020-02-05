package dev.chester_lloyd.moneymanager

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_add_account.*

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
        val image = intArrayOf(R.drawable.ic_account_bank,
            R.drawable.ic_account_business, R.drawable.ic_account_cash,
            R.drawable.ic_account_credit_card, R.drawable.ic_account_dollar,
            R.drawable.ic_account_gift_card, R.drawable.ic_account_joint,
            R.drawable.ic_account_membership_card, R.drawable.ic_account_paypal,
            R.drawable.ic_account_travel_card, R.drawable.ic_account_wallet)
        val spinnerCustomAdapter = SpinnerCustomAdapter(applicationContext, image, imageName, "icon")
        spinner.adapter = spinnerCustomAdapter

//      Set up the account color spinner
        val colourSpinner = findViewById<Spinner>(R.id.spColour)
        val colourName = resources.getStringArray(R.array.colour_names)
        val colour = intArrayOf(R.drawable.ic_circle_green,
            R.drawable.ic_circle_dark_blue, R.drawable.ic_circle_paypal)
        val spinnerCustomAdapter2 = SpinnerCustomAdapter(applicationContext, colour, colourName, "colour")
        colourSpinner.adapter = spinnerCustomAdapter2

        val account = Account()

        fabAddAccount.setOnClickListener {
            val name = etName.text.toString()
            account.name = name

            if (etBalance.text.toString() != "") {
                val balance:Double = etBalance.text.toString().toDouble()
                account.balance = balance
            }

            println(account.toString())

            var dbManager = dbManager(this)

//            var values = ContentValues()
//            values.put("Name", )

            val ID = dbManager.insertAccount(account)
            if (ID > 0) {
//              Account saved to database
                Toast.makeText(this, "Account saved", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Could not save this account", Toast.LENGTH_LONG).show()
            }
        }

//      Add selected icon to account object
        spIcon?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                account.icon = image[position]
            }
        }

//      Add selected colour to account object
        spColour?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                account.colour = colour[position]
            }
        }
    }

//  Spinner adapter class to fill them with icons or colours
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
//              If we are adding icons to the spinner
                view = inflter.inflate(R.layout.spinner_icon, null)
                val icon = view.findViewById(R.id.ivAccountIcon) as ImageView
                val names = view.findViewById(R.id.tvIconName) as TextView
                icon.setImageResource(icons[i])
                names.text = name[i]

            } else {
//              If we are adding anything else, i.e. colours
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