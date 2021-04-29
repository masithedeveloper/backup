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

package com.barclays.absa.banking.express.userProfile.dto

import com.barclays.absa.banking.express.getAllBalances.dto.Account
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatus
import com.barclays.absa.banking.shared.BaseModel
import za.co.absa.networking.dto.BaseResponse

class UserProfileResponse : BaseResponse() {
    val initials: String = ""
    val title: String = ""
    var firstNames: String = ""
    val surname: String = ""
    var idType: String = ""
    var idNumber: String = ""
    val userType: String = ""
    val sbuSegment: String = ""
    val sbuSubSegment: String = ""
    val clientTypeGroup: String = ""
    val clientType: String = ""
    val cellNumber: String = ""
    val numberOfAuthorisationsRequired: Int = 0
    val userNumberOfPendingAuthorisations: Int = 0
    val surephrase: String = ""
    var language: String = ""
    val secondFactorState: Int = 0
    val enrolledForSecondFactor: Boolean = false
    val mailBoxProfileId: String = ""
    val newMailBoxProfileId: Boolean = false
    val customerSessionId: String = ""
    val userId: String = ""
    val primarySecondFactorDevice: Boolean = false
    val secondaryCardAccessBits: Int = 0
    val limitsSet: Boolean = false
    val standaloneCustomer: Boolean = false
    val hasOperatorSystemEnabled: Boolean = false
    val idNumberRequired: Boolean = false
    val lastLoggedIn: String = ""
    val otpSeed: String = ""
    val userAuthorisations: UserAuthorisations = UserAuthorisations()
    val serviceType: String = ""
    val accountList: MutableList<Account> = mutableListOf()
    val clientAgreementAccepted: Boolean = false
    val biometricStatus: BiometricStatus = BiometricStatus.TECHNICAL_ERROR

    val rewardsDetails: ExpressRewardsDetails = ExpressRewardsDetails()
}

class ExpressRewardsDetails : BaseModel {
    val rewardsMembershipNumber = ""
    var rewardsAccountDetailSet = false
    var rewardsAccountBalanceSet = false
    val rewardsTranHistorySet = false
    val accountTypeDescription = ""
    val accountName = ""
}