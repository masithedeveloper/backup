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
package com.barclays.absa.banking.lawForYou.ui

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment.Companion.LAW_FOR_YOU_CASA_REFERENCE
import com.barclays.absa.banking.explore.ui.NewExploreHubFragment.Companion.LAW_FOR_YOU_OFFERS_RESPONSE
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.lawForYou.services.dto.ApplyLawForYou
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.law_for_you_activity.*

class LawForYouActivity : BaseActivity(R.layout.law_for_you_activity) {

    private lateinit var lawForYouViewModel: LawForYouViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lawForYouViewModel = viewModel()
        lawForYouViewModel.apply {
            applyLawForYou = intent.getParcelableExtra(LAW_FOR_YOU_OFFERS_RESPONSE) ?: ApplyLawForYou()
            casaReference = intent.getStringExtra(LAW_FOR_YOU_CASA_REFERENCE) ?: ""
        }
    }

    fun setProgressIndicatorVisibility(visibility: Int) {
        progressIndicatorView.visibility = visibility
    }

    fun setProgressStep(step: Int) {
        progressIndicatorView.setNextStep(step)
        progressIndicatorView.animateNextStep()
    }
}