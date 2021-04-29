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
package com.barclays.absa.banking.passcode.passcodeLogin

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.airbnb.lottie.LottieAnimationView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.MediaPlayerHelper.playNotificationSound
import com.barclays.absa.banking.framework.app.MediaPlayerHelper.stopNotificationSound
import kotlinx.android.synthetic.main.activity_forgot_passcode.*
import styleguide.utils.extensions.toSentenceCase

class ForgotPasscodeActivity : BaseActivity(R.layout.activity_forgot_passcode) {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lottieAnimationView = resultImageView as LottieAnimationView
        lottieAnimationView.setAnimation("general_alert.json")

        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                playNotificationSound()
                Handler(Looper.getMainLooper()).postDelayed({ stopNotificationSound() }, 3000)
            }

            override fun onAnimationEnd(animation: Animator) = stopNotificationSound()

            override fun onAnimationCancel(animation: Animator) = stopNotificationSound()

            override fun onAnimationRepeat(animation: Animator) = Unit
        })

        lottieAnimationView.playAnimation()
        resetPasscodeButton.text = getString(R.string.reset_passcode).toSentenceCase()

        resetPasscodeButton.setOnClickListener {
            appCacheService.setPasscodeResetFlow(true)
            appCacheService.setShouldRevertToOldLinkingFlow(true)
            val loginIntent = Intent(this@ForgotPasscodeActivity, AccountLoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(loginIntent)
            finish()
        }
    }
}