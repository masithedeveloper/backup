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
 */

package com.barclays.absa.banking.linking.ui

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.deviceLinking.ui.CreateNicknameActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import styleguide.screens.GenericResultScreenFragment

class LinkingSureCheckHandler(val baseFragment: BaseFragment, private val linkingViewModel: LinkingViewModel) {
    private val appCacheService: IAppCacheService = getServiceInterface()

    companion object {
        private const val INCORRECT_CREDENTIALS = "Incorrect Credentials"
    }

    private var sureCheckDelegate: SureCheckDelegate = object : SureCheckDelegate(baseFragment.baseActivity) {
        override fun onSureCheckProcessed() {
            baseFragment.startActivity(Intent(baseFragment.activity, CreateNicknameActivity::class.java))
        }

        override fun onSureCheckCancelled() {
            super.onSureCheckCancelled()

            val genericResultScreenProperties = LinkingResultFactory(baseFragment).showFailureScreenFragment(R.string.surecheck_two_cancelled, R.string.surecheck_cancelled_description, R.string.done) {
                baseFragment.baseActivity.finish()
            }
            baseFragment.findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
        }

        override fun onSureCheckRejected() {
            super.onSureCheckRejected()

            val genericResultScreenProperties = LinkingResultFactory(baseFragment).showFailureScreenFragment(R.string.transaction_rejected, -1, R.string.done) {
                baseFragment.baseActivity.finish()
            }
            baseFragment.findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
        }
    }

    fun requestSureCheckForLinking(profileIndex: Int) {
        if (profileIndex != -1) {
            with(linkingViewModel.linkedProfilesList[profileIndex]) {
                appCacheService.setSelectedProfileToLink(linkingViewModel.linkedProfilesList[profileIndex])
                linkingViewModel.linkingTransactionDetails.accountNumber = accessAccount
                linkingViewModel.linkingTransactionDetails.userNumber = userNumber.toString()
            }

            when (linkingViewModel.linkedProfilesList[profileIndex].status) {
                LinkingAccountStatus.ACTIVE.stateCode -> {
                    linkingViewModel.performAccountLinking()
                }
                LinkingAccountStatus.UNPAID.stateCode, LinkingAccountStatus.CLOSED.stateCode, LinkingAccountStatus.SUSPENDED.stateCode -> {
                    val buttonString = if (linkingViewModel.linkedProfilesList.size == 1) R.string.done else R.string.linking_try_another_profile_button
                    val genericResultScreenProperties = LinkingResultFactory(baseFragment).showFailureScreenFragment(R.string.linking_unable_to_link_account, R.string.linking_account_closed_error, buttonString) {
                        if (linkingViewModel.linkedProfilesList.size == 1) {
                            baseFragment.baseActivity.finish()
                        } else {
                            (baseFragment as LinkingBaseFragment).linkingActivity.superOnBackPressed()
                        }
                    }
                    baseFragment.findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
                }
            }
        } else {

            with(appCacheService.getSelectedProfileToLink()) {
                linkingViewModel.linkingTransactionDetails.accountNumber = accessAccount
                linkingViewModel.linkingTransactionDetails.userNumber = userNumber.toString()
            }
            linkingViewModel.performAccountLinking()
        }

        linkingViewModel.sureCheckResult.observe(baseFragment, {
            with(appCacheService) {
                setSureCheckNotificationMethod(it.notificationMethod)
                setSureCheckReferenceNumber(it.referenceNumber)
                if (it.cellnumber.isNotBlank()) {
                    setSureCheckCellphoneNumber(it.cellnumber)
                }
                if (it.email.isNotBlank()) {
                    setSureCheckEmail(it.email)
                }
            }

            appCacheService.setOriginalSureCheckType(it.transactionVerificationType)

            when {
                INCORRECT_CREDENTIALS.equals(it.txnMessage, ignoreCase = true) -> {
                    val genericResultScreenProperties = LinkingResultFactory(baseFragment).showSureCheckPinRevokedResultScreen()
                    baseFragment.findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
                }
                BMBConstants.FAILURE.equals(it.txnStatus, ignoreCase = true) -> {
                    val genericResultScreenProperties = LinkingResultFactory(baseFragment).sureCheckFailed(it.txnMessage)
                    baseFragment.findNavController().navigate(R.id.genericResultFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
                }
                it.transactionVerificationType.isNotEmpty() -> {
                    when (val verificationType = TransactionVerificationType.valueOf(it.transactionVerificationType)) {
                        TransactionVerificationType.SURECHECKV1, TransactionVerificationType.SURECHECKV2 -> goToCountDownTimerScreen(verificationType)
                        TransactionVerificationType.NotNeeded -> baseFragment.startActivity(Intent(baseFragment.activity, CreateNicknameActivity::class.java))
                        TransactionVerificationType.SURECHECKV1_FALLBACK -> sureCheckDelegate.initiateTransactionVerificationEntryScreen()
                        TransactionVerificationType.SURECHECKV2_FALLBACK -> sureCheckDelegate.initiateOfflineOtpScreen()
                    }
                }
            }
        })
    }

    private fun goToCountDownTimerScreen(verificationType: TransactionVerificationType) {
        if (verificationType == TransactionVerificationType.SURECHECKV1 || verificationType == TransactionVerificationType.SURECHECKV1Required) {
            sureCheckDelegate.initiateV1CountDownScreen()
        } else {
            sureCheckDelegate.initiateV2CountDownScreen()
        }
    }
}