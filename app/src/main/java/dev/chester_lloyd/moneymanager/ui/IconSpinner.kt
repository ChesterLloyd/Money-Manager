package dev.chester_lloyd.moneymanager.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import dev.chester_lloyd.moneymanager.R

@Suppress("NAME_SHADOWING")
class IconSpinner (
    context: Context,
    private var icons: Array<Icon>?,
    private var backgrounds: Array<Icon>?,
    private var spinnerType: String) : BaseAdapter() {

    private var inflter: LayoutInflater = LayoutInflater.from(context)

//  Spinner adapter class to fill each item with icons or colours and labels

    override fun getCount(): Int {
        if (icons != null) {
            return icons!!.size
        }
        return backgrounds!!.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view: View?
//        val iconManager = IconManager(context)

        if (spinnerType == "icon") {
//          If we are adding icons to the spinner
            view = inflter.inflate(R.layout.spinner_icon, null)
            val icon = view.findViewById(R.id.ivAccountIcon) as ImageView
            val names = view.findViewById(R.id.tvIconName) as TextView

            icon.setImageResource(icons!![i].drawable)
            println(icons!![i].id)
            println(icons!![i].drawable)
            if (backgrounds != null) {
                icon.setBackgroundResource(backgrounds!![i].drawable)
            }
            names.text = icons!![i].text
        } else {
//          If we are adding anything else, i.e. colours
            view = inflter.inflate(R.layout.spinner_colour, null)
            val colour = view.findViewById(R.id.ivAccountColour) as ImageView
            val names = view.findViewById(R.id.tvColourName) as TextView
            //colour.setBackgroundColor(Color.rgb(200,83,81))
            colour.setBackgroundResource(backgrounds!![i].drawable)

            names.text = backgrounds!![i].text
        }
        return view
    }
}


