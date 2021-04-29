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
package com.barclays.absa.utils

import android.view.View
import android.view.animation.AnimationUtils
import com.barclays.absa.banking.R

object AnimationHelper {
    @JvmStatic
    fun shakeShakeAnimate(view: View) {
        val shakeAnimation = AnimationUtils.loadAnimation(view.context, R.anim.shake)
        view.startAnimation(shakeAnimation)
    }
}