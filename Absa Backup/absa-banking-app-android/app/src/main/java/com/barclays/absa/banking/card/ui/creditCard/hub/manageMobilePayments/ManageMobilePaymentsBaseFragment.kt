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
package com.barclays.absa.banking.card.ui.creditCard.hub.manageMobilePayments

import android.content.Context
import androidx.annotation.LayoutRes
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import styleguide.screens.GenericResultScreenProperties

abstract class ManageMobilePaymentsBaseFragment(@LayoutRes contentLayoutId: Int) : BaseFragment(contentLayoutId) {

    lateinit var manageMobilePaymentsInterface: ManageMobilePaymentsInterface
    lateinit var manageMobilePaymentsViewModel: ManageMobilePaymentsViewModel
    lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        manageMobilePaymentsInterface = context as ManageMobilePaymentsInterface
        manageMobilePaymentsViewModel = manageMobilePaymentsInterface.manageMobilePaymentsViewModel()
    }

    fun getSuccessProperties(): GenericResultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
            .setResultScreenAnimation(ResultAnimations.generalSuccess)
            .setTitle(getString(R.string.mobile_payments_scan_to_pay_successful))
            .setDescription(getString(R.string.mobile_payments_scan_to_pay_successful_message))
            .setPrimaryButtonLabel(getString(R.string.done))
            .build(true)

    fun getFailureProperties(isInvalidCard: Boolean = false): GenericResultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
            .setResultScreenAnimation(ResultAnimations.generalFailure)
            .setTitle(getString(R.string.mobile_payments_scan_to_pay_unsuccessful))
            .setDescription(if (isInvalidCard) getString(R.string.mobile_payments_scan_to_pay_unsuccessful_invalid_card_message) else getString(R.string.mobile_payments_scan_to_pay_unsuccessful_message))
            .setPrimaryButtonLabel(getString(R.string.done))
            .build(false)
}