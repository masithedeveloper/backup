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

package com.barclays.absa.banking.payments.international

class InternationalPaymentsConstants {
    companion object {
        const val COUNTRY_DESCRIPTION = "countryDescription"
        const val COUNTRY_CODE = "countryCode"
        const val SHOULD_DISPLAY_SECURITY_QUESTION = "shouldDisplaySecurityQuestion"
        const val SELECTED_STATE = "selectedState"
        const val COUNTRY_SELECTED = 6007
        const val STATE_SELECTED = 6008
        const val CITY_SELECTED = 6009
        const val INTERNATIONAL_COUNTRY_LIST = "internationalCountryList"
        const val INTERNATIONAL_STATE_LIST = "internationalStateList"
        const val INTERNATIONAL_CITY_LIST = "internationalCityList"
        const val SELECTED_CITY = "selectedCity"
        const val ENTERED_CITY = "enteredCity"
        const val QUOTATION_DETAILS = "quotationDetails"
        const val NON_RESIDENT_OTHER = "NON RESIDENT OTHER"
        const val RES_ACCOUNT_ABROAD = "RES ACCOUNT ABROAD"
        const val NON_SA_RESIDENT = "Non SA resident"
        const val MAXIMUM_ZAR_AMOUNT = "30000"
        const val MINIMUM_USD_AMOUNT = "10"
        const val D0219_ERROR = "D0219"
        const val AMOUNT_TOO_LARGE = "AMOUNT TOO LARGE"
        const val INTERNATIONAL_PAYMENTS = "International Payments"
        const val BENEFICIARY_DETAILS = "beneficiaryDetails"
        const val IS_SECURITY_QUESTION_REQUIRED = "isSecurityQuestionRequired"
        const val IS_CITY_SELECTED = "isCitySelected"
        const val EXCEEDS = "Total Amount exceeds"
        const val FICA = "Switching/General/Validation/FicaLockup"
        const val D0012 = "D0012"
        const val CURRENCY_NOT_SUPPORTED = "T5504"
        const val JAVA_ERROR = "java"
        const val CALLING_FRAGMENT = "callingFragment"
        const val INTERNATIONAL_PAYMENT = "internationalPayments"
        const val D0204 = "D0204"
        const val AMOUNT_EXCEEDS_AVAILABLE = "Expected amount exceeds available balance."
        const val D0208 = "D0208"
    }
}