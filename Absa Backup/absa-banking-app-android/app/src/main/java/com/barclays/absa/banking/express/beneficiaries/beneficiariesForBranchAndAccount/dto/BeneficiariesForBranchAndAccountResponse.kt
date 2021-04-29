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

package com.barclays.absa.banking.express.beneficiaries.beneficiariesForBranchAndAccount.dto

import com.barclays.absa.banking.express.beneficiaries.dto.OnceOffBeneficiary
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.express.beneficiaries.dto.TypeOfBeneficiaryList
import com.barclays.absa.banking.express.payments.absaListedBeneficiaries.dto.AbsaListedBeneficiary
import za.co.absa.networking.dto.BaseResponse

class BeneficiariesForBranchAndAccountResponse : BaseResponse() {
    var institutionalBeneficiaries: List<AbsaListedBeneficiary> = listOf()
    var ownDefinedBeneficiaries: List<RegularBeneficiary> = listOf()
    var onceOffBeneficiaries: List<OnceOffBeneficiary> = listOf()
    var typeOfBeneficiaryList: TypeOfBeneficiaryList = TypeOfBeneficiaryList.None
}