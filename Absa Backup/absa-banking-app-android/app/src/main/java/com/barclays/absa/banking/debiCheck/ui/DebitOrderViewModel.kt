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
package com.barclays.absa.banking.debiCheck.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDataModel
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderList
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.debiCheck.responseListeners.DebitOrderReversePaymentExtendedResponseListener
import com.barclays.absa.banking.debiCheck.responseListeners.DebitOrderStoppedPaymentExtendedResponseListener
import com.barclays.absa.banking.debiCheck.responseListeners.DebitOrdersExtendedResponseListener
import com.barclays.absa.banking.debiCheck.responseListeners.StopDebitOrderExtendedResponseListener
import com.barclays.absa.banking.debiCheck.services.DebitOrderInteractor
import com.barclays.absa.banking.debiCheck.services.DebitOrderService
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.utils.DateUtils
import java.util.*

class DebitOrderViewModel : BaseViewModel() {
    var debitOrderDataModel = DebitOrderDataModel()
    var debitOrderService: DebitOrderService = DebitOrderInteractor()

    var debitOrdersList = MutableLiveData<DebitOrderList>()
    var stoppedDebitOrderList =  MutableLiveData<DebitOrderList>()
    var reverseDebitOrderSureCheckResponse = MutableLiveData<SureCheckResponse>()
    var stopDebitOrderResponse = MutableLiveData<TransactionResponse>()
    private val debitOrderExtendedResponseListener: ExtendedResponseListener<DebitOrderList> by lazy { DebitOrdersExtendedResponseListener(this) }
    private val debitOrderStoppedPaymentExtendedResponseListener: ExtendedResponseListener<DebitOrderList> by lazy { DebitOrderStoppedPaymentExtendedResponseListener(this) }
    private val reverseDebitOrderPaymentExtendedResponseListener: ExtendedResponseListener<SureCheckResponse> by lazy { DebitOrderReversePaymentExtendedResponseListener(this) }
    private val stopDebitOrderExtendedResponseListener: ExtendedResponseListener<TransactionResponse> by lazy { StopDebitOrderExtendedResponseListener(this) }

    fun fetchDebitOrderTransactionList() {
        setAccountDebitOrdersForTheLastFortyDays()
        debitOrderService.fetchDebitOrders(debitOrderDataModel, debitOrderExtendedResponseListener)
    }

    fun fetchStoppedDebitOrderList(debitOrderDataModel: DebitOrderDataModel) {
        debitOrderService.fetchStoppedDebitOrders(debitOrderDataModel, debitOrderStoppedPaymentExtendedResponseListener)
    }

    fun reverseDebitOrderPayment(debitOrderDataModel: DebitOrderDataModel) {
        debitOrderService.reverseDebitOrderPayment(debitOrderDataModel, reverseDebitOrderPaymentExtendedResponseListener)
    }

    fun stopDebitOrder(debitOrderDataModel: DebitOrderDataModel) {
        debitOrderService.stopDebitOrderPayment(debitOrderDataModel, stopDebitOrderExtendedResponseListener)
    }

    private fun setAccountDebitOrdersForTheLastFortyDays() {
        val today = Date()
        val calendar = GregorianCalendar()
        calendar.time = today
        calendar.add(Calendar.DAY_OF_MONTH, -39)
        val fortyDaysAgo = calendar.time

        val fromDateFormatted = DateUtils.format(fortyDaysAgo, "yyyy-MM-dd")
        val toDateFormatted = DateUtils.format(today, "yyyy-MM-dd")
        debitOrderDataModel.fromDate = fromDateFormatted
        debitOrderDataModel.toDate = toDateFormatted
    }
}