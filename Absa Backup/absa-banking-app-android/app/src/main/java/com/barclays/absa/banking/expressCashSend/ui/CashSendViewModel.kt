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

package com.barclays.absa.banking.expressCashSend.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryConfirmation
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.cashSend.services.CashSendInteractor
import com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.dto.CashSendBeneficiary
import com.barclays.absa.banking.express.cashSend.performCashSend.dto.CashSendPaymentResponse
import com.barclays.absa.banking.express.cashSend.validateCashSend.CashSendValidationDataModel
import com.barclays.absa.banking.express.cashSend.validateCashSend.dto.CashSendValidationResponse
import com.barclays.absa.banking.express.shared.dto.SourceAccount
import com.barclays.absa.banking.expressCashSend.services.ClientAgreementDetailsExtendedResponseListener
import com.barclays.absa.banking.expressCashSend.services.UpdateClientAgreementExtendedResponseListener
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class CashSendViewModel : BaseViewModel() {
    val beneficiariesInteractor: BeneficiariesInteractor = BeneficiariesInteractor()
    val cashSendInteractor: CashSendInteractor = CashSendInteractor()
    val isCashSendPlus = MutableLiveData<Boolean>()

    private val updateClientAgreementExtendedResponseListener: ExtendedResponseListener<TransactionResponse> by lazy { UpdateClientAgreementExtendedResponseListener(this) }
    private val clientAgreementDetailsExtendedResponseListener: ExtendedResponseListener<ClientAgreementDetails> by lazy { ClientAgreementDetailsExtendedResponseListener(this) }

    lateinit var cashSendPaymentResponse: CashSendPaymentResponse
    lateinit var cashSendValidationDataModel: CashSendValidationDataModel
    lateinit var cashSendConfirmationLiveData: MutableLiveData<CashSendBeneficiaryConfirmation>
    lateinit var beneficiaryDetail: CashSendBeneficiary

    lateinit var cashSendValidationResponse: CashSendValidationResponse
    var updateClientAgreementDetailsLiveData: MutableLiveData<TransactionResponse> = MutableLiveData()
    var clientAgreementDetailsLiveData: MutableLiveData<ClientAgreementDetails> = MutableLiveData()
    var searchString: MutableLiveData<String> = MutableLiveData<String>()
    var beneficiaryList: ArrayList<CashSendBeneficiary> = arrayListOf()

    var clientAgreement: String = ""
    var accessPin: String = ""
    var cashSendFlow: CashSendFlow = CashSendFlow.CASH_SEND_TO_BENEFICIARY
    var sourceAccount: SourceAccount = SourceAccount()
    var isCashSendToSelf = false

    fun fetchClientAgreementDetails() {
        beneficiariesInteractor.fetchClientAgreementDetails(clientAgreementDetailsExtendedResponseListener)
    }

    fun updateClientAgreementDetails() {
        beneficiariesInteractor.updateClientAgreementDetails(updateClientAgreementExtendedResponseListener)
    }

    fun accountFundsSufficient(amountEntered: Double) = sourceAccount.availableBalance.toDouble() >= amountEntered
}