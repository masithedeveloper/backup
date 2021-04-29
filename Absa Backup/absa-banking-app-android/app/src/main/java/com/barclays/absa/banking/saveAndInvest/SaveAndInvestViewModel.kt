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

package com.barclays.absa.banking.saveAndInvest

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.express.invest.getBankNames.dto.BankNameInfo
import com.barclays.absa.banking.express.invest.getProductDetailsInfo.dto.SaveAndInvestProductInfo
import com.barclays.absa.banking.express.invest.saveEmploymentDetails.dto.DataSharingAndMarketingConsent
import com.barclays.absa.banking.express.invest.saveEmploymentDetails.dto.EmploymentDetails
import com.barclays.absa.banking.express.invest.saveEmploymentDetails.dto.PreferredMarketingChannels
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.AccountCreationDetails
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.AddressDetails
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.ContactDetails
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.PersonalDetails
import com.barclays.absa.banking.express.shared.getCustomerDetails.dto.CustomerCifInfo
import com.barclays.absa.banking.express.shared.getLookupInfo.dto.LookupItem
import com.barclays.absa.banking.express.shared.getRiskRating.dto.RiskRatingResponse
import com.barclays.absa.banking.express.shared.getSavedApplications.dto.SavedApplication
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.shared.responseListeners.FetchClientAgreementDetailsExtendedResponseListener
import com.barclays.absa.banking.shared.responseListeners.UpdatePersonalClientAgreementExtendedResponseListener

open class SaveAndInvestViewModel : BaseViewModel() {

    private val fetchClientAgreementDetailsExtendedResponseListener: ExtendedResponseListener<ClientAgreementDetails> by lazy { FetchClientAgreementDetailsExtendedResponseListener(this) }
    private val personalClientAgreementExtendedResponseListener: ExtendedResponseListener<TransactionResponse?> by lazy { UpdatePersonalClientAgreementExtendedResponseListener(this) }

    var investmentTerm = MutableLiveData<String>()
    var investmentTermInDays = 0
    var investmentTermInMonths = 1
    var maturityDate = ""
    var savedApplication = SavedApplication()
    var sourceOfFundsList = listOf<LookupItem>()
    var selectedSourceOfFunds = mutableListOf<LookupItem>()
    var selectedOccupation = LookupItem()
    var selectedOccupationStatus = LookupItem()
    var selectedSourceOfIncome = LookupItem()
    var customerDetails = CustomerCifInfo()
    var riskRatingResponse = RiskRatingResponse()
    var saveAndInvestProductInfo = SaveAndInvestProductInfo()

    var accountName = ""
    var initialDepositAmount = "0.00"
    var initialDepositAccount = AccountObject()
    var initialDepositReference = ""
    var interestRate = ""

    var recurringPaymentAmount = "0.00"
    var recurringPaymentAccount = AccountObject()
    var recurringPaymentStartDate = ""
    var recurringPaymentEndDate = ""
    var recurringPaymentReference = ""
    var numberOfPayments = "1"

    var interestPaymentAccount = ""
    var interestBankName = ""
    var interestBranchCode = ""
    var interestAccountType = ""
    var interestAccountTypeCode = ""
    var interestAccountNumber = ""
    var interestReference = ""

    var personalDetails = PersonalDetails()
    var accountCreationDetails = AccountCreationDetails()
    var contactDetails = ContactDetails()
    var addressDetails = AddressDetails()
    var bankList = listOf<BankNameInfo>()

    var employmentDetails = EmploymentDetails()
    var dataSharingAndMarketingConsent = DataSharingAndMarketingConsent()

    var casaReference = ""
    var minimumInvestmentTermInDays = 0

    fun fetchPersonalClientAgreementDetails() {
        BeneficiariesInteractor().fetchClientAgreementDetails(fetchClientAgreementDetailsExtendedResponseListener)
    }

    fun updatePersonalClientAgreement() {
        BeneficiariesInteractor().updateClientAgreementDetails(personalClientAgreementExtendedResponseListener)
    }
}