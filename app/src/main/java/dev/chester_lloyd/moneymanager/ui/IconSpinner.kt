package dev.chester_lloyd.moneymanager.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import dev.chester_lloyd.moneymanager.R

class IconSpinner (internal var context: Context, internal var icons: IntArray, internal var name: Array<String>, internal var spinnerType: String) : BaseAdapter() { internal var inflter: LayoutInflater

//  Spinner adapter class to fill them with icons or colours

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
//          If we are adding icons to the spinner
            view = inflter.inflate(R.layout.spinner_icon, null)
            val icon = view.findViewById(R.id.ivAccountIcon) as ImageView
            val names = view.findViewById(R.id.tvIconName) as TextView
            icon.setImageResource(icons[i])
            names.text = name[i]

        } else {
//          If we are adding anything else, i.e. colours
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


