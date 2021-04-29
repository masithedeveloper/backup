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
package com.barclays.absa.banking.riskBasedApproach.services.dto

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import java.io.Serializable

class PersonalInformationResponse : TransactionResponse() {

    var customerInformation: CustomerInformation? = null

    class CustomerInformation : Serializable {
        var firstName: String? = ""
        var initials: String? = ""
        var lastName: String? = ""
        var dateOfBirth: String? = ""
        var gender: String? = ""
        var identityNo: String? = ""
        var identityType: String? = ""
        var clientType: String = ""
        var postalAddress: PostalAddress? = null
        var residentialAddress: PostalAddress? = null
        var cellNumber: String? = ""
        var email: String? = ""
        var sourceOfIncome: String? = ""
        var employmentInformation = EmploymentInformation()
        var preferredContactMethod: String? = ""
    }

    class PostalAddress : Serializable {
        var addressLine1: String? = ""
        var addressLine2: String? = ""
        var suburbRsa: String? = ""
        var town: String? = ""
        var postalCode: String? = ""
        var country: String? = ""
    }

    fun getMailMarketing(): PostalAddress? {
        return customerInformation?.postalAddress
    }

    fun getSmsMarketing(): String? {
        return customerInformation?.cellNumber
    }

    fun getEmailMarketing(): String? {
        return customerInformation?.email
    }
}
