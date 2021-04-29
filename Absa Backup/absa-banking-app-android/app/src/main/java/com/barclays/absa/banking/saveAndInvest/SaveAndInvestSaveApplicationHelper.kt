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
 *
 */

package com.barclays.absa.banking.saveAndInvest

import com.barclays.absa.banking.express.invest.saveEmploymentDetails.dto.EmploymentDetails
import com.barclays.absa.banking.express.shared.getCustomerDetails.dto.CustomerCifInfo

object SaveAndInvestSaveApplicationHelper {

    fun buildEmploymentDetailsInfo(customerInfo: CustomerCifInfo): EmploymentDetails {
        return EmploymentDetails().apply {
            with(customerInfo.employmentInfo) {
                employmentSectorCode = occupationSector
                occupationLevelCode = occupationLevel
                employerName = nameOfEmployer
                employerPhysicalAddress = employerAddress.addressLine1
                employerPhysicalAddressLineTwo = employerAddress.addressLine2
                employerSuburb = employerAddress.suburbRsa
                employerCity = employerAddress.town
                employerPostalCode = employerAddress.postalCode
                this@apply.employedSince = employedSince
            }

            qualificationCode = customerInfo.clientDetails.qualificationCode
            taxDetails = customerInfo.taxDetails
        }
    }
}