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
package com.barclays.absa.banking.paymentsRewrite.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.dto.OcrResponse
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.express.getAllBalances.dto.AccountTypesExpress
import com.barclays.absa.banking.express.payments.dto.BaseValidatePaymentRequest
import com.barclays.absa.banking.express.payments.validateOnceOffPayment.dto.ValidateOnceOffPaymentRequest
import com.barclays.absa.banking.express.payments.validatePayment.dto.ValidatePaymentRequest
import com.barclays.absa.banking.express.shared.dto.SourceAccount
import com.barclays.absa.banking.paymentsRewrite.services.ClientAgreementExtendedResponseListener
import com.barclays.absa.banking.paymentsRewrite.services.UpdateClientAgreementExtendedResponseListener

class PaymentsViewModel : PaymentsBaseViewModel() {

    private val clientAgreementExtendedResponseListener by lazy { ClientAgreementExtendedResponseListener(this) }
    private val updateClientAgreementExtendedResponseListener by lazy { UpdateClientAgreementExtendedResponseListener(this) }
    private val beneficiariesInteractor by lazy { BeneficiariesInteractor() }

    lateinit var paymentsSourceAccounts: List<SourceAccount>
    lateinit var recentlyPaidBeneficiaryList: List<RegularBeneficiary>
    lateinit var validatePaymentRequest: BaseValidatePaymentRequest
    lateinit var selectedSourceAccount: SourceAccount
    lateinit var paymentAmount: String

    lateinit var clientAgreementDetailsLiveData: MutableLiveData<ClientAgreementDetails>
    lateinit var updatedClientAgreementLiveData: MutableLiveData<Boolean>

    var dualAuthorisationRequired = false

    fun isSourceAccountInitialised(): Boolean = ::selectedSourceAccount.isInitialized

    fun createOnceOffValidatePaymentRequest() = ValidateOnceOffPaymentRequest(selectedBeneficiary)

    fun createValidatePaymentRequest(): ValidatePaymentRequest = ValidatePaymentRequest(selectedBeneficiary)

    fun fetchClientAgreementDetails() {
        beneficiariesInteractor.fetchClientAgreementDetails(clientAgreementExtendedResponseListener)
    }

    fun updateClientAgreementStatus() {
        beneficiariesInteractor.updateClientAgreementDetails(updateClientAgreementExtendedResponseListener)
    }

    fun determineAccountTypeIndex(ocrResponse: OcrResponse): Int = if (ocrResponse.accountNumber?.startsWith("9") == true) 1 else 0

    fun shouldDisableIIPForCreditCard(): Boolean = selectedBeneficiary.beneficiaryDetails.realTimePaymentAllowed && selectedSourceAccount.accountType == AccountTypesExpress.CreditCard.name
}