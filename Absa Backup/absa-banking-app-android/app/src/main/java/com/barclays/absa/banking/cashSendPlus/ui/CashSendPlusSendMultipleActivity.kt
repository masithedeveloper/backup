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
package com.barclays.absa.banking.cashSendPlus.ui

import android.content.DialogInterface
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog

class CashSendPlusSendMultipleActivity : BaseActivity(R.layout.cash_send_plus_send_multiple_activity) {

    companion object {
        const val ACCESS_PIN_LENGTH = 6
        const val MAX_ALLOWED_BENEFICIARIES = 10
        const val MIN_AMOUNT_TO_SEND = 20
        const val MAX_AMOUNT_TO_SEND = 3000
    }

    fun showCancelCashSendPlusSendMultipleDialog() {
        AlertDialogProperties.Builder().apply {
            title = getString(R.string.cash_send_plus_cancel_multiple)
            message = getString(R.string.cash_send_plus_are_you_sure_you_want_to_cancel_send_multiple)
            positiveDismissListener = DialogInterface.OnClickListener { _, _ -> finish() }
            negativeDismissListener = DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }
            BaseAlertDialog.showYesNoDialog(this)
        }
    }

    fun showCashSendPlusExceededAmountDialog(exceededAmount: String) {
        AlertDialogProperties.Builder().apply {
            title = getString(R.string.cash_send_plus_amount_exceeds_limit_title)
            message = getString(R.string.cash_send_plus_amount_exceeds_limit_message, exceededAmount)
            negativeButton = getString(R.string.cash_send_plus_go_back)
            positiveButton = getString(R.string.cancel)
            negativeDismissListener = DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }
            positiveDismissListener = DialogInterface.OnClickListener { _, _ -> showCancelCashSendPlusSendMultipleDialog() }
            BaseAlertDialog.showAlertDialog(build())
        }
    }

    fun showCashSendPlusRemoveBeneficiaryDialog(beneficiaryName: String?, beneficiarySurname: String?, callback: () -> Unit) {
        AlertDialogProperties.Builder().apply {
            title = getString(R.string.cash_send_plus_remove_beneficiary)
            message = getString(R.string.cash_send_plus_absa_will_remove_recipient_confirmation, beneficiaryName, beneficiarySurname)
            positiveButton = getString(R.string.remove)
            negativeButton = getString(R.string.cancel)
            positiveDismissListener = DialogInterface.OnClickListener { _, _ -> callback() }
            negativeDismissListener = DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }
            BaseAlertDialog.showAlertDialog(build())
        }
    }

    fun showPinNotSharedDialog() {
        AlertDialogProperties.Builder().apply {
            title = getString(R.string.cash_send_plus_atm_pin_not_shared)
            message = getString(R.string.cash_send_plus_proceed_without_sharing_pin)
            positiveButton = getString(R.string.cash_send_plus_proceed)
            negativeButton = getString(R.string.cancel)
            positiveDismissListener = DialogInterface.OnClickListener { _, _ -> finish() }
            negativeDismissListener = DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }
            BaseAlertDialog.showAlertDialog(build())
        }
    }
}