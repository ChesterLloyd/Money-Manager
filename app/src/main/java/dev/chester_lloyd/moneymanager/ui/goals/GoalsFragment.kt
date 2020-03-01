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

class GoalsFragment : Fragment() {

    private lateinit var goalsViewModel: GoalsViewModel
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var selectedTab :Int = 0
//    private val mainTabs = arrayMapOf(Pair(0, context!!.getString(R.string.income)), Pair(1, context!!.getString(R.string.expense)))

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

    //  On tab change
    private fun onChangeListener() {
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
//                Toast.makeText(context, "Tab selected", Toast.LENGTH_SHORT).show()
                selectedTab = tab.position + 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    //  Adaptor to manage the tabs and fragments that are loaded
    private inner class MyTabsAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                position -> fragment = GoalsTabFragment(position)
            }
            return fragment!!
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Income"
                1 -> "Expenses"
                else -> null
            }
        }
    }
}