/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.presentation.contactUs

import android.os.Bundle
import android.view.View

class ContactUsSouthAfricaFragment : ContactUsFragmentBase() {

    companion object {
        internal fun newInstance(tabTitle: String): ContactUsSouthAfricaFragment =
                ContactUsSouthAfricaFragment().apply { putTabTitle(tabTitle) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLocalLayout()
    }

    private fun setLocalLayout() {
        setItem(emergencyLayout,
                ContactUsEnum.LocalFraudHotline,
                ContactUsEnum.LocalLostStolenCard,
                ContactUsEnum.LocalBankingAppSupportPhone,
                ContactUsEnum.BankingAppSupportEmail)
        setItem(generalLayout, ContactUsEnum.LocalMainSwitchBoard)
    }
}