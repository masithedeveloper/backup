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

import android.content.DialogInterface

import com.barclays.absa.banking.framework.BaseView

interface ContactUsContracts {

    interface ContactUsView : BaseView {
        fun initView()
        fun call(phoneNumber: String)
        fun emailAppSupportOrGeneralEnquiry(emailAddress: String)
    }

    interface ReportFraudView {
        fun isCallMeBackSessionAvailable(): Boolean
        fun navigateToContactUsFragment()
        fun navigateToReportFraudFragment()
        fun navigateToCallMeBackFragment(description: String?)
        fun requestCall()
        fun verifyCallBack(secretCode: String)
        fun showConfirmCallDialog()
        fun showMaxAttemptsDialog(attemptCount: Int, listener: DialogInterface.OnClickListener?)
        fun clearSecretCode()
        fun onBackPressed()
    }
}
