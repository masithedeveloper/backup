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
package com.barclays.absa.utils

import android.content.Context
import android.content.DialogInterface
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants.NEW_LINE
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog

object AppUpgradeUtils {

    fun showMinorOptionsDialog(context: Context, message: String, positiveButtonOnclickListener: DialogInterface.OnClickListener, negativeButtonOnclickListener: DialogInterface.OnClickListener) {
        showAlertDialog(AlertDialogProperties.Builder()
                .message(message + NEW_LINE)
                .title(context.getString(R.string.optional_update))
                .positiveButton(context.getString(R.string.later))
                .negativeButton(context.getString(R.string.update))
                .positiveDismissListener(positiveButtonOnclickListener)
                .negativeDismissListener(negativeButtonOnclickListener)
                .build())
    }

    fun showMajorOptionsDialog(context: Context, message: String, buttonOnclickListener: DialogInterface.OnClickListener) {
        showAlertDialog(AlertDialogProperties.Builder()
                .message(message + NEW_LINE)
                .positiveButton(context.getString(R.string.majorVersionUpdateBtnStr))
                .positiveDismissListener(buttonOnclickListener)
                .build())
    }
}