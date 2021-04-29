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
package com.barclays.absa.banking.shared

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.AlertBox
import com.barclays.absa.banking.shared.AlertDialogProperties.Builder

object BaseAlertDialog {

    private val context: Context
        get() = BMBApplication.getInstance().baseContext

    lateinit var alertDialog: AlertDialog

    fun showErrorAlertDialog(errorMessage: String?) {
        showAlertDialog(Builder()
                .title(context.getString(R.string.error))
                .message(errorMessage)
                .build())
    }

    fun showErrorAlertDialog(errorMessage: String?, positiveOnClickListener: DialogInterface.OnClickListener) {
        showAlertDialog(Builder()
                .title(context.getString(R.string.error))
                .message(errorMessage)
                .positiveDismissListener(positiveOnClickListener)
                .build())
    }

    fun showRetryErrorDialog(message: String?, alertRetryListener: AlertBox.AlertRetryListener) {
        showAlertDialog(Builder()
                .title(context.getString(R.string.error))
                .message(message)
                .negativeButton(context.getString(R.string.cancel))
                .positiveButton(context.getString(R.string.retry))
                .positiveDismissListener { _, _ ->
                    alertRetryListener.retry()
                    dismissAlertDialog()
                }.build())
    }

    fun showYesNoDialog(alertDialogProperties: Builder) {
        showAlertDialog(alertDialogProperties
                .positiveButton(context.getString(R.string.yes))
                .negativeButton(context.getString(R.string.no))
                .build())
    }

    fun showGenericErrorDialog() {
        showErrorAlertDialog(context.getString(R.string.generic_error))
    }

    fun showGenericErrorDialog(positiveOnClickListener: DialogInterface.OnClickListener) {
        showErrorAlertDialog(context.getString(R.string.generic_error), positiveOnClickListener)
    }

    fun showRequestAccessAlertDialog(featureName: String) {
        showAlertDialog(Builder()
                .title(context.getString(R.string.request_access))
                .message(context.getString(R.string.request_access_message))
                .positiveButton(context.getString(R.string.yes))
                .positiveDismissListener { _, _ ->
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.grant_operator_access_message_first) + featureName + context.getString(R.string.grant_operator_access_message_second) + CustomerProfileObject.instance.customerName)
                    }

                    val chooser = Intent.createChooser(shareIntent, context.getString(R.string.request_access))
                    chooser.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    BMBApplication.getInstance().topMostActivity.startActivity(chooser)
                    AnalyticsUtils.getInstance().trackAirDropShare()
                }
                .negativeButton(context.getString(R.string.no_thanks))
                .build())
    }

    fun isDialogShowing(): Boolean {
        return ::alertDialog.isInitialized && alertDialog.isShowing
    }

    fun dismissAlertDialog() {
        if (::alertDialog.isInitialized && alertDialog.isShowing && !BMBApplication.getInstance().topMostActivity.isFinishing) {
            alertDialog.dismiss()
        }
    }

    fun showAlertDialog(alertDialogProperties: AlertDialogProperties) {
        if (!BMBApplication.getInstance().topMostActivity.isFinishing) {
            alertDialog = AlertDialog.Builder(BMBApplication.getInstance().topMostActivity, R.style.MyDialogTheme).create().apply {
                if (this.isShowing) {
                    this.dismiss()
                }
                setTitle(alertDialogProperties.title)
                setMessage(alertDialogProperties.message)
                setCancelable(false)
                alertDialogProperties.editText?.let {
                    setView(it)
                }
                val positiveButtonText = if (alertDialogProperties.positiveButtonText.isNotEmpty()) {
                    alertDialogProperties.positiveButtonText
                } else {
                    BaseAlertDialog.context.getString(R.string.ok)
                }
                setButton(Dialog.BUTTON_POSITIVE, positiveButtonText, alertDialogProperties.positiveDismissListener
                        ?: DialogInterface.OnClickListener { _, _ -> dismissAlertDialog() })

                if (alertDialogProperties.negativeButtonText.isNotEmpty()) {
                    setButton(Dialog.BUTTON_NEGATIVE, alertDialogProperties.negativeButtonText, alertDialogProperties.negativeDismissListener
                            ?: DialogInterface.OnClickListener { _, _ -> dismissAlertDialog() })
                }
                show()
            }
        }
    }
}