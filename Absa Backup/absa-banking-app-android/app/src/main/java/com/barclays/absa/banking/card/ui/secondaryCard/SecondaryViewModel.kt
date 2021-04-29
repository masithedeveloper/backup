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
package com.barclays.absa.banking.card.ui.secondaryCard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.card.services.card.CardInteractor
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.GetSecondaryCardMandateResponse
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.SecondaryCards
import com.barclays.absa.banking.express.secondaryCard.updateSecondaryCard.dto.SecondaryCardsRequest

class SecondaryViewModel : ViewModel() {
    var originalSecondaryCardList: List<SecondaryCards> = listOf()
    var secondaryCardMandates: GetSecondaryCardMandateResponse = GetSecondaryCardMandateResponse()
    val cardService by lazy { CardInteractor() }
    var updateSecondaryCardMandateSureCheckResponse = MutableLiveData<SureCheckResponse>()

    fun buildSecondaryCardSummaryList(): ArrayList<SecondaryCards> {
        val secondaryCardsRequest = arrayListOf<SecondaryCards>()
        secondaryCardMandates.secondaryCardMandateDetailsList.forEachIndexed { index, secondaryCard ->
            val item = originalSecondaryCardList[index]
            if (item.additionalTenantMandate != secondaryCard.additionalTenantMandate) {
                secondaryCardsRequest.add(secondaryCard)
            }
        }
        return secondaryCardsRequest
    }

    fun buildSecondaryCardRequest(): ArrayList<SecondaryCardsRequest> {
        val finalList = arrayListOf<SecondaryCardsRequest>()
        secondaryCardMandates.secondaryCardMandateDetailsList.forEach {
            val item = SecondaryCardsRequest()
            item.additionalPlasticNumber = it.additionalPlasticNumber
            item.additionalTenantMandate = it.additionalTenantMandate
            finalList.add(item)
        }
        return finalList
    }

    fun buildSureCheckSecondaryCardRequest(): ArrayList<SecondaryCardExpressRequest> {
        val finalList = arrayListOf<SecondaryCardExpressRequest>()
        secondaryCardMandates.secondaryCardMandateDetailsList.forEachIndexed { index, it ->
            val item = SecondaryCardExpressRequest()
            item.apply {
                additionalEmbossName = it.additionalEmbossName
                additionalPlastic = it.additionalPlasticNumber
                additionalTenantInd = it.additionalTenantInd
                additionalTenantMandate = it.additionalTenantMandate
                primaryPlastic = secondaryCardMandates.primaryPlastic
            }
            if (it.additionalTenantMandate != originalSecondaryCardList[index].additionalTenantMandate) {
                finalList.add(item)
            }
        }
        return finalList
    }
}