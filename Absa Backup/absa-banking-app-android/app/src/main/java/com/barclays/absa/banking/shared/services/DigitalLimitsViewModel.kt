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

package com.barclays.absa.banking.shared.services

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.limits.DigitalLimit
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitItem
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeConfirmationResult
import com.barclays.absa.banking.boundary.model.limits.DigitalLimitsChangeResult
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.settings.services.digitalLimits.DigitalLimitInteractor
import com.barclays.absa.banking.settings.services.digitalLimits.DigitalLimitService
import com.barclays.absa.banking.shared.responseListeners.DigitalLimitsChangeConfirmationExtendedResponseListener
import com.barclays.absa.banking.shared.responseListeners.DigitalLimitsChangeResultExtendedResponseListener
import com.barclays.absa.banking.shared.responseListeners.DigitalLimitsExtendedResponseListener
import styleguide.utils.extensions.removeCommasAndDots
import styleguide.utils.extensions.removeCurrency

class DigitalLimitsViewModel : BaseViewModel() {

    private val digitalLimitService: DigitalLimitService = DigitalLimitInteractor()
    private val digitalLimitsExtendedResponseListener: ExtendedResponseListener<DigitalLimit?> by lazy { DigitalLimitsExtendedResponseListener(this) }
    private val digitalLimitsChangeConfirmationExtendedResponseListener: ExtendedResponseListener<DigitalLimitsChangeConfirmationResult?> by lazy { DigitalLimitsChangeConfirmationExtendedResponseListener(this) }
    private val digitalLimitsChangeResultExtendedResponseListener: ExtendedResponseListener<DigitalLimitsChangeResult?> by lazy { DigitalLimitsChangeResultExtendedResponseListener(this) }

    var digitalLimit = MutableLiveData<DigitalLimit>()
    var digitalLimitsChangeResult = MutableLiveData<DigitalLimitsChangeResult>()
    var digitalLimitsChangeConfirmationResult = MutableLiveData<DigitalLimitsChangeConfirmationResult>()

    fun retrieveDigitalLimits() {
        digitalLimitService.fetchDigitalLimits(digitalLimitsExtendedResponseListener)
    }

    private fun buildFormattedDigitalLimit(vararg amounts: String): DigitalLimit {
        val digitalLimit = DigitalLimit()

        val dailyPaymentLimit = DigitalLimitItem()
        val dailyInterAccountTransferLimit = DigitalLimitItem()
        val recurringPaymentTransactionLimit = DigitalLimitItem()
        val futureDatedPaymentTransactionLimit = DigitalLimitItem()

        val dailyPaymentLimitAmount = Amount(amounts[0].removeCommasAndDots())
        val dailyInterAccountTransferLimitAmount = Amount(amounts[1].removeCommasAndDots())
        val recurringPaymentTransactionLimitAmount = Amount(amounts[2].removeCommasAndDots())
        val futureDatedPaymentTransactionLimitAmount = Amount(amounts[3].removeCommasAndDots())

        dailyPaymentLimit.actualLimit = dailyPaymentLimitAmount
        dailyInterAccountTransferLimit.actualLimit = dailyInterAccountTransferLimitAmount
        recurringPaymentTransactionLimit.actualLimit = recurringPaymentTransactionLimitAmount
        futureDatedPaymentTransactionLimit.actualLimit = futureDatedPaymentTransactionLimitAmount

        digitalLimit.dailyPaymentLimit = dailyPaymentLimit
        digitalLimit.dailyInterAccountTransferLimit = dailyInterAccountTransferLimit
        digitalLimit.recurringPaymentTransactionLimit = recurringPaymentTransactionLimit
        digitalLimit.futureDatedPaymentTransactionLimit = futureDatedPaymentTransactionLimit

        return digitalLimit
    }

    fun changeDigitalLimits(vararg amounts: String) {

        val newDigitalLimit = buildFormattedDigitalLimit(amounts[0].removeCurrency(), amounts[1].removeCurrency(),
                amounts[2].removeCurrency(), amounts[3].removeCurrency())

        val oldDigitalLimit = buildFormattedDigitalLimit(digitalLimit.value?.dailyPaymentLimit?.actualLimit?.getAmount().toString(),
                digitalLimit.value?.dailyInterAccountTransferLimit?.actualLimit?.getAmount().toString(),
                digitalLimit.value?.recurringPaymentTransactionLimit?.actualLimit?.getAmount().toString(),
                digitalLimit.value?.futureDatedPaymentTransactionLimit?.actualLimit?.getAmount().toString())

        digitalLimitService.changeDigitalLimits(oldDigitalLimit, newDigitalLimit, digitalLimitsChangeResultExtendedResponseListener)
    }

    fun confirmDigitalLimitsChange(stubDigitalLimitsChangeRequestSureCheck2Required: Boolean) {
        if (digitalLimitsChangeResult.value?.transactionReferenceId != null) {
            digitalLimitService.confirmDigitalLimitChange(digitalLimitsChangeResult.value?.transactionReferenceId, stubDigitalLimitsChangeRequestSureCheck2Required, digitalLimitsChangeConfirmationExtendedResponseListener)
        } else {
            // Not really sure how this can be improved
            val transactionResponse = TransactionResponse()
            transactionResponse.transactionMessage = "Null transaction Reference ID"
            failureResponse.value = transactionResponse
        }
    }
}