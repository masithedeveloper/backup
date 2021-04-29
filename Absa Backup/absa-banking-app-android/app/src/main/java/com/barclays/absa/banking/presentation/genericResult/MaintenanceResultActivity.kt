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
 *
 */
package com.barclays.absa.banking.presentation.genericResult

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper

class MaintenanceResultActivity : GenericResultActivity() {
    var tapCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hiddenView = View(this).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ViewGroup.LayoutParams(300, 300)
            setOnClickListener {
                tapCount++
                if (tapCount > 2) {
                    ExpressAuthenticationHelper.allowMaintenanceTesting = true
                    BMBApplication.getInstance().topMostActivity.finish()
                }
            }
        }

        binding.contentConstraintLayout.addView(hiddenView)
    }
}