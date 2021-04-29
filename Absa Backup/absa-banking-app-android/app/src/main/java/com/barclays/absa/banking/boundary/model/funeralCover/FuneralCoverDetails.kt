/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.boundary.model.funeralCover

import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.ultimateProtector.services.dto.BeneficiaryInfo
import java.io.Serializable


class FuneralCoverDetails : ResponseObject(), Serializable {
    var txnStatus: String = ""
    var policyNumber: String = ""
    var debitDate: String = ""
    var policyStartDate: String = ""
    var accountToDebit: String = ""
    var accountNumber: String = ""
    var accountDescription: String = ""
    var accountType: String = ""
    var employmentStatus: String = ""
    var sourceOfFunds: String = ""
    var occupation: String = ""
    var yearlyIncrease: String = ""
    var mainMemberName: String = ""
    var mainMemberCover: String = ""
    var mainMemberPremium: String = ""
    var spouseCover: String = ""
    var spousePremium: String = ""
    var spousePlanCode: String = ""
    var totalMonthlyPremium: String = ""
    var familySelected: String = ""
    var planCode: String = ""
    var totalCoverAmount: String = ""
    var familyInitials: String = ""
    var familySurname: String = ""
    var familyGender: String = ""
    var familyDateOfBirth: String = ""
    var familyCoverAmount: String = ""
    var familyPremium: String = ""
    var familyRelationship: String = ""
    var familyRelationshipCode: String = ""
    var familyBenefitCode: String = ""
    var familyCategory: String = ""
    var beneficiaryInfo: BeneficiaryInfo? = null
    var familyMemberList: MutableList<FamilyMemberCoverDetails> = ArrayList()
}
