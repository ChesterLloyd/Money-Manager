package dev.chester_lloyd.moneymanager.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import dev.chester_lloyd.moneymanager.*
import kotlinx.android.synthetic.main.fragment_transaction_tab.*
import kotlinx.android.synthetic.main.fragment_transaction_tab.view.*
import kotlinx.android.synthetic.main.transaction.view.*
import java.util.*
import kotlin.collections.ArrayList

class TransactionsFragment : Fragment() {

    private lateinit var transactionsViewModel: TransactionsViewModel
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var selectedTab :Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        transactionsViewModel =
            ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_transactions, container, false)



//      Set up tabs
        tabLayout = root.findViewById(R.id.tabs) as TabLayout
        viewPager = root.findViewById(R.id.viewpager) as ViewPager
        viewPager!!.setAdapter(MyTabsAdapter(getFragmentManager()))
        tabLayout!!.post(Runnable { tabLayout!!.setupWithViewPager(viewPager) })
        onchangelistener()


        //      Launch new transaction activity with fab
        val fab: FloatingActionButton = root.findViewById(R.id.fab)
        fab.setOnClickListener {
            //            val intent = Intent(context, AddTransaction::class.java)
//            val intent = Intent(context, Main2Activity::class.java)

//            startActivity(intent)
            println(selectedTab)
        }

        return root
    }

    private fun onchangelistener() {
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Toast.makeText(context, "Tab selcted", Toast.LENGTH_LONG).show()
                selectedTab = tab.position + 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

//
    private inner class MyTabsAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val tabs = 5;

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = TransactionTabFragment(1)
                1 -> fragment = TransactionTabFragment(2)
                2 -> fragment = TransactionTabFragment(3)
                3 -> fragment = TransactionTabFragment(4)
                4 -> fragment = TransactionTabFragment(5)
            }
            return fragment!!
        }

        override fun getCount(): Int {
            return tabs
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "TabLayout1"
                1 -> return "TabLayout2"
                2 -> return "TabLayout3"
                3 -> return "TabLayout4"
                4 -> return "TabLayout5"
            }
            return null
        }
    }
}