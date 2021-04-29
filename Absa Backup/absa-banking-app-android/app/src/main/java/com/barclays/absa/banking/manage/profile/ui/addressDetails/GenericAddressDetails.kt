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
 */

package com.barclays.absa.banking.manage.profile.ui.addressDetails

import android.os.Parcelable
import com.barclays.absa.banking.manage.profile.ui.widgets.AddressDetails
import com.barclays.absa.banking.manage.profile.ui.widgets.AddressFlowType
import kotlinx.android.parcel.Parcelize

@Parcelize
class GenericAddressDetails(var residentialAddress: AddressDetails? = null,
                            var genericAddress: AddressDetails? = null,
                            var flowType: AddressFlowType? = null) : Parcelable