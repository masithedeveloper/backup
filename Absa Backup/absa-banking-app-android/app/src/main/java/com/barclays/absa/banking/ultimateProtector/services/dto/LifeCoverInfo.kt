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

package com.barclays.absa.banking.ultimateProtector.services.dto

import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount
import com.barclays.absa.banking.shared.services.dto.LookupItem

data class LifeCoverInfo(var medicalQuestionOne: Boolean? = null,
                         var medicalQuestionTwo: Boolean? = null,
                         var medicalQuestionThree: Boolean? = null,
                         var isStepThreeDeclarationAccepted: Boolean? = null,
                         var benefitCode: String = "",
                         var coverAmount: String = "",
                         var monthlyPremium: String = "",
                         var serviceFee: String = "",
                         var dayOfDebit: String = "",
                         var accountInfo: RetailAccount? = null,
                         var employmentStatus: LookupItem? = null,
                         var occupation: LookupItem? = null,
                         var sourceOfFund: LookupItem? = null,
                         var payTo: String = "",
                         var isBeneficiarySelected: Boolean? = null,
                         var beneficiaryInfo: BeneficiaryInfo? = null,
                         var isStepSixDeclarationAccepted: Boolean? = null,
                         var isStepSixTermsAccepted: Boolean? = null,
                         var termsAndConditionPdfUrl: String = "")