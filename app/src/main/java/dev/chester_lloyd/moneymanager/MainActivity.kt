package dev.chester_lloyd.moneymanager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import java.text.DecimalFormat
import kotlin.collections.ArrayList

/**
 * An [AppCompatActivity] subclass for the main activity.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    /**
     * An [onCreate] method that sets up the toolbar and navigation drawer.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_accounts, R.id.nav_transactions,
                R.id.nav_categories, R.id.nav_goals, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /**
     * An [onCreateOptionsMenu] method that adds the settings menu to the toolbar.
     *
     * @param menu The options menu to place items.
     * @return True to display the menu, or false to not show the menu.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /**
     * An [onSupportNavigateUp] method that closes this activity (goes to previous page) once
     * toolbar back button is pressed.
     *
     * @return true if Up navigation completed successfully and this Activity was finished, false
     * otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {

        private const val PREFS_FILENAME = "dev.chester-lloyd.moneymanager.prefs"
        private const val PREFS_CURRENCY_SYMBOL = "currency_symbol"
        private const val PREFS_CURRENCY_GROUP = "currency_group"
        private const val PREFS_CURRENCY_DECIMAL = "currency_decimal"
        private const val PREFS_CURRENCY_SUFFIX = "currency_suffix"

        /**
         * A method that updates a collection of shared preferences that stores the currency format.
         *
         * @param context Context.
         * @param symbol The proceeding symbol at the start of a currency value (£, $, PLN, etc).
         * @param group The three digit grouping separator.
         * @param decimal The decimal point symbol.
         * @param suffix Any characters to add to the end of the currency string.
         */
        fun updateCurrencyFormat(context: Context, symbol: String, group: String, decimal: String, suffix: String) {
            val editPrefs = context.getSharedPreferences(PREFS_FILENAME, 0)
                .edit()
            editPrefs.putString(PREFS_CURRENCY_SYMBOL, symbol)
            editPrefs.putString(PREFS_CURRENCY_DECIMAL, decimal)
            editPrefs.putString(PREFS_CURRENCY_GROUP, group)
            editPrefs.putString(PREFS_CURRENCY_SUFFIX, suffix)
                .apply()
        }

        /**
         * A method that reads a collection of shared preference that stores the currency format.
         *
         * @param context Context.
         * @return An [ArrayList] that contains all four parts to the currency format.
         */
        fun getCurrencyFormat(context: Context): ArrayList<String> {
            val prefs: SharedPreferences? = context.getSharedPreferences(PREFS_FILENAME, 0)
            return arrayListOf(
                prefs!!.getString(PREFS_CURRENCY_SYMBOL, "")!!,
                prefs.getString(PREFS_CURRENCY_GROUP, "")!!,
                prefs.getString(PREFS_CURRENCY_DECIMAL, "")!!,
                prefs.getString(PREFS_CURRENCY_SUFFIX, "")!!
            )
        }

        /**
         * Returns a balance given as a [Double] as a [String] with the user defined currency
         * formatting.
         *
         * @param context Context.
         * @return The balance formatted as a currency value.
         */
        fun stringBalance(context: Context, amount: Double): String {
            // Get user specified locale options
            val format = getCurrencyFormat(context)
            var start = ""
            var absAmount = amount

            // Get negative sign, if there
            if (absAmount < 0) {
                start += "-"
                absAmount *= -1
            }

            /* Get the number (now without the minus sign) and add grouping digit. Use comma for
             * first bit, then replace with user defined symbol. Finally, trim the decimal places
             * off.
             */
            val groupString = format[0] + DecimalFormat("#,###.00")
                .format(absAmount.toBigDecimal())
            start += groupString.replace(",", format[1], false)
                .subSequence(0, groupString.length - 3)

            // Get only the decimal places of the number (2dp)
            var decimalPart = DecimalFormat("#.00").format(absAmount.toBigDecimal())
            decimalPart = decimalPart.subSequence(decimalPart.length - 2, decimalPart.length)
                .toString()

            // Put it all together and return
            return "${start}${format[2]}${decimalPart}${format[3]}"
        }
    }
}
