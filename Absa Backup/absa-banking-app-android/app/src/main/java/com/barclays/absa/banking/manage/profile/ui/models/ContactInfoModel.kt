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

package com.barclays.absa.banking.manage.profile.ui.models

import com.barclays.absa.banking.shared.BaseModel

class ContactInfoModel : BaseModel {
    var clientType = ""
    var residentialAddressLine1 = ""
    var residentialAddressLine2 = ""
    var residentialSuburbRsa = ""
    var residentialTown = ""
    var residentialPostalCode = ""
    var residentialCountry = ""
    var postalAddressLine1 = ""
    var postalAddressLine2 = ""
    var postalSuburbRsa = ""
    var postalTown = ""
    var postalPostalCode = ""
    var preferredContactMethod = ""
    var homeTelephoneCode = ""
    var homeTelephoneNumber = ""
    var homeFaxCode = ""
    var homeFaxNumber = ""
    var cellNumber = ""
    var emailAddress = ""
}