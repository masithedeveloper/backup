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

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.home.services.HomeScreenInteractor
import com.barclays.absa.banking.home.services.HomeScreenService
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackResponse
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationDataModel
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationResponse
import java.lang.ref.WeakReference

class ContactUsContainerPresenter(view: ContactUsContainerView) {

    private val weakReference: WeakReference<ContactUsContainerView> = WeakReference(view)
    private val homeScreenInteractor: HomeScreenService

    fun getView(): ContactUsContainerView? = weakReference.get()

    private val callBackResponseListener = object : ExtendedResponseListener<CallBackResponse>() {
        override fun onSuccess(successResponse: CallBackResponse) {
            super.onSuccess()
            getView()?.let { view ->
                if (BMBConstants.FAILURE.equals(successResponse.transactionStatus, ignoreCase = true)) {
                    view.navigateToGenericFailureScreen(successResponse.transactionMessage)
                } else {
                    view.navigateToCallMeBackFragment(null, successResponse.uniqueReferenceNumber)
                }
                view.dismissProgressDialog()
            }
        }

        override fun onFailure(failureResponse: ResponseObject) {
            super.onFailure(failureResponse)
            getView()?.dismissProgressDialog()
        }
    }

    private val callBackVerificationResponseResponseListener = object : ExtendedResponseListener<CallBackVerificationResponse>() {
        override fun onSuccess(successResponse: CallBackVerificationResponse) {
            getView()?.let { view ->
                view.dismissProgressDialog()
                view.navigateToCallMeBackSuccessScreen()
            }
        }

        override fun onFailure(failureResponse: ResponseObject) {
            super.onFailure(failureResponse)
            getView()?.let { view ->
                view.dismissProgressDialog()
                view.navigateToCallMeBackFailureScreen()
            }
        }
    }

    init {
        callBackVerificationResponseResponseListener.setView(view)
        callBackResponseListener.setView(view)
        homeScreenInteractor = HomeScreenInteractor()
    }

    fun requestCallBack(secretCode: String, callBackDateTime: String) {
        getView()?.let { view ->
            view.showProgressDialog()
            homeScreenInteractor.requestCallBack(secretCode, callBackDateTime, callBackResponseListener)
        }
    }

    fun requestVerificationCallBack(callBackVerificationDataModel: CallBackVerificationDataModel) {
        getView()?.let { view ->
            view.showProgressDialog()
            homeScreenInteractor.verifyCallBack(callBackVerificationDataModel, callBackVerificationResponseResponseListener)
        }
    }
}