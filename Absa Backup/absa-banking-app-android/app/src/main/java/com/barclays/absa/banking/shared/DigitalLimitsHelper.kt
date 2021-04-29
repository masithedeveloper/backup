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

package com.barclays.absa.banking.shared

import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.limits.DigitalLimit
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeConfirmationResult
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthDigitalLimitChangePendingActivity
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.shared.BaseAlertDialog.dismissAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.banking.shared.services.DigitalLimitsViewModel
import com.barclays.absa.utils.viewModel
import java.math.BigDecimal

class DigitalLimitsHelper {

    companion object {
        private lateinit var digitalLimitsViewModel: DigitalLimitsViewModel
        lateinit var digitalLimitState: MutableLiveData<DigitalLimitState>
        private var isTransfer = true

        private fun setUpViewModel(activity: BaseActivity) {
            digitalLimitsViewModel = activity.viewModel()
            resetMutableLiveData(activity)
            digitalLimitsViewModel.retrieveDigitalLimits()
        }

        private fun resetMutableLiveData(activity: BaseActivity) {
            digitalLimitState = MutableLiveData()
            digitalLimitState.removeObservers(activity)

            digitalLimitsViewModel.digitalLimit = MutableLiveData()
            digitalLimitsViewModel.digitalLimitsChangeResult = MutableLiveData()
            digitalLimitsViewModel.digitalLimitsChangeConfirmationResult = MutableLiveData()
            digitalLimitsViewModel.digitalLimit.removeObservers(activity)
            digitalLimitsViewModel.digitalLimitsChangeResult.removeObservers(activity)
            digitalLimitsViewModel.digitalLimitsChangeConfirmationResult.removeObservers(activity)

            digitalLimitsViewModel.failureResponse.removeObservers(activity)
            digitalLimitsViewModel.failureResponse = MutableLiveData()
            digitalLimitsViewModel.failureResponse.observe(activity, {
                digitalLimitState.value = DigitalLimitState.UNCHANGED
            })
        }

        fun checkPaymentAmount(activity: BaseActivity, amount: Amount, isFutureDate: Boolean) {
            isTransfer = false
            setUpViewModel(activity)

            digitalLimitsViewModel.digitalLimit.observe(activity, Observer { digitalLimit ->
                digitalLimit?.let {
                    val limitToUse = if (isFutureDate) it.futureDatedPaymentTransactionLimit else it.dailyPaymentLimit

                    if (isFutureDate) {
                        val compareTo = amount.amountDouble.compareTo(limitToUse.actualLimit.amountDouble)

                        if (compareTo > 0) {
                            limitToUse.actualLimit = Amount(ceilingBigDecimal(amount.amountValue).toString())
                            val title = activity.getString(R.string.future_dated_payment_limit)
                            val message = activity.getString(R.string.update_payment_limit_message) + "\n\n" + activity.getString(R.string.new_future_limit) + ": " + limitToUse.actualLimit.toString()
                            showDialog(activity, it, limitToUse.actualLimit.toString(), title, message)
                        } else {
                            digitalLimitState.value = DigitalLimitState.UNCHANGED
                        }
                    } else {
                        val compareTo = amount.amountDouble.compareTo(limitToUse.availableLimit.amountDouble)

                        if (compareTo > 0) {
                            val bigDecimal = ceilingBigDecimal(limitToUse.actualLimit.amountValue - limitToUse.availableLimit.amountValue + amount.amountValue)
                            limitToUse.actualLimit = Amount(bigDecimal.toString())
                            showDialog(activity, it, limitToUse.actualLimit.toString())
                        } else {
                            digitalLimitState.value = DigitalLimitState.UNCHANGED
                        }
                    }
                    return@Observer
                }
                digitalLimitState.value = DigitalLimitState.UNCHANGED
            })
        }

        private fun ceilingBigDecimal(bigDecimal: BigDecimal): BigDecimal {
            return bigDecimal.setScale(0, BigDecimal.ROUND_CEILING)
        }

        fun checkMultiplePaymentAmount(activity: BaseActivity, normalAmount: Amount, futureAmount: Amount) {
            isTransfer = false
            setUpViewModel(activity)

            digitalLimitsViewModel.digitalLimit.observe(activity, Observer { digitalLimit ->
                digitalLimit?.let {
                    val normalAmountDigitalLimit = digitalLimit.dailyPaymentLimit
                    val futureAmountDigitalLimit = digitalLimit.futureDatedPaymentTransactionLimit
                    val compareToNormal = normalAmount.amountDouble.compareTo(normalAmountDigitalLimit.availableLimit.amountDouble)
                    val compareToFuture = futureAmount.amountDouble.compareTo(futureAmountDigitalLimit.actualLimit.amountDouble)

                    if (compareToNormal > 0 || compareToFuture > 0) {
                        val title = activity.getString(R.string.update_payment_limit)
                        var message = activity.getString(R.string.update_payment_limit_message)

                        if (compareToNormal > 0) {
                            normalAmountDigitalLimit.actualLimit = Amount(ceilingBigDecimal(normalAmountDigitalLimit.actualLimit.amountValue - normalAmountDigitalLimit.availableLimit.amountValue + normalAmount.amountValue).toString())
                            message = message + "\n\n" + activity.getString(R.string.payment_daily_limit) + ": " + normalAmountDigitalLimit.actualLimit.toString()
                        }
                        if (compareToFuture > 0) {
                            futureAmountDigitalLimit.actualLimit = Amount(ceilingBigDecimal(futureAmount.amountValue).toString())
                            message = message + "\n\n" + activity.getString(R.string.future_dated_payment_limit) + ": " + futureAmountDigitalLimit.actualLimit.toString()
                        }
                        showDialog(activity, it, normalAmountDigitalLimit.actualLimit.toString(), title, message)
                    } else {
                        digitalLimitState.value = DigitalLimitState.UNCHANGED
                    }
                    return@Observer
                }
                digitalLimitState.value = DigitalLimitState.UNCHANGED
            })
        }

        fun checkTransferAmount(activity: BaseActivity, amount: Amount, customMessage: String = "") {
            isTransfer = true
            setUpViewModel(activity)
            digitalLimitsViewModel.digitalLimit.observe(activity, Observer { digitalLimit ->
                digitalLimit?.let {
                    val compareTo = amount.amountDouble.compareTo(it.dailyInterAccountTransferLimit.availableLimit.amountDouble)

                    if (compareTo > 0) {
                        it.dailyInterAccountTransferLimit.actualLimit = Amount(ceilingBigDecimal(it.dailyInterAccountTransferLimit.actualLimit.amountValue - it.dailyInterAccountTransferLimit.availableLimit.amountValue + amount.amountValue).toString())
                        showDialog(activity, it, it.dailyInterAccountTransferLimit.actualLimit.toString(), "", customMessage)
                    } else {
                        digitalLimitState.value = DigitalLimitState.UNCHANGED
                    }
                    return@Observer
                }
                digitalLimitState.value = DigitalLimitState.UNCHANGED
            })
        }

        private fun showDialog(activity: BaseActivity, digitalLimit: DigitalLimit, newLimit: String) {
            showDialog(activity, digitalLimit, newLimit, "", "")
        }

        private fun showDialog(activity: BaseActivity, digitalLimit: DigitalLimit, newLimit: String, customTitle: String, customMessage: String) {

            val digitalLimitsSureCheckDelegate = object : SureCheckDelegate(activity) {
                var canceled: Boolean = false

                override fun onSureCheckProcessed() {
                    if (!canceled) {
                        digitalLimitsViewModel.confirmDigitalLimitsChange(false)
                    }
                }

                override fun onSureCheckCancelled() {
                    super.onSureCheckCancelled(activity)
                    canceled = true
                }

                override fun onSureCheckFailed() {
                    showFailedResultScreen(activity, true)
                }
            }

            val title = if (customTitle.isNotEmpty()) {
                customTitle
            } else {
                if (isTransfer) activity.getString(R.string.fixed_deposit_update_daily_transfer_limit) else activity.getString(R.string.fixed_deposit_update_daily_payment_limit)
            }

            val message = if (customMessage.isNotEmpty()) {
                customMessage
            } else {
                if (isTransfer) activity.getString(R.string.fixed_deposit_update_daily_transfer_limit_message) + "\n\n" + activity.getString(R.string.new_limit) + ": " + newLimit else activity.getString(R.string.fixed_deposit_update_daily_payment_limit_message) + "\n\n" + activity.getString(R.string.new_limit) + ": " + newLimit
            }

            showAlertDialog(AlertDialogProperties.Builder()
                    .title(title)
                    .message(message)
                    .positiveButton(activity.getString(R.string.update))
                    .negativeButton(activity.getString(R.string.cancel))
                    .positiveDismissListener { _, _ ->
                        digitalLimitsViewModel.digitalLimitsChangeResult.observe(activity, {
                            digitalLimitsViewModel.confirmDigitalLimitsChange(true)
                        })

                        digitalLimitsViewModel.digitalLimitsChangeConfirmationResult.observe(activity, {
                            digitalLimitsSureCheckDelegate.processSureCheck(activity, it) { launchResultScreen(activity, it) }
                        })

                        digitalLimitsViewModel.changeDigitalLimits(digitalLimit.dailyPaymentLimit.actualLimit.getAmount(),
                                digitalLimit.dailyInterAccountTransferLimit.actualLimit.getAmount(), digitalLimit.recurringPaymentTransactionLimit.actualLimit.getAmount(), digitalLimit.futureDatedPaymentTransactionLimit.actualLimit.getAmount())
                    }.negativeDismissListener { _, _ ->
                        dismissAlertDialog()
                        digitalLimitState.value = DigitalLimitState.CANCELLED
                    }
                    .build())
        }

        private fun showFailedResultScreen(activity: BaseActivity, isFailureResult: Boolean) {
            GenericResultActivity.bottomOnClickListener = View.OnClickListener { activity.loadAccountsAndGoHome() }

            val intent = Intent(activity, GenericResultActivity::class.java)
            intent.putExtra(GenericResultActivity.IS_FAILURE, true)
            intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross)
            if (isFailureResult) {
                intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.surecheck_failed)
            } else {
                intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_rejected)
            }
            intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            activity.startActivity(intent)
        }

        private fun launchResultScreen(activity: BaseActivity, digitalLimitsChangeConfirmationResult: DigitalLimitsChangeConfirmationResult?) {
            if (digitalLimitsChangeConfirmationResult != null) {
                val successFailureMessage = digitalLimitsChangeConfirmationResult.successOrFailMsgValue
                val transactionStatus = digitalLimitsChangeConfirmationResult.transactionStatus

                if (!successFailureMessage.isNullOrEmpty() && BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION.equals(successFailureMessage, ignoreCase = true)) {
                    val authorisationIntent = Intent(activity, DualAuthDigitalLimitChangePendingActivity::class.java)
                    activity.startActivity(authorisationIntent)
                    digitalLimitState.value = DigitalLimitState.CANCELLED
                } else {
                    val intent: Intent = if (digitalLimitsChangeConfirmationResult.status != null && BMBConstants.FAILURE.equals(digitalLimitsChangeConfirmationResult.status, ignoreCase = true)) {
                        IntentFactory.getFailureResultScreen(activity, R.string.failureMsg, -1)
                    } else if (!transactionStatus.isNullOrEmpty() && BMBConstants.FAILURE.equals(transactionStatus, ignoreCase = true)) {
                        val intentBuilder = IntentFactoryGenericResult.getFailureResultBuilder(activity)
                                .setGenericResultHeaderMessage(activity.getString(R.string.system_error))
                                .setGenericResultSubMessage(digitalLimitsChangeConfirmationResult.oldTransactionMessage)
                        intentBuilder.build()
                    } else {
                        digitalLimitState.value = DigitalLimitState.CHANGED
                        return
                    }

                    digitalLimitState.value = DigitalLimitState.CANCELLED
                    intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done)
                    GenericResultActivity.bottomOnClickListener = View.OnClickListener { activity.loadAccountsAndGoHome() }
                    activity.startActivityIfAvailable(intent)
                }
            }
        }
    }
}
