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

package com.barclays.absa.banking.businessBanking.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.newToBank.services.NewToBankInteractor
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveCardPackageResponse
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveOptionalExtrasResponse
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class BusinessBankingViewModel : BaseViewModel() {
    var newToBankService = NewToBankInteractor()
    val businessEvolveIslamicMutableLiveData = MutableLiveData<BusinessEvolveCardPackageResponse>()
    val businessEvolveStandardMutableLiveData = MutableLiveData<BusinessEvolveCardPackageResponse>()
    var businessEvolveOptionalExtrasMutableLiveData = MutableLiveData<BusinessEvolveOptionalExtrasResponse>()
    val selectedPackageMutableLiveData = MutableLiveData<BusinessEvolveCardPackageResponse>()
    var selectedProductType = ""

    private val businessEvolveIslamicExtendedResponseListener = BusinessEvolveIslamicExtendedResponseListener(this)
    private val businessEvolveStandardExtendedResponseListener = BusinessEvolveStandardExtendedResponseListener(this)
    private val businessEvolveOptionalExtrasExtendedResponseListener = BusinessEvolveOptionalExtrasExtendedResponseListener(this)

    fun fetchBusinessEvolvePackages() {
        newToBankService.fetchBusinessEvolveIslamicAccount(businessEvolveIslamicExtendedResponseListener)
        newToBankService.fetchBusinessEvolveStandardAccount(businessEvolveStandardExtendedResponseListener)
    }

    fun fetchBusinessEvolveOptionalExtrasPackage() {
        newToBankService.fetchBusinessEvolveOptionalExtras(businessEvolveOptionalExtrasExtendedResponseListener)
    }
}