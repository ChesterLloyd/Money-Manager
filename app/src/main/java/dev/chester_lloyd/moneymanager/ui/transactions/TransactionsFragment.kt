package dev.chester_lloyd.moneymanager.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dev.chester_lloyd.moneymanager.R

class TransactionsFragment : Fragment() {

    private lateinit var transactionsViewModel: TransactionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        transactionsViewModel =
            ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_transactions, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        transactionsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}