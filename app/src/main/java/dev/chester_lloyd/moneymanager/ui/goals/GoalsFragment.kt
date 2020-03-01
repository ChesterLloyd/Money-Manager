package dev.chester_lloyd.moneymanager.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dev.chester_lloyd.moneymanager.R

/**
 * A [Fragment] subclass to show a tabbed layout containing ListViews of transactions based on their
 * categories.
 *
 * @author Chester Lloyd
 * @since 1.0
 */
class GoalsFragment : Fragment() {

    private lateinit var goalsViewModel: GoalsViewModel
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var selectedTab: Int = 0

    /**
     * An [onCreateView] method that sets up the View and tabs
     *
     * @param inflater The LayoutInflater object
     * @container The parent view.
     * @param savedInstanceState Fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        goalsViewModel = ViewModelProvider(this)[GoalsViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_goals, container, false)
        goalsViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        // Set up tabs
        tabLayout = root.findViewById(R.id.tabs) as TabLayout
        viewPager = root.findViewById(R.id.viewpager) as ViewPager
        viewPager!!.adapter = MyTabsAdapter(childFragmentManager)
        tabLayout!!.post { tabLayout!!.setupWithViewPager(viewPager) }
        onChangeListener()

        return root
    }

    /**
     * When the selected tab changes, update the [selectedTab] variable.
     */
    private fun onChangeListener() {
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedTab = tab.position + 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    /**
     * An inner class that loads a [Fragment] containing a ListView of pie charts for income or
     * expenses.
     *
     * @param fm A [FragmentManager].
     */
    private inner class MyTabsAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        /**
         * Get the [Fragment] to be placed within this page that loads the corresponding pie charts
         * based on income or expenses.
         *
         * @param position Position of the tab in the tabbed layout.
         * @return A [Fragment], passing it the ID of the selected tab.
         */
        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                position -> fragment = GoalsTabFragment(position)
            }
            return fragment!!
        }

        /**
         * Returns number of tabs.
         *
         * @return The number of tabs.
         */
        override fun getCount(): Int {
            return 2
        }

        /**
         * Get the title of the tab.
         *
         * @param position Position of the tab in the tabbed layout.
         * @return The category's name, or null
         */
        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.income)
                1 -> getString(R.string.expense)
                else -> null
            }
        }
    }
}