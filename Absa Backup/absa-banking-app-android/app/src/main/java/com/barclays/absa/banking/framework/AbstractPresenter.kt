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
package com.barclays.absa.banking.framework

import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.framework.utils.BMBLogger
import java.lang.ref.WeakReference

open class AbstractPresenter(@JvmField protected val viewWeakReference: WeakReference<out BaseView>) {

    fun showProgressIndicator() {
        val view = viewWeakReference.get()
        view?.showProgressDialog()
        BMBLogger.d("ProgressDialog", view?.hashCode().toString() + " Started")
    }

    fun dismissProgressIndicator() {
        val view = viewWeakReference.get()
        view?.dismissProgressDialog()
        BMBLogger.d("ProgressDialog", view?.hashCode().toString() + " Dismissed")
    }

    fun showGenericErrorMessage(className: String) {
        val view = viewWeakReference.get()
        view?.dismissProgressDialog()
        MonitoringInteractor().logExtendedResponseListenerObjectNull(className)
        view?.showGenericErrorMessage()
    }

    fun showErrorMessage(errorMessage: String) {
        val view = viewWeakReference.get()
        view?.dismissProgressDialog()
        view?.showMessageError(errorMessage)
    }
}