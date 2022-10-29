package dev.chester_lloyd.moneymanager.ui.dashboard

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.widget.Toast
import dev.chester_lloyd.moneymanager.MainActivity
import dev.chester_lloyd.moneymanager.R
import dev.chester_lloyd.moneymanager.databinding.ActivitySetupAppBinding
import java.text.NumberFormat

/**
 * An [AppCompatActivity] subclass that is shown when the user first loads the app that requests any
 * setup details.
 *
 * @suppress DEPRECATION.
 * @author Chester Lloyd
 * @since 1.0
 */
@Suppress("DEPRECATION")
class SetupApp : AppCompatActivity() {

    private lateinit var binding: ActivitySetupAppBinding
    private var symbol = NumberFormat.getCurrencyInstance().currency.symbol
    private var group = ","
    private var decimal = "."
    private var suffix = ""
    private var setup = true

    /**
     * An [onCreate] method that sets up the FAB and view.
     *
     * @param savedInstanceState Fragment is being re-constructed from a previous saved state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar name
        this.supportActionBar?.title = getString(R.string.settings_currency)

        val format = MainActivity.getCurrencyFormat(this)
        if (format[0] != "" || format[3] != "") {
            // The app has been set up, load saved values
            setup = false
            symbol = format[0]
            group = format[1]
            decimal = format[2]
            suffix = format[3]

            // Show toolbar back button
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        updatePreview()
        binding.etCurrencySymbol.setText(symbol)
        binding.etCurrencyGroup.setText(group)
        binding.etCurrencyDecimal.setText(decimal)
        binding.etCurrencySuffix.setText(suffix)

        /* Update the view when the currency symbol, decimal symbol, digit grouping separator or the
         * suffix has been changed by the user.
         */
        binding.etCurrencySymbol.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                symbol = s.toString()
                updatePreview()
            }
        })
        binding.etCurrencyGroup.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                group = s.toString()
                updatePreview()
            }
        })
        binding.etCurrencyDecimal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                decimal = s.toString()
                updatePreview()
            }
        })
        binding.etCurrencySuffix.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                suffix = s.toString()
                updatePreview()
            }
        })

        // Save these preferences with FAB
        binding.fabSetupCurrency.setOnClickListener {
            if ((symbol.replace("\\s".toRegex(), "") == "") &&
                (suffix.replace("\\s".toRegex(), "") == "")
            ) {
                // Symbol and suffix are blank
                Toast.makeText(this, R.string.settings_currency_blank, Toast.LENGTH_SHORT).show()
            } else {
                // All data has been filled out, start saving
                MainActivity.updateCurrencyFormat(this, symbol, group, decimal, suffix)

                if (!setup) {
                    Toast.makeText(this, R.string.settings_currency_updated, Toast.LENGTH_SHORT)
                        .show()
                }
                this.finish()
            }
        }
    }

    /**
     * A method that updates the currency preview as the user changes the data in the text boxes.
     *
     * @suppress ResourceType.
     */
    @SuppressLint("ResourceType")
    fun updatePreview() {
        if (decimal == "") decimal = " "
        val colourStr = resources.getString(R.color.colorPrimary)
        val colour = "#${colourStr.subSequence(3, colourStr.length)}"

        binding.tvCurrencyPreview.text = Html.fromHtml(
            "<font color='$colour'>${symbol}</font>3" +
                    "<font color='$colour'>${group}</font>000" +
                    "<font color='$colour'>${decimal}</font>50" +
                    "<font color='$colour'>${suffix}</font>"
        )
    }

    /**
     * An [onSupportNavigateUp] method that closes this activity (goes to previous page) once
     * toolbar back button is pressed.
     *
     * @return true if Up navigation completed successfully and this Activity was finished, false
     * otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * An [onBackPressed] method that prevents closing this activity once navbar back button is
     * pressed.
     */
    override fun onBackPressed() {
        if (!setup) {
            super.onBackPressed()
        } else {
            // Not calling super so the back button has been disabled, show a warning
            Toast.makeText(this, R.string.app_setup_back, Toast.LENGTH_LONG).show()
        }
    }
}
