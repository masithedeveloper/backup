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
package com.barclays.absa.banking.debiCheck.services

import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDataModel
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderList
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.debiCheck.services.dto.DebitOrderReversePaymentRequest
import com.barclays.absa.banking.debiCheck.services.dto.DebitOrderViewStoppedPaymentsRequest
import com.barclays.absa.banking.debiCheck.services.dto.DebitOrdersRequest
import com.barclays.absa.banking.debiCheck.services.dto.StopDebitOrderRequest
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener

class DebitOrderInteractor : AbstractInteractor(), DebitOrderService {

    override fun fetchDebitOrders(debitOrderDataModel: DebitOrderDataModel, debitOrderExtendedResponseListener: ExtendedResponseListener<DebitOrderList>) {
        submitRequest(DebitOrdersRequest(debitOrderDataModel, debitOrderExtendedResponseListener))
    }

    override fun fetchStoppedDebitOrders(debitOrderDataModel: DebitOrderDataModel, debitOrderStoppedPaymentExtendedResponseListener: ExtendedResponseListener<DebitOrderList>) {
        submitRequest(DebitOrderViewStoppedPaymentsRequest(debitOrderDataModel, debitOrderStoppedPaymentExtendedResponseListener))
    }

    override fun reverseDebitOrderPayment(debitOrderDataModel: DebitOrderDataModel, reverseDebitOrderPaymentExtendedResponseListener: ExtendedResponseListener<SureCheckResponse>) {
        submitRequest(DebitOrderReversePaymentRequest(debitOrderDataModel, reverseDebitOrderPaymentExtendedResponseListener))
    }

    override fun stopDebitOrderPayment(debitOrderDataModel: DebitOrderDataModel, stopDebitOrderExtendedResponseListener: ExtendedResponseListener<TransactionResponse>) {
        submitRequest(StopDebitOrderRequest(debitOrderDataModel, stopDebitOrderExtendedResponseListener))
    }
}