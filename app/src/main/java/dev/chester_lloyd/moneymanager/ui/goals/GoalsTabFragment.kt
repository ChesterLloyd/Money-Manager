package dev.chester_lloyd.moneymanager.ui.goals

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import dev.chester_lloyd.moneymanager.PieManager
import dev.chester_lloyd.moneymanager.R
import kotlinx.android.synthetic.main.fragment_goals_tab.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.view.PieChartView
import java.text.SimpleDateFormat
import java.util.*

/**
 * A [Fragment] subclass to show a tabbed layout containing pie charts.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class GoalsTabFragment(private val tab: Int) : Fragment() {

    /**
     * An [onCreateView] method that sets up the View
     *
     * @param inflater The LayoutInflater object
     * @param container The parent view.
     * @param savedInstanceState Fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goals_tab, container, false)
    }

    /**
     * An [onResume] method that creates a pie chart per month that there exists a transaction.
     */
    override fun onResume() {
        super.onResume()

        // Get direction based on tab ID
        var direction = "in"
        if (tab == 1) {
            direction = "out"
        }

        // Get width of display so we know how large to make things
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val xdpi = displayMetrics.xdpi
        val density = displayMetrics.density

        val pieManager = PieManager(context!!)

        // Clear views when coming back to a tab
        llCharts.removeAllViews()

        val dates = pieManager.getAllDates(direction)

        // For every month in the database, make a pie chart for it
        for (date in dates.indices) {
            // Get the month and year from the date
            val month = SimpleDateFormat("MM").format(dates[date].time)
            val year = dates[date].get(Calendar.YEAR).toString()

            // Set up the text to show the month and year of the pie
            val tvDate = TextView(context)
            tvDate.text = "${SimpleDateFormat("MMMM").format(dates[date].time)}, $year"
            tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28F)
            var top = (30 * density + 0.5f).toInt()
            if (date == 0) {
                top = 0
            }
            tvDate.setPadding(0, top, 0, (15 * density + 0.5f).toInt())

            // Set up the pie chart
            val pieChart = PieChartView(context)
            pieChart.pieChartData = PieChartData(pieManager.categoryMonth(month, direction))
                .setHasLabels(true)
//            .setOnValueTouchListener(new ValueTouchListener());

            /*  A bit of maths involved here
             *  px * 160 / dpi = dp
             *
             *  Using this formula, calculate what 1 dp would be in pixels, then take of 16dp
             *  for the left and right hand side margins
             */
            val dp = width * 160 / xdpi
            val margin = width - (width / dp * 16 * 2).toInt()
            pieChart.layoutParams = ViewGroup.LayoutParams(margin, margin)

            // Add them to the view
            llCharts.addView(tvDate)
            llCharts.addView(pieChart)
        }
        pieManager.sqlDB!!.close()
    }
}
