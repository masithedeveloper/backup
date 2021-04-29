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

package com.barclays.absa.banking.fixedDeposit.responseListeners

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.BankBranches
import com.barclays.absa.banking.boundary.model.BankDetails
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.fixedDeposit.FixedDepositData
import com.barclays.absa.banking.fixedDeposit.FixedDepositPayoutDetailsData
import com.barclays.absa.banking.fixedDeposit.FixedDepositReinvestInstructionsFragment.ReinvestInstruction
import com.barclays.absa.banking.fixedDeposit.FixedDepositRenewalInstructionData
import com.barclays.absa.banking.fixedDeposit.services.dto.*
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.model.FicaCheckResponse
import com.barclays.absa.banking.overdraft.services.OverdraftInteractor
import com.barclays.absa.banking.payments.services.PaymentsInteractor
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.shared.responseListeners.FetchClientAgreementDetailsExtendedResponseListener
import com.barclays.absa.banking.shared.responseListeners.UpdatePersonalClientAgreementExtendedResponseListener

class FixedDepositViewModel : BaseViewModel() {

    var fixedDepositInteractor: FixedDepositInteractor = FixedDepositInteractor()
    var paymentsInteractor: PaymentsInteractor = PaymentsInteractor()
    var overdraftInteractor: OverdraftInteractor = OverdraftInteractor()
    var beneficiariesInteractor: BeneficiariesInteractor = BeneficiariesInteractor()

    private val interestRateInfoResponseListener: ExtendedResponseListener<FixedDepositInterestRateInfoResponse> by lazy { FixedDepositInterestRateInfoExtendedResponseListener(this) }
    private val accountDetailResponseListener: ExtendedResponseListener<FixedDepositAccountDetailsResponse> by lazy { FixedDepositAccountDetailsExtendedResponseListener(this) }

    private val createAccountExtendedResponseListener: ExtendedResponseListener<FixedDepositCreateAccountResponse> by lazy { FixedDepositCreateAccountExtendedResponseListener(this) }
    private val createAccountConfirmExtendedResponseListener: ExtendedResponseListener<FixedDepositCreateAccountConfirmResponse> by lazy { FixedDepositCreateAccountConfirmExtendedResponseListener(this) }
    private val createAccountProcessExtendedResponseListener: ExtendedResponseListener<FixedDepositCreateAccountProcessResponse> by lazy { FixedDepositCreateAccountProcessExtendedResponseListener(this) }
    private val updateAccountExtendedResponseListener: ExtendedResponseListener<FixedDepositUpdateAccountDetailsResponse> by lazy { FixedDepositUpdateAccountExtendedResponseListener(this) }
    private val renewalInstructionExtendedResponseListener: ExtendedResponseListener<FixedDepositRenewalInstructionResponse> by lazy { FixedDepositRenewalInstructionExtendedResponseListener(this) }
    private val confirmRenewalInstructionExtendedResponseListener: FixedDepositConfirmRenewalInstructionExtendedResponseListener by lazy { FixedDepositConfirmRenewalInstructionExtendedResponseListener(this) }
    private val createRenewalInstructionExtendedResponseListener: FixedDepositCreateRenewalInstructionExtendedResponseListener by lazy { FixedDepositCreateRenewalInstructionExtendedResponseListener(this) }

    private val bankListExtendedResponseListener: ExtendedResponseListener<BankDetails> by lazy { FixedDepositBankListExtendedResponseListener(this) }
    private val branchListExtendedResponseListener: ExtendedResponseListener<BankBranches?> by lazy { FixedDepositBranchListExtendedResponseListener(this) }

    private val ficaCheckExtendedResponseListener: ExtendedResponseListener<FicaCheckResponse> by lazy { FixedDepositFicaCheckExtendedResponseListener(this) }

    private val personalClientAgreementExtendedResponseListener: ExtendedResponseListener<TransactionResponse?> by lazy { UpdatePersonalClientAgreementExtendedResponseListener(this) }
    private val fetchClientAgreementDetailsExtendedResponseListener: ExtendedResponseListener<ClientAgreementDetails> by lazy { FetchClientAgreementDetailsExtendedResponseListener(this) }

    val ficaCheckResponse = MutableLiveData<FicaCheckResponse>()
    val bankDetails = MutableLiveData<BankDetails>()
    val bankBranches = MutableLiveData<BankBranches>()

    var investmentTerm = MutableLiveData<String>()
    val interestRateInfo = MutableLiveData<FixedDepositInterestRateInfoResponse>()
    val accountDetailsResponse = MutableLiveData<FixedDepositAccountDetailsResponse>()
    val updateAccountDetailsResponse = MutableLiveData<FixedDepositUpdateAccountDetailsResponse>()
    val renewalInstructionResponse = MutableLiveData<FixedDepositRenewalInstructionResponse>()
    var confirmRenewalInstructionResponse = MutableLiveData<FixedDepositConfirmRenewalInstructionResponse>()
    val createRenewalInstructionResponse = MutableLiveData<FixedDepositCreateRenewalInstructionResponse>()

    val createAccountResponse = MutableLiveData<FixedDepositCreateAccountResponse>()
    var createAccountConfirmResponse = MutableLiveData<FixedDepositCreateAccountConfirmResponse>()
    var createAccountProcessResponse = MutableLiveData<FixedDepositCreateAccountProcessResponse>()

    var fixedDepositData = FixedDepositData()
    var fixedDepositPayoutDetailsData = FixedDepositPayoutDetailsData()
    var renewalInstructionData = FixedDepositRenewalInstructionData()
    var currentRateTable = TermDepositInterestRateDayTable()

    var dayTableList: ArrayList<Int> = ArrayList()

    var reinvestInstruction: ReinvestInstruction = ReinvestInstruction.NONE
    lateinit var accountObject: AccountObject

    fun fetchInterestRateInfo() {
        fixedDepositInteractor.fetchInterestRateInfo(interestRateInfoResponseListener)
    }

    fun createAccount() {
        if (createAccountResponse.value == null) {
            fixedDepositInteractor.createAccount(createAccountExtendedResponseListener)
        }
    }

    fun confirmAccount() {
        if (createAccountConfirmResponse.value == null) {
            fixedDepositInteractor.createAccountConfirm(createAccountConfirmExtendedResponseListener)
        }
    }

    fun processAccount(fixedDepositData: FixedDepositData, sureCheckAccepted: Boolean) {
        fixedDepositInteractor.createAccountProcess(fixedDepositData, sureCheckAccepted, createAccountProcessExtendedResponseListener)
    }

    fun fetchAccountDetails(accountNumber: String) {
        fixedDepositInteractor.fetchAccountDetail(accountNumber, accountDetailResponseListener)
    }

    fun fetchBankList() {
        if (bankDetails.value == null) {
            paymentsInteractor.fetchBankList(bankListExtendedResponseListener)
        }
    }

    fun fetchBranchList(bankName: String) {
        paymentsInteractor.fetchBranchList(bankName, branchListExtendedResponseListener)
    }

    fun performFicaCheck() {
        overdraftInteractor.fetchFICAAndCIFStatus(ficaCheckExtendedResponseListener)
    }

    fun updatePersonalClientAgreement() {
        beneficiariesInteractor.updateClientAgreementDetails(personalClientAgreementExtendedResponseListener)
    }

    fun fetchPersonalClientAgreementDetails() {
        beneficiariesInteractor.fetchClientAgreementDetails(fetchClientAgreementDetailsExtendedResponseListener)
    }

    fun updateAccountDetails() {
        fixedDepositInteractor.requestAccountUpdate(fixedDepositPayoutDetailsData, updateAccountExtendedResponseListener)
    }

    fun fetchRenewalInstruction(accountNumber: String) {
        fixedDepositInteractor.requestRenewalInstruction(accountNumber, renewalInstructionExtendedResponseListener)
    }

    fun confirmRenewalInstruction() {
        fixedDepositInteractor.requestRenewalInstructionConfirmation(renewalInstructionData, confirmRenewalInstructionExtendedResponseListener)
    }

    fun createRenewalInstruction() {
        fixedDepositInteractor.createRenewalInstruction(renewalInstructionData, createRenewalInstructionExtendedResponseListener)
    }

    fun isIslamicAccount(): Boolean = accountDetailsResponse.value?.fixedDeposit?.description?.contains("Islam", true) ?: false
}