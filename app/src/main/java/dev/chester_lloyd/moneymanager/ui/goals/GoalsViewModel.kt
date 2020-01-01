package dev.chester_lloyd.moneymanager.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoalsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is tools Fragment"
    }
    val text: LiveData<String> = _text
}