package dev.chester_lloyd.moneymanager.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.chester_lloyd.moneymanager.MainActivity
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.ui.dashboard.SetupApp
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A [Fragment] subclass to show the settings screen.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    /**
     * An [onCreateView] method that sets up the buttons and view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val buCurrencyFormat: Button = root.findViewById(R.id.buCurrencyFormat)
        buCurrencyFormat.setOnClickListener {
            startActivity(Intent(context, SetupApp::class.java))
        }

        return root
    }

    /**
     * An [onResume] method that shows the current currency format in a text view as a preview.
     */
    @SuppressLint("ResourceType")
    override fun onResume() {
        super.onResume()

        val format = MainActivity.getCurrencyFormat(context!!)
        val colourStr = resources.getString(R.color.colorPrimary)
        val colour = "#${colourStr.subSequence(3, colourStr.length)}"
        tvCurrencyFormat.text = Html.fromHtml(
            "<font color='$colour'>${format[0]}</font>3" +
                    "<font color='$colour'>${format[1]}</font>000" +
                    "<font color='$colour'>${format[2]}</font>50" +
                    "<font color='$colour'>${format[3]}</font>"
        )
    }
}