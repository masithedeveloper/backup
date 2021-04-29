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
package com.barclays.absa.banking.presentation.contactUs

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.utils.EmailUtil
import com.barclays.absa.banking.framework.utils.TelephoneUtil

class ContactUsActivity : BaseActivity(R.layout.contact_us_activity), ContactUsContracts.ContactUsView {

    private lateinit var contactUsFragment: ContactUsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(R.string.contact_us_heading)
        initView()
    }

    override fun initView() {
        contactUsFragment = ContactUsFragment()
        startFragment(contactUsFragment, R.id.fragmentContainer,
                true, AnimationType.NONE, false, ContactUsActivity::class.java.simpleName)
    }

    override fun emailAppSupportOrGeneralEnquiry(emailAddress: String) = EmailUtil.email(this, emailAddress)

    override fun call(phoneNumber: String) = TelephoneUtil.call(this, phoneNumber)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        contactUsFragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}