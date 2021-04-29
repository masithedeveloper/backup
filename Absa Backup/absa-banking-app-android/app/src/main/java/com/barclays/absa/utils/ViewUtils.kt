/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.utils

import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView

object ViewUtils {
    fun scrollToTopOfView(scrollView: View, targetView: View) {
        when (scrollView) {
            is NestedScrollView -> scrollView.post { scrollView.smoothScrollTo(0, targetView.y.toInt()) }
            is ScrollView -> scrollView.post { scrollView.smoothScrollTo(0, targetView.y.toInt()) }
        }
    }

    fun scrollToBottomOfView(scrollView: View, targetView: View) {
        when (scrollView) {
            is NestedScrollView -> scrollView.post { scrollView.smoothScrollTo(0, targetView.y.toInt() + targetView.height) }
            is ScrollView -> scrollView.post { scrollView.smoothScrollTo(0, targetView.y.toInt() + targetView.height) }
        }
    }
}