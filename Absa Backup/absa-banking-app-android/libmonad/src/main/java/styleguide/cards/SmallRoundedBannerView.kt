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
package styleguide.cards

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.alert_view.view.labelTextView
import kotlinx.android.synthetic.main.small_rounded_banner_view.view.*
import za.co.absa.presentation.uilib.R

class SmallRoundedBannerView : ConstraintLayout {

    constructor(context: Context) : super(context, null) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, -1) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.small_rounded_banner_view, this)
        background = ContextCompat.getDrawable(context, R.drawable.small_rounded_banner_background)
    }

    fun setAlert(alert: Alert) {
        labelTextView.text = alert.label
    }

    fun setImage(resourceId: Int) {
        leftIconImageView.setImageDrawable(ContextCompat.getDrawable(context, resourceId))
    }
}
