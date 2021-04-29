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

package com.barclays.absa.banking.funeralCover.services.dto

import com.barclays.absa.banking.boundary.model.AllPerils
import com.barclays.absa.banking.boundary.model.PolicyClaim
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0857_REGISTER_CLAIMS
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import styleguide.utils.extensions.removeSpaces

class AllPerilsRequest<T>(policyClaim: PolicyClaim, duplicateClaim: String, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        val address = policyClaim.policyDetail?.shortTermPolicyComponents?.get(0)?.propertyAddress?.getFormattedAddress()
        val claimCategory = policyClaim.claimCategory

        params = RequestParams.Builder()
                .put(OP0857_REGISTER_CLAIMS)
                .put(Transaction.SERVICE_CLAIM_TYPE, policyClaim.claimType)
                .put(Transaction.SERVICE_DATE_OF_INCIDENT, policyClaim.incidentDate)
                .put(Transaction.SERVICE_HAS_ITEM_BEEN_FIXED, policyClaim.itemAffected)
                .put(Transaction.SERVICE_ADDITIONAL_DAMAGE, policyClaim.additionalDamage)
                .put(Transaction.SERVICE_ALTERNATIVE_NAME, policyClaim.alternativeContactName)
                .put(Transaction.SERVICE_ALTERNATIVE_NUMBER, policyClaim.alternativeContactNumber.removeSpaces())
                .put(Transaction.SERVICE_INSURED_PROPERTY, address)
                .put(Transaction.SERVICE_ALTERNATE_CONTACT_NUMBER, policyClaim.alternativeContactNumber.removeSpaces())
                .put(Transaction.SERVICE_EMAIL_ADDRESS, policyClaim.beneficiaryDetail?.email)
                .put(Transaction.SERVICE_CONTACT_NUMBER, policyClaim.beneficiaryDetail?.actualCellNo.removeSpaces())
                .put(Transaction.SERVICE_POLICY_NUMBER, policyClaim.policyDetail?.policy?.number)
                .put(Transaction.DUPLICATE_CLAIMS, duplicateClaim)
                .put(Transaction.SERVICE_CLAIM_RESULTANT_DAMAGE, policyClaim.additionalDamagedItems)
                .put(Transaction.SERVICE_CLAIM_CATEGORY, claimCategory)
                .put(Transaction.SERVICE_CAUSE_CODE, policyClaim.causeCode)
                .build()

        mockResponseFile = "home_loan/op0857_register_claims.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = AllPerils::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}