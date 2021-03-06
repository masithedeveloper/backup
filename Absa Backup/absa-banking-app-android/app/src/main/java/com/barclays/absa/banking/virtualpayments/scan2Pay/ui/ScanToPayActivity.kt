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
package com.barclays.absa.banking.virtualpayments.scan2Pay.ui

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity

class ScanToPayActivity : BaseActivity(R.layout.activity_scan_to_pay) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.qr_payments_menu_item)
    }
}