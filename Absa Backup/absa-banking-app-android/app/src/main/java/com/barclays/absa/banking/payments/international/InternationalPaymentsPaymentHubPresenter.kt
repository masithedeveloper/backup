/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.payments.international

import android.os.Handler
import android.os.Looper
import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.payments.international.data.InternationalPaymentBeneficiaryDetails
import com.barclays.absa.banking.payments.international.responseListeners.PendingTransactionExtendedResponseListener
import com.barclays.absa.banking.payments.international.responseListeners.ValidateForHolidaysAndTimeExtendedResponseListener
import com.barclays.absa.banking.payments.international.responseListeners.WesternUnionBeneficiaryDetailsExtendedResponseListener
import com.barclays.absa.banking.payments.international.responseListeners.WesternUnionBeneficiaryListExtendedResponseListener
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsInteractor
import com.barclays.absa.banking.payments.international.services.dto.OutBoundPendingTransactionResponse
import com.barclays.absa.banking.payments.international.services.dto.ValidateForHolidaysAndTimeResponse
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionBeneficiaryDetails
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionBeneficiaryListObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class InternationalPaymentsPaymentHubPresenter(globalView: InternationalPaymentsContract.WesternUnionPaymentHubView) : AbstractPresenter(WeakReference(globalView)), InternationalPaymentsContract.WesternUnionPaymentHubPresenter, InternationalPaymentsContract.UniversalInternationalPaymentsBeneficiary {

    private var internationalPaymentsInteractor: InternationalPaymentsInteractor = InternationalPaymentsInteractor()
    private val westernUnionBeneficiaryListExtendedResponseListener: WesternUnionBeneficiaryListExtendedResponseListener by lazy { WesternUnionBeneficiaryListExtendedResponseListener(this) }
    private val validateForHolidaysAndTimeExtendedResponseListener: ValidateForHolidaysAndTimeExtendedResponseListener by lazy { ValidateForHolidaysAndTimeExtendedResponseListener(this) }
    private val westernUnionBeneficiaryDetailsExtendedResponseListener: WesternUnionBeneficiaryDetailsExtendedResponseListener by lazy { WesternUnionBeneficiaryDetailsExtendedResponseListener(this) }
    private val pendingTransactionExtendedResponseListener: PendingTransactionExtendedResponseListener by lazy { PendingTransactionExtendedResponseListener(this) }
    private var beneficiaryDetails: InternationalPaymentBeneficiaryDetails = InternationalPaymentBeneficiaryDetails()
    private var isOnceOff: Boolean = false

    override fun viewInstantiated() {
        showProgressIndicator()
        internationalPaymentsInteractor.getWesternUnionBeneficiariesList(westernUnionBeneficiaryListExtendedResponseListener)
    }

    override fun beneficiaryServiceResponse(westernUnionBeneficiaryListObject: WesternUnionBeneficiaryListObject) {
        val beneficiaryList: ArrayList<InternationalPaymentBeneficiaryDetails> = ArrayList()
        val recentBeneficiaryList: ArrayList<InternationalPaymentBeneficiaryDetails> = ArrayList()
        val view = viewWeakReference.get() as InternationalPaymentsContract.WesternUnionPaymentHubView

        view.getLifecycleCoroutineScope().launch(Dispatchers.IO) {
            val westernUnionBeneficiaries = westernUnionBeneficiaryListObject.westernUnionBeneficiaryList
            if (westernUnionBeneficiaries != null && westernUnionBeneficiaries.isNotEmpty()) {
                for (westernUnionBeneficiaryDetails in westernUnionBeneficiaries) {
                    beneficiaryList.add(InternationalPaymentBeneficiaryDetails(westernUnionBeneficiaryDetails.lastPayment,
                            westernUnionBeneficiaryDetails.beneficiaryFirstName + " " + westernUnionBeneficiaryDetails.beneficiarySurname,
                            westernUnionBeneficiaryDetails.lastPayDate, westernUnionBeneficiaryDetails.id, westernUnionBeneficiaryDetails.transferType,
                            westernUnionBeneficiaryDetails.beneficiaryIFTType, westernUnionBeneficiaryDetails.status,
                            westernUnionBeneficiaryDetails.eftNumber, westernUnionBeneficiaryDetails.cifkey, westernUnionBeneficiaryDetails.tiebNumber))
                }
            }

            val recentBeneficiaries = westernUnionBeneficiaryListObject.recentlyPaidWesternUnionBeneficiaryList
            if (recentBeneficiaries != null && recentBeneficiaries.isNotEmpty()) {
                for (westernUnionBeneficiaryDetails in recentBeneficiaries) {
                    recentBeneficiaryList.add(InternationalPaymentBeneficiaryDetails(westernUnionBeneficiaryDetails.lastPayment,
                            westernUnionBeneficiaryDetails.beneficiaryFirstName + " " + westernUnionBeneficiaryDetails.beneficiarySurname,
                            westernUnionBeneficiaryDetails.lastPayDate, westernUnionBeneficiaryDetails.id, westernUnionBeneficiaryDetails.transferType,
                            westernUnionBeneficiaryDetails.beneficiaryIFTType, westernUnionBeneficiaryDetails.status,
                            westernUnionBeneficiaryDetails.eftNumber, westernUnionBeneficiaryDetails.cifkey, westernUnionBeneficiaryDetails.tiebNumber))
                }
            }

            Handler(Looper.getMainLooper()).post {
                view.beneficiaryListReturned(beneficiaryList, recentBeneficiaryList)
            }
        }
    }

    override fun holidaysAndTimeValidationResponse(validateForHolidaysAndTimeResponse: ValidateForHolidaysAndTimeResponse) {
        val validateForHolidaysAndTimeObject = validateForHolidaysAndTimeResponse.responseDTO
        val view = viewWeakReference.get() as InternationalPaymentsContract.WesternUnionPaymentHubView
        if ("Failure".equals(validateForHolidaysAndTimeResponse.transactionStatus, true)) {
            dismissProgressIndicator()
            view.navigateToGenericErrorScreen(validateForHolidaysAndTimeResponse.transactionMessage)
        } else {
            if (validateForHolidaysAndTimeObject != null) {
                //NB: This should be validateForHolidaysAndTimeObject.isAllowWU, always remove the exclamation after testing (should be used by anyone that needs to test past 5PM or before 8AM)
                if (validateForHolidaysAndTimeObject.allowWU) {
                    if (isOnceOff) {
                        dismissProgressIndicator()
                        view.navigateToInternationalPaymentsDisclaimer()
                    } else {
                        fetchBeneficiaryDetails(beneficiaryDetails)
                    }
                } else {
                    dismissProgressIndicator()
                    view.navigateToInternationalPaymentsHoursNote()
                }
            } else {
                showErrorScreen()
            }
        }
    }

    override fun onPaySomeoneNew() {
        isOnceOff = true
        internationalPaymentsInteractor.validateForHolidaysAndTime(validateForHolidaysAndTimeExtendedResponseListener)
    }

    override fun beneficiaryServiceResponse(westernUnionBeneficiaryDetails: WesternUnionBeneficiaryDetails) {
        dismissProgressIndicator()
        val view = viewWeakReference.get() as InternationalPaymentsContract.WesternUnionPaymentHubView
        view.saveBeneficiaryDetails(westernUnionBeneficiaryDetails)
        view.navigateToDisclaimerFragment()
    }

    override fun showErrorScreen() {
        dismissProgressIndicator()
        val view = viewWeakReference.get() as InternationalPaymentsContract.WesternUnionPaymentHubView
        view.showGenericErrorMessage()
    }

    override fun fetchBeneficiary(beneficiaryDetails: InternationalPaymentBeneficiaryDetails) {
        isOnceOff = false
        this.beneficiaryDetails = beneficiaryDetails
        internationalPaymentsInteractor.validateForHolidaysAndTime(validateForHolidaysAndTimeExtendedResponseListener)
    }

    private fun fetchBeneficiaryDetails(beneficiaryDetails: InternationalPaymentBeneficiaryDetails) {
        this.beneficiaryDetails = beneficiaryDetails
        internationalPaymentsInteractor.getOutBoundPendingTransaction(beneficiaryDetails.beneficiaryId.toString(), pendingTransactionExtendedResponseListener)
    }

    override fun transactionStatusResponse(pendingTransactionResponse: OutBoundPendingTransactionResponse?) {
        if (!pendingTransactionResponse!!.isPending) {
            internationalPaymentsInteractor.fetchBeneficiaryDetails(beneficiaryDetails, westernUnionBeneficiaryDetailsExtendedResponseListener)
        } else {
            val view = viewWeakReference.get() as InternationalPaymentsContract.WesternUnionPaymentHubView
            view.navigateToPendingTransactionScreen()
        }
    }
}