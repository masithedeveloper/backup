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
package com.barclays.absa.banking.card.ui.secondaryCard

import android.content.Context
import com.barclays.absa.banking.express.secondaryCard.updateSecondaryCard.SecondaryCardUpdateMandateViewModel
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.viewModel

abstract class SecondaryCardBaseFragment(@androidx.annotation.LayoutRes layout: Int) : BaseFragment(layout) {
    protected lateinit var secondaryCardActivity: SecondaryCardActivity
    protected lateinit var secondaryViewModel: SecondaryViewModel
    protected lateinit var secondaryCardUpdateMandateViewModel: SecondaryCardUpdateMandateViewModel

    companion object {
        const val SECONDARY_CARD_TENANT_MANDATE_ACTIVE = "Y"
        const val SECONDARY_CARD_TENANT_MANDATE_DEACTIVATED = "N"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        secondaryCardActivity = context as SecondaryCardActivity
        secondaryViewModel = secondaryCardActivity.viewModel()
        secondaryCardUpdateMandateViewModel = viewModel()
    }
}