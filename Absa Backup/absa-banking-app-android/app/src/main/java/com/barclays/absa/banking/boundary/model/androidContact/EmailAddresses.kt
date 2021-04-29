/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.boundary.model.androidContact

import android.util.Pair
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import java.util.*

data class EmailAddresses(var home: String? = "",
                          var mobile: String? = "",
                          var work: String? = "",
                          var other: String? = "",
                          var custom: String? = "") {

    val emailAddressPairList: List<Pair<String, String>>
        get() {
            val context = BMBApplication.getInstance()
            val contactList = ArrayList<Pair<String, String>>()
            if (!custom.isNullOrEmpty()) {
                contactList.add(Pair<String, String>(context.getString(R.string.custom_label), custom))
            }
            if (!mobile.isNullOrEmpty()) {
                contactList.add(Pair<String, String>(context.getString(R.string.mobile_label), mobile))
            }
            if (!home.isNullOrEmpty()) {
                contactList.add(Pair<String, String>(context.getString(R.string.home), home))
            }
            if (!work.isNullOrEmpty()) {
                contactList.add(Pair<String, String>(context.getString(R.string.work_label), work))
            }
            if (!other.isNullOrEmpty()) {
                contactList.add(Pair<String, String>(context.getString(R.string.other_label), other))
            }
            return contactList
        }
}