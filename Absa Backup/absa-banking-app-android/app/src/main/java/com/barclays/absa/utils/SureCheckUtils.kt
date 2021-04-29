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
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog

object SureCheckUtils {
    @JvmStatic
    fun isResponseSuccessSureCheck(model: ResponseObject?, context: Context): Boolean {
        if (model == null) {
            showAlertDialog(AlertDialogProperties.Builder()
                    .title(context.getString(R.string.alert))
                    .message(context.getString(R.string.generic_error))
                    .build())
            return false
        } else if (model.responseCode != AppConstants.RESPONSE_CODE_SUCCESS && model.responseCode != AppConstants.RESPONSE_CODE_PARTIAL_SUCCESS) {
            showAlertDialog(AlertDialogProperties.Builder()
                    .title(context.getString(R.string.alert))
                    .message(model.responseMessage)
                    .build())
            return false
        }
        return true
    }
}