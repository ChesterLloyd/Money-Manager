package dev.chester_lloyd.moneymanager.ui

import android.content.Context
import android.widget.EditText

class CurrencyValidatior {

    val editText: EditText
    var balance: String = ""
    var balanceFocus: Boolean = false


    constructor(editText: EditText) {
        this.editText = editText
    }


    fun focusListener(gainFocus: Boolean) {
        if (gainFocus) {
//          Check if a Currency symbol is present first
            if (editText.text.firstOrNull() != '£') {
                balanceFocus = true
                editText.setText("£" + editText.text)
            }
        } else {
//          onBlur
//          Check if a empty and remove the symbol
            if (editText.text.firstOrNull() == '£' && editText.text.length == 1) {
                balanceFocus = false
                editText.setText("")
            }
        }
    }

    fun beforeTextChangedListener(s: CharSequence) {
        balance = s.toString()
    }

    fun onTextChangedListener(s: CharSequence) {
//      Get old position of decimal point
        val oldDecimalPos = balance.indexOf('.')
        var newBalance = ""

//      Where to put the cursor if text is replaced
        var cursorPos = 0
        var decimalCount = 0;

        var diff = balance.length - s.length;

//      Check if balance contains multiple - or . or over 2dp
        for (i in 0..s.length - 1) {
            if (s[i] == '-') {
//              Check if current character is a - sign
                if (i == 1) {
//                  Check if this was found at the start (after £), if so add to output string
                    newBalance += s[i]
                } else {
//                  If not, update cursor position to here as this char was removed
                    cursorPos = i
                }
            } else if (s[i] == '£') {
//              Check if current character is a £ sign
                if (i == 0) {
//                  Check if this was found at the start, if so add to output string
                    newBalance += s[i]
                } else {
//                  If not, update cursor position to here as this char was removed
                    cursorPos = i
                }
            } else if (s[i] == '.') {
//              Check if current character is a . sign

                if (decimalCount == 0) {
//                  Check if no decimal points have been added to the output yet

                    if (i >= oldDecimalPos) {
//                     We are adding the decimal at the position of the old one
//                     (or the last in the input), so add it
                        decimalCount++
                        newBalance += s[i]
                    } else if (i == oldDecimalPos - diff) {
//                      Some characters have been removed before it, so add this one
                        decimalCount++
                        newBalance += s[i]
                    } else {
//                      Do not add this decimal point, update cursor position to here
                        cursorPos = i
                    }
                } else {
//                  More than 1 decimal point being added, update cursor position to here
                    cursorPos = i
                }
            } else {
//              This is an allowed digit, keep it
                newBalance += s[i]
            }
        }

        if (decimalCount == 1) {
//          Check if a decimal point is present first
            val splitBalance = newBalance.split(".")
            if (splitBalance[1].length > 2) {
//              If there are more than 2 numbers after dp, remove any past the 2
                newBalance =
                    splitBalance[0] + "." + splitBalance[1].dropLast((splitBalance[1].length - 2))
                cursorPos = newBalance.length
            }
        }

//      Stop user deleting the currency symbol
        if (s.length == 0 && balanceFocus) {
            newBalance = "£"
            cursorPos = 1
        }

//      Add currency symbol in if it is not the first character
        if (s.firstOrNull() != '£' && balanceFocus) {
            newBalance = "£" + newBalance
            cursorPos = 1
        }

//      Update balance and cursor position
        if (editText.text.toString() != newBalance) {
            editText.setText(newBalance)
//          Could try to paste in a load of junk data (not type character by character
            try {
                editText.setSelection(cursorPos)
            } catch (e: IndexOutOfBoundsException) {
                editText.setSelection(1)
            }
        }
    }

    fun getBalance(): Double {
        if (editText.text.firstOrNull() == '£') {
            if (editText.text.length > 1) {
                val splitBalance = editText.text.split("£")

                println(splitBalance[1].toDouble())
                return splitBalance[1].toDouble()
            } else {
                return 0.0
            }
        }
        return editText.text.toString().toDouble()
    }
}