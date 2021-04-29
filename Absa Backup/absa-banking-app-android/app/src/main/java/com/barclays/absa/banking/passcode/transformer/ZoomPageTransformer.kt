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
 *
 */
package com.barclays.absa.banking.passcode.transformer

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max

class ZoomPageTransformer(private val pager: ViewPager) : ViewPager.PageTransformer {

    companion object {
        private const val MIN_SCALE = 0.85f
    }

    override fun transformPage(view: View, position: Float) = with(view) {
        val pageWidthFraction: Float = (pager.paddingLeft).toFloat() / pager.measuredWidth
        val adjustedPosition = position - pageWidthFraction
        when {
            adjustedPosition < -1 -> {
                // [-Infinity,-1) - This page is way off-screen to the left.
                scaleX = MIN_SCALE
                scaleY = MIN_SCALE
            }
            adjustedPosition <= 1 -> { // [-1,1] - In page transitions
                // Modify the default slide transition to shrink the page as well
                val scaleFactor = max(MIN_SCALE, 1 - abs(adjustedPosition))

                // Scale the page down (between MIN_SCALE and 1)
                scaleX = scaleFactor
                scaleY = scaleFactor
            }
            else -> {
                // (1,+Infinity] - This page is way off-screen to the right.
                scaleX = MIN_SCALE
                scaleY = MIN_SCALE
            }
        }
    }
}