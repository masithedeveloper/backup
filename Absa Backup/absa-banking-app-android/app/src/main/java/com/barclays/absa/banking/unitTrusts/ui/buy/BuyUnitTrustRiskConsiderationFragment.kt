/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.unitTrusts.ui.buy

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import styleguide.utils.extensions.toTitleCase

class BuyUnitTrustRiskConsiderationFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_risk_consideration_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.buy_unit_trust_risk_consideration).toTitleCase())
        hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_Learn_WhatisRisk")
    }
}