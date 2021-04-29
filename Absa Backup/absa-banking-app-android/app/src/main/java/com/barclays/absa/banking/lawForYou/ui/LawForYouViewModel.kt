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

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount
import com.barclays.absa.banking.lawForYou.services.*
import com.barclays.absa.banking.lawForYou.services.dto.ApplyLawForYou
import com.barclays.absa.banking.lawForYou.services.dto.ApplyLawForYouResponse
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmounts
import com.barclays.absa.banking.lawForYou.services.dto.CoverAmountsResponse
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileDetails
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileResponse
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.shared.services.dto.SuburbResponse

class LawForYouViewModel : BaseViewModel() {

    var lawForYouService = LawForYouInteractor()
    private val requestCoverAmountResponseListener = RequestCoverAmountsResponseListener(this)
    private val personalInformationResponseListener = PersonalInformationResponseListener(this)
    private val suburbResponseListener = SuburbResponseListener(this)
    private val riskProfileResponseListener = RiskProfileDetailsResponseListener(this)
    private val applyLawForYouResponseListener = ApplyLawForYouResponseListener(this)

    lateinit var casaReference: String
    lateinit var applyLawForYou: ApplyLawForYou
    lateinit var selectedRetailAccount: RetailAccount
    lateinit var selectedCoverOption: CoverAmounts
    var selectedOccupation: LookupItem? = null
    var selectedOccupationStatus: LookupItem? = null
    var selectedSourceOfFunds: LookupItem? = null
    var lawForYouDetails: LawForYouDetails = LawForYouDetails()

    var cityAndPostalCodeMutableLiveData = MutableLiveData<SuburbResponse>()
    var personalInformationResponseMutableLiveData = MutableLiveData<PersonalInformationResponse>()
    val coverOptionsMutableLifeData = MutableLiveData<CoverAmountsResponse>()
    var riskProfileResponseMutableLiveData = MutableLiveData<RiskProfileResponse>()
    var applyLawForYouResponseMutableLiveData = MutableLiveData<ApplyLawForYouResponse>()

    fun fetchCoverAmounts() = lawForYouService.requestCoverAmounts(requestCoverAmountResponseListener)

    fun fetchPersonalInformation() = lawForYouService.requestPersonInformation(personalInformationResponseListener)

    fun fetchCityAndPostalCodes(area: String) = lawForYouService.requestSuburb(area, suburbResponseListener)

    fun requestRiskProfile(riskProfileDetails: RiskProfileDetails) = lawForYouService.requestRiskProfile(riskProfileDetails, riskProfileResponseListener)

    fun submitLawForYouApplication() = lawForYouService.requestLawForYouApplication(lawForYouDetails, applyLawForYouResponseListener)
}
