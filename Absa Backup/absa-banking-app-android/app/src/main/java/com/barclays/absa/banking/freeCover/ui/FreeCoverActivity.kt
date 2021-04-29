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

package com.barclays.absa.banking.freeCover.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.freeCover.services.dto.FreeCoverViewModel
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.free_cover_activity.*

class FreeCoverActivity : BaseActivity(R.layout.free_cover_activity), FreeCoverInterface {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var riskBasedApproachViewModel: RiskBasedApproachViewModel
    private lateinit var freeCoverViewModel: FreeCoverViewModel

    companion object {
        const val FREE_COVER_DATA = "freeCoverData"
        const val FREE_COVER_ANALYTICS_TAG = "FreeCover"
        const val FREE_COVER_CASA_REFERENCE = "FREE_COVER_CASA_REFERENCE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolBar as Toolbar?)

        sharedViewModel = viewModel()
        riskBasedApproachViewModel = viewModel()
        freeCoverViewModel = viewModel()
        freeCoverViewModel.casaReference = intent.getStringExtra(FREE_COVER_CASA_REFERENCE) ?: ""
        freeCoverViewModel.freeCoverData = intent.getParcelableExtra(FREE_COVER_DATA) ?: FreeCoverData()

    }

    override fun hideProgressIndicatorView() {
        freeCoverProgressIndicatorView.visibility = View.GONE
    }

    override fun showProgressIndicatorView() {
        freeCoverProgressIndicatorView.visibility = View.VISIBLE
    }

    override fun setStep(step: Int) {
        freeCoverProgressIndicatorView.setNextStep(step)
        freeCoverProgressIndicatorView.animateNextStep()
    }

    override fun sharedViewModel() = sharedViewModel

    override fun riskBasedApproachViewModel() = riskBasedApproachViewModel
}