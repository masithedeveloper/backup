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

package com.barclays.absa.banking.manage.profile.ui.widgets

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class AddressDetails(
        var addressLineOne: String = "",
        var addressLineTwo: String = "",
        var suburb: String = "",
        var town: String = "",
        var postalCode: String = "",
        var country: String = "",
        var employerName: String = "",
        var employerTelephoneNumber: String = "",
        var employerFaxNumber: String = "") : Parcelable
