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
 */

package com.barclays.absa.banking.manage.profile

enum class ManageProfileFlow {
    UPDATE_FINANCIAL_DETAILS,
    UPDATE_ADDRESS_DETAILS,
    FOREIGN_TAX,
    LOCAL_TAX,
    MISSING_MARKET_CONSENT
}

enum class ManageProfileDocUploadType {
    PROOF_OF_RESIDENCE,
    ID_DOCUMENT
}

enum class ManageProfileEducationDetailsFlow {
    EDUCATION,
    EMPLOYMENT,
    EMPLOYER
}

enum class ManageProfileFinancialDetailsFlow {
    UPDATE_FOREIGN_TAX,
    UPDATE_LOCAL_TAX,
    UPDATE_OTHER_FINANCIAL_DETAILS,
    UPDATE_INCOME_DETAILS
}