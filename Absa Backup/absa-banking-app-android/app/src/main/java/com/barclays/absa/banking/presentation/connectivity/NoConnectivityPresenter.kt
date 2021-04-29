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

package com.barclays.absa.banking.presentation.connectivity

import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.presentation.connectivity.NoConnectivityActivity.*
import java.lang.ref.WeakReference

internal class NoConnectivityPresenter(noConnectivityView: NoConnectivityView) {

    private val noConnectivityViewWeakReference: WeakReference<NoConnectivityView> = WeakReference(noConnectivityView)

    fun onCallToActionClicked(connectivityType: String) {
        val noConnectivityView = noConnectivityViewWeakReference.get()
        if (noConnectivityView != null) {
            when (connectivityType) {
                CONNECTIVITY_DATA_SIGNAL, CONNECTIVITY_ENTERSEKT -> noConnectivityView.retry()
                CONNECTIVITY_MAINTENANCE -> noConnectivityView.close()
                else -> {
                }
            }
        }
    }

    fun onViewLoaded(connectivityType: String) {
        val noConnectivityView = noConnectivityViewWeakReference.get()
        if (noConnectivityView != null) {
            val buttonText: String
            val message: String
            val instruction: String
            when (connectivityType) {
                CONNECTIVITY_DATA_SIGNAL -> {
                    buttonText = BMBApplication.getInstance().getString(R.string.connectivity_try_again)
                    message = ""
                    instruction = BMBApplication.getInstance().getString(R.string.connectivity_turn_on_connection)
                }
                CONNECTIVITY_ENTERSEKT -> {
                    buttonText = BMBApplication.getInstance().getString(R.string.connectivity_try_again)
                    message = BMBApplication.getInstance().getString(R.string.connectivity_entersekt_message)
                    instruction = BMBApplication.getInstance().getString(R.string.connectivity_entersekt_instruction)
                }
                CONNECTIVITY_MAINTENANCE -> {
                    message = BMBApplication.getInstance().getString(R.string.connectivity_maintenance_message)
                    instruction = BMBApplication.getInstance().getString(R.string.connectivity_maintenance_instruction)
                    buttonText = BMBApplication.getInstance().getString(R.string.connectivity_okay)
                }
                else -> {
                    message = BMBApplication.getInstance().getString(R.string.connectivity_maintenance_message)
                    instruction = BMBApplication.getInstance().getString(R.string.connectivity_maintenance_instruction)
                    buttonText = BMBApplication.getInstance().getString(R.string.connectivity_okay)
                }
            }
            noConnectivityView.setActionText(buttonText)
            noConnectivityView.setMessage(message)
            noConnectivityView.setInstruction(instruction)
        }
    }
}