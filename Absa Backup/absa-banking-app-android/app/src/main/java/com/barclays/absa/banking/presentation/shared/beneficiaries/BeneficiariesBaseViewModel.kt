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
package com.barclays.absa.banking.presentation.shared.beneficiaries

import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import za.co.absa.networking.dto.BaseResponse

open class BeneficiariesBaseViewModel : BaseViewModel() {

    lateinit var beneficiaryList: List<RegularBeneficiary>
    lateinit var selectedBeneficiary: RegularBeneficiary

    var beneficiariesCached = false
    var beneficiaryAdded = false

    private val filteredBeneficiaryList by lazy { mutableListOf<RegularBeneficiary>() }

    companion object {
        const val NO_BENEFICIARIES_LINKED = "No beneficiaries linked to your portfolio."
    }

    fun hasBeneficiariesLinked(baseResponse: BaseResponse): Boolean = !baseResponse.header.resultMessages.any { it.responseMessage == NO_BENEFICIARIES_LINKED }

    fun filterBeneficiariesByQuery(beneficiaryList: List<RegularBeneficiary>, query: String): List<RegularBeneficiary> {
        if (beneficiaryList.isNotEmpty() && query.isNotEmpty()) {
            if (filteredBeneficiaryList.isNotEmpty()) {
                filteredBeneficiaryList.clear()
            }
            filteredBeneficiaryList.addAll(beneficiaryList.filter {
                it.beneficiaryName.trim().contains(query.trim(), true) || it.beneficiaryNumberNameAndAccount.trim().contains(query.trim(), true)
            })
        }
        return filteredBeneficiaryList
    }

    fun beneficiaryExists(): Boolean = beneficiaryList.any { it.targetAccountNumber == selectedBeneficiary.targetAccountNumber }

    fun beneficiarySuccessfullyAdded() {
        beneficiaryAdded = true
        beneficiariesCached = false
    }
}