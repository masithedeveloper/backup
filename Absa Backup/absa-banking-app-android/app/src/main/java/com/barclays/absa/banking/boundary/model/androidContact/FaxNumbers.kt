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

data class FaxNumbers(var homeFax: String? = "",
                      var workFax: String? = "") {

    val faxNumberPairList: List<Pair<String, String>>
        get() {
            val context = BMBApplication.getInstance()
            val contactList = ArrayList<Pair<String, String>>()
            if (!homeFax.isNullOrEmpty()) {
                contactList.add(Pair<String, String>(context.getString(R.string.home), homeFax))
            }
            if (!workFax.isNullOrEmpty()) {
                contactList.add(Pair<String, String>(context.getString(R.string.work_label), workFax))
            }
            return contactList
        }
}
