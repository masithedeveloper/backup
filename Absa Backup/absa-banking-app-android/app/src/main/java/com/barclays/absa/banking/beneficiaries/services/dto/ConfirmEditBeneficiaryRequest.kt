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
package com.barclays.absa.banking.beneficiaries.services.dto

import com.barclays.absa.banking.beneficiaries.services.AddPaymentBeneficiaryResponseParser
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0332_EDIT_BENEFICIARY_CONFIRMATION
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.framework.app.BMBConstants

class ConfirmEditBeneficiaryRequest<T>(beneficiaryDetailObject: BeneficiaryDetailObject,
                                       notificationMethodDetails: Transaction?,
                                       responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        val requestParamsBuilder = RequestParams.Builder()
                .put(OP0332_EDIT_BENEFICIARY_CONFIRMATION)
                .put(Transaction.SERVICE_BENEFICIARY_NAME, beneficiaryDetailObject.beneficiaryName)
                .put(Transaction.SERVICE_BENEFICIARY_TYPE, BMBConstants.PASS_PAYMENT.toLowerCase())
                .put(Transaction.SERVICE_BENEFICIARY_STATUS_TYPE, beneficiaryDetailObject.beneficiaryStatusType)
                .put(Transaction.SERVICE_ACCOUNT_NUMBER, beneficiaryDetailObject.actNo)
                .put(Transaction.SERVICE_ACCOUNT_TYPE, beneficiaryDetailObject.accountType)
                .put(Transaction.SERVICE_BANK_ACCOUNT_NO, beneficiaryDetailObject.benAcctNumAtInst)
                .put(Transaction.SERVICE_BANK_ACCOUNT_HOLDER, beneficiaryDetailObject.beneficiaryName)
                .put(Transaction.SERVICE_INSTITUTION_CODE, beneficiaryDetailObject.instCode.trim())
                .put(Transaction.SERVICE_BEN_RECIPIENT_NAME, beneficiaryDetailObject.beneficiaryName)
                .put(Transaction.SERVICE_BEN_REFERENCE, beneficiaryDetailObject.benReference)
                .put(Transaction.SERVICE_BEN_NOTICE_TYPE, beneficiaryDetailObject.benNoticeType)
                .put(Transaction.SERVICE_IMAGE, beneficiaryDetailObject.imageName)
                .put(Transaction.SERVICE_IMAGE_NAME, beneficiaryDetailObject.imageName)
                .put(Transaction.SERVICE_BENEFICIARY_ID, beneficiaryDetailObject.beneficiaryId)
                .put(Transaction.SERVICE_BENEFICIARY_TIEBNUMBER, beneficiaryDetailObject.tiebNumber)
                .put(Transaction.SERVICE_BANK_NAME, beneficiaryDetailObject.bankName)
                .put(Transaction.SERVICE_BRANCH_NAME, beneficiaryDetailObject.branch)
                .put(Transaction.SERVICE_BRANCH_CODE, beneficiaryDetailObject.branchCode)
                .put(Transaction.SERVICE_MY_REFERENCE, beneficiaryDetailObject.myReference)
                .put(Transaction.SERVICE_MY_NOTICE_TYPE, beneficiaryDetailObject.myNoticeType)
                .put(Transaction.SERVICE_MY_MOBILE, beneficiaryDetailObject.cellNo)
                .put(Transaction.SERVICE_MY_EMAIL, beneficiaryDetailObject.email)
                .put(Transaction.SERVICE_MY_FAX_NUM, beneficiaryDetailObject.faxNumber)
                .put(Transaction.SERVICE_MY_FAX_CODE, beneficiaryDetailObject.faxCode)
                .put(Transaction.SERVICE_THEIR_FAX_CODE, beneficiaryDetailObject.benFaxCode)

        notificationMethodDetails?.let {
            requestParamsBuilder.put(notificationMethodDetails, beneficiaryDetailObject.benNoticeDetail)
        }
        params =  requestParamsBuilder.build()

        mockResponseFile = BeneficiariesMockFactory.editPaymentConfirmation()
        responseParser = AddPaymentBeneficiaryResponseParser()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AddBeneficiaryPaymentObject::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}