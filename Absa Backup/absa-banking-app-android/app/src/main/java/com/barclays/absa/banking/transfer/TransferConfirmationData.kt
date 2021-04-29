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

package com.barclays.absa.banking.transfer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class TransferConfirmationData(var amountToTransfer: String = "",
                               var fromAccountDescription: String = "",
                               var toAccountDescription: String = "",
                               var fromAccountReference: String = "",
                               var toAccountReference: String = "",
                               var fromAccountNumber: String = "",
                               var toAccountNumber: String = "",
                               var transactionDate: Date = Date(),
                               var useTime: Boolean = false,
                               var isFutureDatedTransfer: Boolean = false) : Parcelable