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

package com.barclays.absa.banking.expressCashSend.ui

import android.os.Parcelable
import com.barclays.absa.banking.boundary.model.TransactionObject
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class BeneficiaryDetail(
        val name: String,
        val surname: String,
        val accountNumber: String,
        val reference: String,
        val hasImage: Boolean,
        val imageName: String,
        val transactions: ArrayList<TransactionObject> = ArrayList()
) : Parcelable