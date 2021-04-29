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
package com.barclays.absa.banking.unitTrusts.ui.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.unitTrusts.services.UnitTrustInteractor
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService
import com.barclays.absa.banking.unitTrusts.services.dto.*

class ViewUnitTrustViewModel : BaseViewModel() {

    val buyMoreUnitsCappedLiveData = MutableLiveData<BuyMoreUnitsCappedResponse>()
    var buyMoreUnitsValidLumpSumLiveData = MutableLiveData<ValidationStatus>()

    private val _unitTrustRedemptionAccountLiveData = MutableLiveData<UnitsTrustRedemptionAccountResponse>()
    val unitTrustRedemptionAccountLiveData: LiveData<UnitsTrustRedemptionAccountResponse>
        get() = _unitTrustRedemptionAccountLiveData

    private val _buyMoreUnitsLinkedAccountsLiveData = MutableLiveData<BuyMoreUnitsLinkedAccountsResponse>()
    val buyMoreUnitsLinkedAccountsLiveData: LiveData<BuyMoreUnitsLinkedAccountsResponse>
        get() = _buyMoreUnitsLinkedAccountsLiveData

    private val _switchUnitTrustFundsLiveData = MutableLiveData<SwitchFundsListResponse>()
    val switchUnitTrustFundsLiveData: LiveData<SwitchFundsListResponse>
        get() = _switchUnitTrustFundsLiveData

    private val _buyMoreFundsResponseLiveData = MutableLiveData<BuyMoreFundsResponse>()
    val buyMoreFundsResponseLiveData: LiveData<BuyMoreFundsResponse>
        get() = _buyMoreFundsResponseLiveData

    private val _redeemFundResponseLiveData = MutableLiveData<RedeemFundResponse>()
    val redeemFundResponseLiveData: LiveData<RedeemFundResponse>
        get() = _redeemFundResponseLiveData

    private val _switchFundResponseLiveData = MutableLiveData<SwitchFundResponse>()
    val switchFundResponseLiveData: LiveData<SwitchFundResponse>
        get() = _switchFundResponseLiveData


    val buyMoreUnitsData = MutableLiveData<BuyMoreUnitsInfo>()

    val unitTrustRedemptionAccountData = MutableLiveData<FundRedemptionInfo>()

    val unitTrustSwitchAccountData = MutableLiveData<FundSwitchInfo>()

    lateinit var buyUnitTrustActions: UnitTrustFundActions

    private val unitTrustService: UnitTrustService by lazy { UnitTrustInteractor() }

    var switchToFundPdfUrl = ""

    fun fetchBuyMoreUnitsCappedStatus() {
        buyMoreUnitsData.value?.fund?.fundCode?.let {
            unitTrustService.fetchBuyMoreUnitsCappedStatus(it, BuyMoreUnitsCappedExtendedResponseListener(this))
        }
    }

    fun fetchUnitTrustAccountRedemptionStatus() {
        buyMoreUnitsData.value?.accountNumber?.let {
            unitTrustService.fetchUnitTrustRedemptionAccountStatus(it, UnitTrustRedemptionAccountResponseListener(_unitTrustRedemptionAccountLiveData))
        }
    }

    fun fetchLinkedAccounts() {
        if (_buyMoreUnitsLinkedAccountsLiveData.value == null) {
            unitTrustService.fetchBuyMoreUnitsLinkedAccounts(ListOfAccountsExtendedResponseListener(_buyMoreUnitsLinkedAccountsLiveData))
        }
    }

    fun fetchLumpSumStatus() {
        buyMoreUnitsData.value?.let {
            unitTrustService.buyMoreUnitsValidateLumpSumAmount(it, BuyMoreUnitsLumpSumResponseListener(this))
        }
    }

    fun resetBuyMoreUnitsValidLumpSumLiveData() {
        buyMoreUnitsValidLumpSumLiveData = MutableLiveData()
    }

    fun buyMoreFunds(sureCheckAccepted: Boolean) {
        buyMoreUnitsData.value?.let {
            unitTrustService.fetchBuyMoreUnitsStatus(it, sureCheckAccepted, BuyMoreUnitsResponseListener(_buyMoreFundsResponseLiveData))
        }
    }

    fun redeemFund() {
        unitTrustRedemptionAccountData.value?.let {
            unitTrustService.fetchRedeemFundStatus(it, RedeemFundResponseListener(_redeemFundResponseLiveData))
        }
    }

    fun fetchUnitTrustFunds() {
        if (_switchUnitTrustFundsLiveData.value == null) {
            unitTrustService.fetchSwitchUnitTrustFunds(SwitchUnitsTrustFundExtendedResponseListener(_switchUnitTrustFundsLiveData))
        }
    }

    fun switchUnitTrustFund() {
        unitTrustSwitchAccountData.value?.let {
            unitTrustService.switchUnitTrustFund(it, SwitchUnitTrustFundExtendedResponseListener(_switchFundResponseLiveData))
        }
    }
}