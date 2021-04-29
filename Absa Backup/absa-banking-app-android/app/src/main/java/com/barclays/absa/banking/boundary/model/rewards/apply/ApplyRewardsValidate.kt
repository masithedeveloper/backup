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
package com.barclays.absa.banking.boundary.model.rewards.apply

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class ApplyRewardsValidate : ResponseObject() {

    @JsonProperty("cellPhoneNumber")
    val cellPhoneNumber: String? = null
    @JsonProperty("emailAddress")
    val emailAddress: String? = null
    @JsonProperty("homeTel")
    val homeTelephone: String? = null
    @JsonProperty("workTel")
    val workTelephone: String? = null
    @JsonProperty("preferredLanguage")
    val preferredLanguage: String? = null
    @JsonProperty("addressLine1")
    val addressLine1: String? = null
    @JsonProperty("addressLine2")
    val addressLine2: String? = null
    @JsonProperty("suburb")
    val suburb: String? = null
    @JsonProperty("cityName")
    val city: String? = null
    @JsonProperty("postalCode")
    val postalCode: String? = null
    @JsonProperty("stmntDeliveryIndic")
    val statementDeliveryIndicator: String? = null
    @JsonProperty("recvMktngMaterial")
    val receivedMarketingMaterial: String? = null
    @JsonProperty("mktngMethod")
    val marketingMethod: String? = null
    @JsonProperty("monthlyFee")
    val monthlyFee: Amount? = null
    @JsonProperty("annualFee")
    val annualFee: Amount? = null
    @JsonProperty("chargeFreqID")
    val chargeFrequencyID: String? = null
    @JsonProperty("orderFreqDate")
    val orderFrequencyDate: String? = null
    @JsonProperty("fromAccount")
    val fromAccount: String? = null
    @JsonProperty("accountDesc")
    val accountDescription: String? = null
    @JsonProperty("workFaxNumber")
    val workFaxNumber: String? = null
    @JsonProperty("workFaxCode")
    val workFaxCode: String? = null
    @JsonProperty("homeFaxNumber")
    val homeFaxNumber: String? = null
    @JsonProperty("homeFaxCode")
    val homeFaxCode: String? = null
    @JsonProperty("custAddInfo")
    val customerAdditionalInformation: String? = null
    @JsonProperty("solicitIndic")
    val solicitIndicator: String? = null
    @JsonProperty("corresTitle")
    val correspondenceTitle: String? = null
    @JsonProperty("writnLangCode")
    val writtenLanguageCode: String? = null
    @JsonProperty("spoknLangCode")
    val spokenLanguageCode: String? = null
    @JsonProperty("writnLangName")
    val writtenLanguageName: String? = null
    @JsonProperty("spoknLangName")
    val spokenLanguageName: String? = null
    @JsonProperty("workNumber")
    val workNumber: String? = null
    @JsonProperty("homeNumber")
    val homeNumber: String? = null
    @JsonProperty("homeAreaCode")
    val homeAreaCode: String? = null
    @JsonProperty("workAreaCode")
    val workAreaCode: String? = null

}
