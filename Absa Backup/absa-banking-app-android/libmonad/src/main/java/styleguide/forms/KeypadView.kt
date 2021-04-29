/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package styleguide.forms

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.keypad_view.view.*
import za.co.absa.presentation.uilib.R

class KeypadView : ConstraintLayout {
    private lateinit var indicatorArray: MutableList<ImageView>
    private lateinit var buttonsArray: Array<TextView?>
    private val typedKeys = arrayOfNulls<String>(5)
    private var onPasscodeChangeListener: OnPasscodeChangeListener? = null
    private var backSpaceOnClickListener: OnClickListener? = null
    private var forgotPasscodeOnclickListener: OnClickListener? = null
    private var redThemeEnabled = false

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attributes: AttributeSet?) {
        inflate(context, R.layout.keypad_view, this)
        wireUpComponents()
        setUpComponentsListeners()

        val typedArray = context.obtainStyledAttributes(attributes, R.styleable.KeypadView)
        val forgotPasscodeText = typedArray.getString(R.styleable.KeypadView_attribute_forgot_passcode_text)
        redThemeEnabled = typedArray.getBoolean(R.styleable.KeypadView_attribute_red_theme, false)
        typedArray.recycle()

        forgotPasscodeTextView?.text = forgotPasscodeText

        if (redThemeEnabled) {
            applyRedTheme()
        }
    }

    private fun applyRedTheme() {
        for (indicator in indicatorArray) {
            indicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keypad_indicator_unselected_white))
        }
        for (button in buttonsArray) {
            button?.setBackgroundResource(R.drawable.ic_keypad_button_white)
            button?.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        backspaceImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keypad_backspace_white))
    }

    private fun wireUpComponents() {
        indicatorArray = mutableListOf<ImageView>().apply {
            add(indicator1ImageView)
            add(indicator2ImageView)
            add(indicator3ImageView)
            add(indicator4ImageView)
            add(indicator5ImageView)
        }

        buttonsArray = arrayOfNulls(10)
        buttonsArray[0] = button1TextView
        buttonsArray[1] = button2TextView
        buttonsArray[2] = button3TextView
        buttonsArray[3] = button4TextView
        buttonsArray[4] = button5TextView
        buttonsArray[5] = button6TextView
        buttonsArray[6] = button7TextView
        buttonsArray[7] = button8TextView
        buttonsArray[8] = button9TextView
        buttonsArray[9] = button0TextView
    }

    private fun setUpComponentsListeners() {
        for (button in buttonsArray) {
            button?.setOnClickListener { v: View ->
                addToCode((v as TextView).text.toString())
                keypadAnimation(v)
            }
        }

        backspaceImageView.setOnClickListener { v: View ->
            removeFromCode()
            keypadAnimation(v)
            backSpaceOnClickListener?.onClick(v)
        }

        forgotPasscodeTextView.setOnClickListener { v: View ->
            keypadAnimation(v)
            forgotPasscodeOnclickListener?.onClick(v)
        }
    }

    private fun addToCode(keyValue: String) {
        for (i in typedKeys.indices) {
            if (typedKeys[i] == null) {
                typedKeys[i] = keyValue
                setSelectedIndicator(i)
                if (i == typedKeys.size - 1 && onPasscodeChangeListener != null) {
                    onPasscodeChangeListener!!.onKeyEntered(keyValue)
                    onPasscodeChangeListener!!.onCompleted(passcode)
                } else if (onPasscodeChangeListener != null) {
                    onPasscodeChangeListener!!.onKeyEntered(keyValue)
                    onPasscodeChangeListener!!.onChangedPasscode(passcode)
                }
                break
            }
        }
    }

    private fun setSelectedIndicator(position: Int) {
        if (redThemeEnabled) {
            indicatorArray[position].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_keypad_indicator_circle_white))
        } else {
            indicatorArray[position].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_keypad_indicator_selected))
            indicatorArray[position].imageTintList = null
        }
    }

    private val passcode: String
        get() {
            val stringBuilder = StringBuilder()
            for (typedKey in typedKeys) {
                stringBuilder.append(typedKey)
            }
            return stringBuilder.toString()
        }

    private fun removeFromCode() {
        for (i in typedKeys.size - 1 downTo 0) {
            if (typedKeys[i] != null) {
                typedKeys[i] = null
                setUnselectedIndicator(i)
                break
            }
        }
    }

    private fun setUnselectedIndicator(position: Int) {
        if (redThemeEnabled) {
            indicatorArray[position].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_keypad_indicator_unselected_white))
        } else {
            indicatorArray[position].setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_keypad_indicator_unselected))
            indicatorArray[position].imageTintList = null
        }
    }

    private fun keypadAnimation(view: View) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.scale_in_out)
        if (isTextView(view)) {
            val textViewKey = view as TextView
            val keyValue = textViewKey.text.toString()
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    if (isNumber(keyValue)) {
                        if (redThemeEnabled) {
                            view.setBackgroundResource(R.drawable.circular_button_dark_shadow_layer_v24)
                            textViewKey.setTextColor(ContextCompat.getColor(context, R.color.graphite))
                        } else {
                            view.setBackgroundResource(R.drawable.circular_button_dark_shadow_layer_v24)
                            textViewKey.setTextColor(ContextCompat.getColor(context, R.color.white))
                        }
                    }
                }

                override fun onAnimationEnd(animation: Animation) {
                    if (isNumber(keyValue)) {
                        if (redThemeEnabled) {
                            view.setBackgroundResource(R.drawable.ic_keypad_button_white)
                            textViewKey.setTextColor(ContextCompat.getColor(context, R.color.white))
                        } else {
                            view.setBackgroundResource(R.drawable.ic_keypad_grey_button_circles)
                            textViewKey.setTextColor(ContextCompat.getColor(context, R.color.graphite_light_theme_item_color))
                        }
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        view.startAnimation(anim)
    }

    private fun isNumber(text: String): Boolean {
        return try {
            Integer.valueOf(text)
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isTextView(view: View): Boolean {
        return try {
            view as TextView
            true
        } catch (e: ClassCastException) {
            false
        }
    }

    fun hideBottomLeftKey() {
        forgotPasscodeTextView.visibility = GONE
    }

    fun changeForgotPasscodeText(passcodeText: String) {
        forgotPasscodeTextView.text = passcodeText
    }

    fun setForgotPasscodeOnclickListener(onclickListener: OnClickListener?) {
        forgotPasscodeOnclickListener = onclickListener
    }

    fun setClearPasscodeOnclickListener(onclickListener: OnClickListener?) {
        backSpaceOnClickListener = onclickListener
    }

    fun setOnPasscodeChangeListener(onPasscodeChangeListener: OnPasscodeChangeListener?) {
        this.onPasscodeChangeListener = onPasscodeChangeListener
    }

    fun clearPasscode() {
        for (i in typedKeys.indices) {
            typedKeys[i] = null
            setUnselectedIndicator(i)
        }
    }
}