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

import java.io.Serializable

class EmploymentInformation : Serializable {

    val highestLvlOfEducation : String = ""
    val postMetricInd : String = ""
    val occupationStatus : String = ""
    val occupation : String = ""
    val occupationLevel : String = ""
    val occupationSector : String = ""
    val sectorDescription : String = ""
    val doubleTaxCountry : String = ""
    val exmptionAgreementIndicator : String = ""
    val workTelephoneCode : String = ""
    val workNumber : String = ""
    val workTelephoneExtn : String = ""
    val faxWorkCode : String = ""
    val workFaxNumber : String = ""

    val employerAddressDTO = EmployerAddressDTO()
}