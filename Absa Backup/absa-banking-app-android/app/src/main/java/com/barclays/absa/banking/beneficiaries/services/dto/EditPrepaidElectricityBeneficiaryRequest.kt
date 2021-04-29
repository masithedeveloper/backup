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

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesMockFactory
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService
import com.barclays.absa.banking.beneficiaries.services.BeneficiariesService.OP2086_EDIT_PREPAID_ELECTRICITY_BENEFICIARY
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.boundary.model.MeterNumberObject
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams

class EditPrepaidElectricityBeneficiaryRequest<T>(beneficiaryName: String, beneficiaryDetailObject: BeneficiaryDetailObject,
                                                  meterNumberObject: MeterNumberObject, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        val hasImage = if (beneficiaryDetailObject.hasImage) "Y" else "N"
        params = RequestParams.Builder()
                .put(OP2086_EDIT_PREPAID_ELECTRICITY_BENEFICIARY)
                .put(BeneficiariesService.BENEFICIARY_TYPE, beneficiaryDetailObject.beneficiaryType)
                .put(BeneficiariesService.BENEFICIARY_NAME, beneficiaryName)
                .put(BeneficiariesService.BENEFICIARY_ID, beneficiaryDetailObject.beneficiaryId)
                .put(BeneficiariesService.UTILITY, meterNumberObject.utility)
                .put(BeneficiariesService.METER_NUMBER, meterNumberObject.meterNumber)
                .put(BeneficiariesService.MY_NOTIFICATION_TYPE, beneficiaryDetailObject.myNoticeType)
                .put(BeneficiariesService.CELLPHONE_NUMBER, beneficiaryDetailObject.cellNo)
                .put(BeneficiariesService.EMAIL, beneficiaryDetailObject.email)
                .put(BeneficiariesService.FAX_CODE, beneficiaryDetailObject.faxCode)
                .put(BeneficiariesService.FAX_NUMBER, beneficiaryDetailObject.faxNumber)
                .put(BeneficiariesService.PAYMENT_MADE_BY, CustomerProfileObject.instance.customerName)
                .put(BeneficiariesService.BENEFICIARY_NOTIFICATION_TYPE, beneficiaryDetailObject.benNoticeType)
                .put(BeneficiariesService.BENEFICIARY_CELLPHONE_NUMBER, beneficiaryDetailObject.benCellNo)
                .put(BeneficiariesService.BENEFICIARY_EMAIL, beneficiaryDetailObject.benEmail)
                .put(BeneficiariesService.BENEFICIARY_FAX_CODE, beneficiaryDetailObject.benFaxCode)
                .put(BeneficiariesService.BENEFICIARY_FAX_NUMBER, beneficiaryDetailObject.benFaxNumber)
                .put(BeneficiariesService.RECIPIENT_NAME, meterNumberObject.customerName)
                .put(TransactionParams.Transaction.HAS_IMAGE, hasImage)
                .build()

        mockResponseFile = BeneficiariesMockFactory.editPrepaidElectricityBeneficiaryResults()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = TransactionResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}