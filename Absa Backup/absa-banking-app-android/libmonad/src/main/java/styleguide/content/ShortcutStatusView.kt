/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package styleguide.content

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.shortcut_status_view.view.*
import styleguide.bars.ToggleView.OnCustomCheckChangeListener
import za.co.absa.presentation.uilib.R

class ShortcutStatusView : ConstraintLayout {

    lateinit var shortcutToggle: SwitchCompat
    lateinit var shortcutImageView: ImageView

    var onCustomCheckChangeListener: OnCustomCheckChangeListener? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, -1) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.shortcut_status_view, this)
        toggleBarStyle(shortcutSwitch.isChecked, context)

        shortcutToggle = shortcutSwitch
        shortcutImageView = shortcutMenuImageView
    }

    fun toggleBarStyle(isChecked: Boolean, context: Context) {
        @ColorRes val colorResId = if (isChecked) R.color.pink else R.color.foil
        @ColorInt val color = ContextCompat.getColor(context, colorResId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            shortcutSwitch.trackDrawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
        } else {
            shortcutSwitch.trackDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        shortcutSwitch.isChecked = isChecked
    }

    fun setAccountToggleStyle(compoundButton: CompoundButton?, isChecked: Boolean) {
        toggleBarStyle(isChecked, context)
        onCustomCheckChangeListener?.onCustomCheckChangeListener(compoundButton, isChecked)
    }

    fun setShortcutImage(@DrawableRes drawableResId: Int) = shortcutImageView.setImageResource(drawableResId)

    var shortcutName: String
        get() = shortcutNameTextView.text.toString()
        set(accountName) {
            shortcutNameTextView.text = accountName
        }
}