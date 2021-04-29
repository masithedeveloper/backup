/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.paymentsRewrite.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.beneficiaries.addRegularBeneficiary.AddRegularBeneficiaryViewModel
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryAccountType
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryNotification
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryNotificationMethod
import com.barclays.absa.banking.express.beneficiaries.dto.TypeOfBeneficiaryList
import com.barclays.absa.banking.express.beneficiaries.enquireRegularBeneficiary.EnquireBeneficiaryDetailsViewModel
import com.barclays.absa.banking.express.payments.absaListedBeneficiaries.PaymentsAbsaListedBeneficiariesViewModel
import com.barclays.absa.banking.express.payments.listBankNames.PaymentsListBankNamesViewModel
import com.barclays.absa.banking.express.payments.listBranchCodesForBank.PaymentsListBranchCodesForBankViewModel
import com.barclays.absa.banking.express.payments.payBeneficiary.PayBeneficiaryViewModel
import com.barclays.absa.banking.express.payments.payOnceOffBeneficiary.PayOnceOffBeneficiaryViewModel
import com.barclays.absa.banking.express.payments.validateOnceOffPayment.ValidateOnceOffPaymentViewModel
import com.barclays.absa.banking.express.payments.validatePayment.ValidatePaymentViewModel
import com.barclays.absa.banking.framework.BaseFragment
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorInterface
import styleguide.forms.notificationmethodview.NotificationMethodData
import styleguide.utils.extensions.toFormattedCellphoneNumber

open class PaymentsBaseFragment(@LayoutRes layout: Int) : BaseFragment(layout) {
    val paymentsViewModel by activityViewModels<PaymentsViewModel>()
    val validateBeneficiaryPaymentViewModel by activityViewModels<ValidatePaymentViewModel>()
    val validateOnceOffPaymentViewModel by activityViewModels<ValidateOnceOffPaymentViewModel>()
    val payBeneficiaryViewModel by activityViewModels<PayBeneficiaryViewModel>()
    val payOnceOffBeneficiaryViewModel by activityViewModels<PayOnceOffBeneficiaryViewModel>()
    val listBankNamesViewModel by activityViewModels<PaymentsListBankNamesViewModel>()
    val listBranchCodesForBankViewModel by activityViewModels<PaymentsListBranchCodesForBankViewModel>()
    val absaListedBeneficiariesViewModel by activityViewModels<PaymentsAbsaListedBeneficiariesViewModel>()
    val addRegularBeneficiaryViewModel by activityViewModels<AddRegularBeneficiaryViewModel>()
    val enquireBeneficiaryDetailsViewModel by activityViewModels<EnquireBeneficiaryDetailsViewModel>()

    val paymentsActivity: PaymentsActivity
        get() = baseActivity as PaymentsActivity

    fun getNotificationMethodDetails(beneficiaryNotification: BeneficiaryNotification): String {
        val notificationDetail = when (beneficiaryNotification.notificationMethod) {
            BeneficiaryNotificationMethod.SMS -> beneficiaryNotification.cellphoneNumber.toFormattedCellphoneNumber()
            BeneficiaryNotificationMethod.FAX -> "${beneficiaryNotification.faxCode}${beneficiaryNotification.faxNumber}".toFormattedCellphoneNumber()
            BeneficiaryNotificationMethod.EMAIL -> beneficiaryNotification.emailAddress
            BeneficiaryNotificationMethod.PUSH -> ""
            BeneficiaryNotificationMethod.NONE -> getString(R.string.none)
        }

        return if (notificationDetail.isEmpty()) {
            beneficiaryNotification.notificationMethod = BeneficiaryNotificationMethod.NONE
            getString(R.string.none)
        } else {
            notificationDetail
        }
    }

    fun getAccountTypeString(accountType: BeneficiaryAccountType): String = when (accountType) {
        BeneficiaryAccountType.CURRENT_ACCOUNT -> getString(R.string.current_account)
        BeneficiaryAccountType.SAVINGS_ACCOUNT -> getString(R.string.savings_account)
        BeneficiaryAccountType.TRANSMISSION_ACCOUNT -> getString(R.string.transmission_account)
        BeneficiaryAccountType.HOME_LOAN -> getString(R.string.home_loan)
        BeneficiaryAccountType.ONLINE_SHARE_TRADING -> getString(R.string.online_share_sharing)
        BeneficiaryAccountType.NONE -> getString(R.string.none)
    }

    fun updateNotificationMethodDetails(notification: BeneficiaryNotification, notificationMethodData: NotificationMethodData, notificationView: NormalInputView<SelectorInterface>) {
        val ordinal = NotificationMethodData.TYPE.valueOf(notificationMethodData.notificationMethodType.name).ordinal
        notification.notificationMethod = BeneficiaryNotificationMethod.values()[ordinal]

        when (notification.notificationMethod) {
            BeneficiaryNotificationMethod.SMS -> {
                notification.cellphoneNumber = notificationMethodData.notificationMethodDetail
                notificationView.text = notificationMethodData.notificationMethodDetail.toFormattedCellphoneNumber()
            }
            BeneficiaryNotificationMethod.FAX -> {
                notification.faxCode = notificationMethodData.notificationMethodDetail.take(3)
                notification.faxNumber = notificationMethodData.notificationMethodDetail.drop(3)
                notificationView.text = notificationMethodData.notificationMethodDetail.toFormattedCellphoneNumber()
            }
            BeneficiaryNotificationMethod.EMAIL -> {
                notification.emailAddress = notificationMethodData.notificationMethodDetail
                notificationView.text = notificationMethodData.notificationMethodDetail
            }
            BeneficiaryNotificationMethod.PUSH -> {
            }
            BeneficiaryNotificationMethod.NONE -> notificationView.text = getString(R.string.none)
        }
    }

    fun popBackToPaymentsHub() {
        popBackTo(R.id.paymentHubFragment)
    }

    internal fun handleNavigationForBranchAndAccount(typeOfBeneficiaryList: TypeOfBeneficiaryList) {
        val isOnceOff = paymentsViewModel.isOnceOffPayment && (typeOfBeneficiaryList == TypeOfBeneficiaryList.OnceOffPayment || typeOfBeneficiaryList == TypeOfBeneficiaryList.InstitutionalAndOnceOffPaymentBeneficiary)
        when {
            typeOfBeneficiaryList == TypeOfBeneficiaryList.None -> navigate(PaymentTabsFragmentDirections.actionPaymentTabsFragmentToBeneficiaryDetailsConfirmationFragment())
            isOnceOff -> navigate(PaymentTabsFragmentDirections.actionPaymentTabsFragmentToOnceOffAlreadyExistsFragment())
            else -> navigate(PaymentTabsFragmentDirections.actionPaymentTabsFragmentToBeneficiaryAlreadyExistsFragment())
        }
    }
}