package dev.chester_lloyd.moneymanager.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.chester_lloyd.moneymanager.R

class GoalsFragment : Fragment() {

    private lateinit var goalsViewModel: GoalsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        goalsViewModel = ViewModelProvider(this)[GoalsViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_goals, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        goalsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = "GOALS"
        })
        return root
    }
}