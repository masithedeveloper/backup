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

package com.barclays.absa.banking.overdraft.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.explore.services.dto.BusinessBankOverdraftData
import com.barclays.absa.banking.framework.BaseActivity

class BusinessOverdraftActivity : BaseActivity(R.layout.activity_business_overdraft) {
    private val businessOverdraftViewModel by viewModels<BusinessOverdraftViewModel>()

    companion object {
        const val BUSINESS_OVERDRAFT_ANALYTIC_TAG = "BusinessVCLOverdraft"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getParcelableExtra<BusinessBankOverdraftData>(IntentFactoryOverdraft.BUSINESS_OVERDRAFT_DATA)?.let {
            businessOverdraftViewModel.businessBankOverdraftData = it
        }
    }
}