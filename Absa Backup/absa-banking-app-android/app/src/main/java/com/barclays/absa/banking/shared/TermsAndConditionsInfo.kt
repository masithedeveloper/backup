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

package com.barclays.absa.banking.shared

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

private const val DEFAULT_APPLICATION_TYPE = "ALL"
private const val UNDEFINED_DESTINATION = -1

@Parcelize
class TermsAndConditionsInfo(var destination: Int = UNDEFINED_DESTINATION,
                             var shouldDisplayCheckBox: Boolean = true,
                             var applicationType: String = DEFAULT_APPLICATION_TYPE,
                             var productCode: String = "",
                             var productName: String = "",
                             var isDocFusionDocument: Boolean = false,
                             var cacheKey: String = "",
                             var url: String = "") : Parcelable