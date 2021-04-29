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

import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.JAVA_ERROR
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.responseListeners.InternationalPaymentsResultResponseExtendedResponseListener
import com.barclays.absa.banking.payments.international.responseListeners.PerformOnceOffInternationalPaymentResponseExtendedResponseListener
import com.barclays.absa.banking.payments.international.responseListeners.WesternUnionValidatePaymentExtendedResponseListener
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsInteractor
import com.barclays.absa.banking.payments.international.services.dto.InternationalPaymentsResultResponse
import com.barclays.absa.banking.payments.international.services.dto.PerformOnceOffInternationalPaymentResponse
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionValidatePaymentResponse
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.DateUtils
import java.lang.ref.WeakReference

class InternationalPaymentsConfirmPaymentPresenter(globalView: InternationalPaymentsContract.InternationalPaymentsConfirmPaymentView, var sureCheckDelegate: SureCheckDelegate, private var paymentDetails: BeneficiaryEnteredDetails?) : AbstractPresenter(WeakReference(globalView)), InternationalPaymentsContract.InternationalPaymentsConfirmPaymentPresenter {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    private var internationalPaymentsInteractor: InternationalPaymentsInteractor = InternationalPaymentsInteractor()
    private val performOnceOffInternationalPaymentResponseExtendedResponseListener: PerformOnceOffInternationalPaymentResponseExtendedResponseListener by lazy { PerformOnceOffInternationalPaymentResponseExtendedResponseListener(this) }
    private val westernUnionValidatePaymentExtendedResponseListener: WesternUnionValidatePaymentExtendedResponseListener by lazy { WesternUnionValidatePaymentExtendedResponseListener(this) }
    private val internationalPaymentsResultResponseExtendedResponseListener: InternationalPaymentsResultResponseExtendedResponseListener by lazy { InternationalPaymentsResultResponseExtendedResponseListener(this) }
    private var referenceNumber: String? = ""
    private var isOnceOff: Boolean = false

    override fun paymentValidation(isOnceOff: Boolean, reference: String) {
        this.isOnceOff = isOnceOff
        showProgressIndicator()
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsConfirmPaymentView
        if (BuildConfigHelper.STUB) {
            dismissProgressIndicator()
            view.showPaymentPendingScreen("today", "CB This is a test")
        }

        paymentDetails?.let { internationalPaymentCacheService.setEnteredBeneficiaryDetails(it) }
        if (isOnceOff) {
            internationalPaymentsInteractor.performOnceOffInternationalPayment(paymentDetails, internationalPaymentCacheService.getQuoteDetails(), performOnceOffInternationalPaymentResponseExtendedResponseListener)
        } else {
            internationalPaymentsInteractor.validatePayment(westernUnionValidatePaymentExtendedResponseListener)
        }
    }

    override fun paymentValidated(successResponse: WesternUnionValidatePaymentResponse?) {
        referenceNumber = successResponse?.transactionReferenceID
        performBeneficiaryPayment()
    }

    override fun performBeneficiaryPayment() {
        internationalPaymentsInteractor.performInternationalPayment(referenceNumber.toString(), internationalPaymentsResultResponseExtendedResponseListener)
    }

    override fun processOnceOffPayment() {
        internationalPaymentsInteractor.performOnceOffInternationalPayment(paymentDetails, internationalPaymentCacheService.getQuoteDetails(), performOnceOffInternationalPaymentResponseExtendedResponseListener)
    }

    override fun onceOffPaymentResponse(successResponse: PerformOnceOffInternationalPaymentResponse) {
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsConfirmPaymentView

        if (SUCCESS.equals(successResponse.internationalPaymentsResult?.resultDetail, ignoreCase = true)) {
            dismissProgressIndicator()
            view.showPaymentPendingScreen(successResponse.internationalPaymentsResult?.transactionDate.toString(), successResponse.internationalPaymentsResult?.transactionReferenceNumber.toString())
        }

        if (successResponse.errorMessage != null && successResponse.errorMessage == FAILURE && successResponse.errorMessage.contains(JAVA_ERROR)) {
            dismissProgressIndicator()
            view.showConnectionErrorScreen()
        } else if (successResponse.errorMessage != null && successResponse.errorMessage == FAILURE) {
            dismissProgressIndicator()
            view.showError(successResponse.transactionMessage.toString())
        }

        if (TransactionVerificationType.SURECHECKV2Required.toString().equals(successResponse.sureCheckFlag, ignoreCase = true)) {
            sureCheckDelegate.processSureCheck(view.fetchbaseActivity(), successResponse) { this.processOnceOffPayment() }
        }
    }

    override fun existingBeneficiaryPaymentResponse(successResponse: InternationalPaymentsResultResponse) {
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsConfirmPaymentView

        if (SUCCESS.equals(successResponse.internationalPaymentsResult?.resultDetail, true)) {
            dismissProgressIndicator()
            view.showPaymentPendingScreen(DateUtils.getCurrentDateAndTime(), successResponse.internationalPaymentsResult?.transactionReferenceNumber.toString())
        }

        if (successResponse.internationalPaymentsResult?.errorMessage.equals(FAILURE, true) && successResponse.errorMessage?.contains(JAVA_ERROR) == false) {
            dismissProgressIndicator()
            view.showError(successResponse.transactionMessage.toString())
        } else if (successResponse.internationalPaymentsResult?.errorMessage?.contains(JAVA_ERROR) == true) {
            dismissProgressIndicator()
            view.showConnectionErrorScreen()
        }

        if (TransactionVerificationType.SURECHECKV2Required.toString().equals(successResponse.sureCheckFlag, ignoreCase = true)) {
            sureCheckDelegate.processSureCheck(view.fetchbaseActivity(), successResponse) { this.performBeneficiaryPayment() }
        }
    }
}