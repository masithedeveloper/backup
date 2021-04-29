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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.framework.data.ResponseObject

class ResidingCountry : ResponseObject() {

    var cmacountryIndicator: String? = ""
    var cityName: String? = ""
    var countryDescription: String? = ""
    var stateName: String? = ""
    var displayQuestionsAndAnswers: String? = ""
    var countryCode: String? = ""
    var isoCountryCode: String? = ""

    override fun toString(): String {
        return "(cmacountryIndicator=$cmacountryIndicator, cityName=$cityName, countryDescription=$countryDescription, stateName=$stateName, displayQuestionsAndAnswers=$displayQuestionsAndAnswers, countryCode=$countryCode, isoCountryCode=$isoCountryCode)"
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true

        return if (other is ResidingCountry) {
            true and (cmacountryIndicator == other.cmacountryIndicator)
                    .and(cityName == other.cityName)
                    .and(countryDescription == other.countryDescription)
                    .and(stateName == other.stateName)
                    .and(displayQuestionsAndAnswers == other.displayQuestionsAndAnswers)
                    .and(countryCode == other.countryCode)
                    .and(isoCountryCode == other.isoCountryCode)

        } else false
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        result = result * PRIME + if (cmacountryIndicator == null) 0 else cmacountryIndicator.hashCode()
        result = result * PRIME + if (cityName == null) 0 else cityName.hashCode()
        result = result * PRIME + if (countryDescription == null) 0 else countryDescription.hashCode()
        result = result * PRIME + if (stateName == null) 0 else stateName.hashCode()
        result = result * PRIME + if (displayQuestionsAndAnswers == null) 0 else displayQuestionsAndAnswers.hashCode()
        result = result * PRIME + if (countryCode == null) 0 else countryCode.hashCode()
        result = result * PRIME + if (isoCountryCode == null) 0 else isoCountryCode.hashCode()
        return result
    }
}
