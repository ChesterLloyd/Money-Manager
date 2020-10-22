package dev.chester_lloyd.moneymanager.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import dev.chester_lloyd.moneymanager.MainActivity.Companion.isPinCorrect
import dev.chester_lloyd.moneymanager.MainActivity.Companion.isPinSet
import dev.chester_lloyd.moneymanager.MainActivity.Companion.updatePin
import dev.chester_lloyd.moneymanager.R
import kotlinx.android.synthetic.main.activity_pin_code.*

/**
 * An [AppCompatActivity] subclass for the PIN code login / setup screen.
 *
 * @author Chester Lloyd
 * @since 1.3
 */
class PinCodeActivity : AppCompatActivity() {

    private var mIndicatorDots: IndicatorDots? = null
    private var newPin = false
    private var updatePin = false
    private var from = ""
    private var pin1 = "x"

    /**
     * An [onCreate] method that sets up the PIN code elements.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        from = this.intent.getStringExtra("from")!!
        newPin = from == "settings" && !isPinSet(applicationContext)
        updatePin = from == "settings" || from == "settings-remove" ||
                from == "update" || from == "update1"

        super.onCreate(savedInstanceState)

        if (updatePin) {
            // Setup toolbar name and show a back button
            when {
                newPin -> {
                    this.supportActionBar?.title = getString(R.string.settings_set_pin_button)
                    from = "new"
                }
                from == "settings-remove" -> {
                    this.supportActionBar?.title = getString(R.string.settings_remove_pin_button)
                }
                else -> {
                    this.supportActionBar?.title = getString(R.string.settings_update_pin_button)
                }
            }
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            // Fullscreen view
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setContentView(R.layout.activity_pin_code)

        when (from) {
            "home" -> {
                tvInstructions.text = ""
            }
            "settings" -> {
                // We are confirming our PIN prior to update
                tvInstructions.text = getText(R.string.settings_confirm_pin)
            }
            "update1" -> {
                // We are confirming our new PIN - set first PIN and instructions
                pin1 = this.intent.getStringExtra("pin1")!!
                tvInstructions.text = getText(R.string.settings_confirm_pin)
            }
            "settings-remove" -> {
                tvInstructions.text = getText(R.string.settings_confirm_pin)
            }
        }

        // Setup PIN code elements
        val mPinLockView = findViewById<View>(R.id.pin_lock_view) as PinLockView
        mIndicatorDots = findViewById<View>(R.id.indicator_dots) as IndicatorDots
        mPinLockView.attachIndicatorDots(mIndicatorDots)
        mPinLockView.setPinLockListener(mPinLockListener)
        // mPinLockView.enableLayoutShuffling();
        mPinLockView.pinLength = 4
        mPinLockView.textColor = ContextCompat.getColor(this, R.color.white)
        mIndicatorDots!!.indicatorType = IndicatorDots.IndicatorType.FILL_WITH_ANIMATION
        mPinLockView.setPinLockListener(mPinLockListener)
    }

    private val mPinLockListener: PinLockListener = object : PinLockListener {
        override fun onComplete(pin: String) {
            // PIN entered
            when (from) {
                "home" -> {
                    if (isPinCorrect(applicationContext, pin)) {
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        tvInstructions.text = getText(R.string.settings_pin_incorrect)
                        clPinCode.setBackgroundColor(resources.getColor(R.color.colorRedBackground))
                    }
                }
                "settings" -> {
                    if (isPinCorrect(applicationContext, pin)) {
                        // PIN is correct, let's get ready to update
                        val pinIntent = Intent(applicationContext, PinCodeActivity::class.java)
                        val pinBundle = Bundle()
                        pinBundle.putString("from", "update")
                        pinIntent.putExtras(pinBundle)
                        startActivityForResult(pinIntent, 0)
                    } else {
                        // PIN did not match - show warning
                        tvInstructions.text = getText(R.string.settings_pin_incorrect)
                        clPinCode.setBackgroundColor(resources.getColor(R.color.colorRedBackground))
                    }
                }
                "update1" -> {
                    // We are confirming our new PIN - check if it matches the second PIN
                    if (pin1 == pin) {
                        // PINs match, update it and go back to settings
                        tvInstructions.text = getText(R.string.settings_pin_set)
                        updatePin(applicationContext, pin)
                        Handler().postDelayed({
                            setResult(RESULT_OK)
                            finish()
                        }, 1_500)
                    } else {
                        // Second PIN did not match - show warning
                        tvInstructions.text = getText(R.string.settings_pin_set_failed)
                        clPinCode.setBackgroundColor(resources.getColor(R.color.colorRedBackground))
                    }
                }
                "settings-remove" -> {
                    if (isPinCorrect(applicationContext, pin)) {
                        // PIN is correct, remove it and go back to settings
                        tvInstructions.text = getText(R.string.settings_pin_removed)
                        updatePin(applicationContext, "x")
                        Handler().postDelayed({
                            setResult(RESULT_OK)
                            finish()
                        }, 1_500)
                    } else {
                        // PIN did not match - show warning
                        tvInstructions.text = getText(R.string.settings_pin_incorrect)
                        clPinCode.setBackgroundColor(resources.getColor(R.color.colorRedBackground))
                    }
                }
                else -> {
                    // Round 2, confirm the first PIN
                    val pinIntent = Intent(applicationContext, PinCodeActivity::class.java)
                    val pinBundle = Bundle()
                    pinBundle.putString("from", "update1")
                    pinBundle.putString("pin1", pin)
                    pinIntent.putExtras(pinBundle)
                    startActivityForResult(pinIntent, 0)
                }
            }
        }

        override fun onEmpty() {
            // No PIN
        }

        override fun onPinChange(pinLength: Int, intermediatePin: String) {
            // PIN after a key press
            if (from == "home") {
                tvInstructions.text = ""
            } else if (from == "settings" || from == "update1" || from == "settings-remove") {
                tvInstructions.text = getText(R.string.settings_confirm_pin)
            }

            // Reset background in case user retypes numbers
            clPinCode.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        }
    }

    /**
     * An [onActivityResult] method that will close this activity if the user has completed (passed
     * or failed) the PIN confirmation step, or pressed a back button.
     */
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK) // Could be closing the confirmation prior to update
            this.finish()
        }
    }

    /**
     * An [onSupportNavigateUp] method that closes this activity once toolbar back button is
     * pressed.
     *
     * @return true if Up navigation completed successfully and this Activity was finished, false
     * otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * An [onBackPressed] method that informs the calling activity that we have closed this activity
     * by toolbar or device back button. This is only allowed when updating the PIN.
     */
    override fun onBackPressed() {
        if (updatePin) {
            setResult(RESULT_OK)
            super.onBackPressed()
        }
    }
}