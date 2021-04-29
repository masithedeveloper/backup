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

package com.barclays.absa.banking.freeCover.services.dto

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.freeCover.services.FreeCoverInteractor
import com.barclays.absa.banking.freeCover.ui.FreeCoverData
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class FreeCoverViewModel : BaseViewModel() {
    var freeCoverInteractor = FreeCoverInteractor()
    var freeCoverData = FreeCoverData()
    var applyFreeCoverData = ApplyFreeCoverData()
    var isDeceasedEstate: Boolean = false
    var policyStartDate = ""
    var casaReference = ""
    var selectedEmploymentStatusIndex = -1
    var selectedOccupationStatusIndex = -1

    private val applyForFreeCoverExtendedResponseListener: ExtendedResponseListener<ApplyForFreeCoverResponse> by lazy { ApplyForFreeCoverExtendedResponseListener(this) }
    private val coverAmountApplyFreeCoverExtendedResponseListener: ExtendedResponseListener<CoverAmountApplyFreeCoverResponse> by lazy { CoverAmountApplyFreeCoverExtendedResponseListener(this) }

    var applyForFreeCoverStatusResponse = MutableLiveData<ApplyForFreeCoverResponse>()
    var coverAmountApplyFreeCoverResponse = MutableLiveData<CoverAmountApplyFreeCoverResponse>()

    fun applyFreeCover(applyFreeCoverData: ApplyFreeCoverData) = freeCoverInteractor.applyFreeCover(applyFreeCoverData, applyForFreeCoverExtendedResponseListener)
    fun fetchCoverAmount() = freeCoverInteractor.fetchCoverAmount(coverAmountApplyFreeCoverExtendedResponseListener)
}