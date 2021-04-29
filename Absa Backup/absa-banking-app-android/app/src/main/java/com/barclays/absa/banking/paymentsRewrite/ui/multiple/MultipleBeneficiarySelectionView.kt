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
 */
package com.barclays.absa.banking.paymentsRewrite.ui.multiple

import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.framework.BaseView

interface MultipleBeneficiarySelectionView : BaseView {
    override val isBusinessAccount: Boolean
    val selectedBeneficiaries: List<RegularBeneficiary>

    fun navigateToChooseAccountScreen()
    fun getSectionListBeneficiary(selectedPosition: Int): RegularBeneficiary?
    fun onBeneficiaryClicked(adapterPosition: Int)
    fun notifyOnItemSelection()
    fun notifyOnItemDeselection(position: Int)
    fun toggleContinueButton(enable: Boolean)
    fun onBeneficiaryListFiltered(paymentBeneficiaryList: List<RegularBeneficiary>)
    fun stopSearch()
}