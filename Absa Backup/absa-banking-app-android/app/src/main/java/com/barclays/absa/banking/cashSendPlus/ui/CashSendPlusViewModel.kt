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
package com.barclays.absa.banking.cashSendPlus.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject
import com.barclays.absa.banking.boundary.model.PINObject
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.cashSend.services.CashSendInteractor
import com.barclays.absa.banking.cashSendPlus.services.*
import com.barclays.absa.banking.framework.app.BMBConstants.PASS_CASHSEND
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.google.gson.Gson

class CashSendPlusViewModel : BaseViewModel() {

    var cashSendPlusInteractor = CashSendPlusInteractor()
    var cashSendInteractor = CashSendInteractor()
    var beneficiariesInteractor = BeneficiariesInteractor()
    val cashSendPlusRegistration = CashSendPlusRegistration()

    var selectedBeneficiaryPosition = 0
    var useSameAccessPinForAllBeneficiary = false

    val cashSendPlusRegistrationResponse by lazy { MutableLiveData<CashSendPlusRegistrationResponse>() }
    val updateCashSendPlusLimitResponse by lazy { MutableLiveData<UpdateCashSendPlusLimitResponse>() }
    val checkCashSendPlusRegistrationStatusResponse by lazy { MutableLiveData<CheckCashSendPlusRegistrationStatusResponse>() }
    val checkCashSendPlusRegistrationStatusFailedResponse by lazy { MutableLiveData<ResponseObject?>() }
    val cancelCashSendPlusRegistrationResponse by lazy { MutableLiveData<CancelCashSendPlusRegistrationResponse>() }
    val updateCustomerAgreementDetailsResponse by lazy { MutableLiveData<TransactionResponse>() }
    val cashSendPlusBeneficiaryListResponse by lazy { MutableLiveData<BeneficiaryListObject>() }
    val cashSendPlusPinEncryptionResponse by lazy { MutableLiveData<PINObject>() }
    val validateCashSendPlusSendMultipleResponse by lazy { MutableLiveData<CashSendPlusSendMultipleResponse>() }
    val cashSendPlusSendMultipleResponse by lazy { MutableLiveData<CashSendPlusSendMultipleResponse>() }

    private val cashSendPlusRegistrationExtendedResponseListener by lazy { CashSendPlusRegistrationResponseListener(this) }
    private val updateCashSendPlusLimitExtendedResponseListener by lazy { UpdateCashSendPlusLimitResponseListener(this) }
    private val checkCashSendPlusRegistrationStatusExtendedResponseListener by lazy { CheckCashSendPlusRegistrationStatusResponseListener(this) }
    private val cancelCashSendPlusRegistrationExtendedResponseListener by lazy { CancelCashSendPlusRegistrationResponseListener(this) }
    private val updateClientAgreementDetailsExtendedResponseListener by lazy { UpdateCustomerAgreementDetailsResponseListener(this) }
    private val cashSendPlusBeneficiaryListExtendedResponseListener by lazy { CashSendPlusBeneficiaryListResponseListener(this) }
    private val cashSendPlusPinEncryptionResponseListener by lazy { CashSendPlusPinEncryptionResponseListener(this) }
    private val cashSendPlusValidateSendMultipleResponseListener by lazy { ValidateCashSendPlusSendMultipleResponseListener(this) }
    private val cashSendPlusSendMultipleResponseListener by lazy { CashSendPlusSendMultipleResponseListener(this) }

    fun sendCashSendPlusRegistration(cashSendPlusLimitAmount: String, cashSendPlusEmailAddress: String) {
        cashSendPlusInteractor.sendCashSendPlusRegistration(cashSendPlusLimitAmount, cashSendPlusEmailAddress, cashSendPlusRegistrationExtendedResponseListener)
    }

    fun sendUpdateCashSendPlusLimit(newCashSendPlusLimitAmount: String, cashSendPlusLimitAmount: String) {
        cashSendPlusInteractor.sendUpdateCashSendPlusLimit(newCashSendPlusLimitAmount, cashSendPlusLimitAmount, updateCashSendPlusLimitExtendedResponseListener)
    }

    fun sendCashSendPlusRegistrationCancellation() {
        cashSendPlusInteractor.sendCashSendPlusRegistrationCancellation(cancelCashSendPlusRegistrationExtendedResponseListener)
    }

    fun sendCheckCashSendPlusRegistration() {
        cashSendPlusInteractor.sendCheckCashSendPlusRegistration(checkCashSendPlusRegistrationStatusExtendedResponseListener)
    }

    fun sendCashSendPlusValidateSendMultiple(cashSendPlusSendMultipleRequestDataModel: CashSendPlusSendMultipleRequestDataModel) {
        cashSendPlusInteractor.sendCheckCashSendPlusValidateSendMultiple(Gson().toJson(cashSendPlusSendMultipleRequestDataModel), cashSendPlusValidateSendMultipleResponseListener)
    }

    fun sendCashSendPlusSendMultiple(cashSendPlusSendMultipleRequestDataModel: CashSendPlusSendMultipleRequestDataModel) {
        cashSendPlusInteractor.sendCheckCashSendPlusSendMultiple(Gson().toJson(cashSendPlusSendMultipleRequestDataModel), cashSendPlusSendMultipleResponseListener)
    }

    fun updateClientAgreementDetails() {
        cashSendPlusInteractor.updateClientAgreementDetails(updateClientAgreementDetailsExtendedResponseListener)
    }

    fun fetchBeneficiaryList() {
        beneficiariesInteractor.fetchBeneficiaryList(PASS_CASHSEND, cashSendPlusBeneficiaryListExtendedResponseListener)
    }

    fun fetchEncryptedPin(accessPin: String) {
        cashSendInteractor.requestCashSendPinEncryption(accessPin, cashSendPlusPinEncryptionResponseListener)
    }
}