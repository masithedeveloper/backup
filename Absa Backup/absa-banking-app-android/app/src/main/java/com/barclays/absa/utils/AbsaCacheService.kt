/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.utils

class AbsaCacheService : IAbsaCacheService {

    private var isPersonalClientAgreementAccepted = false
    private var isClientTypeCached = false
    private var isCallMeBackRequested = false
    private var isInternationalPaymentsAllowed = false

    override fun isPersonalClientAgreementAccepted(): Boolean = isPersonalClientAgreementAccepted
    override fun setPersonalClientAgreementAccepted(personalClientAgreementAccepted: Boolean) {
        isPersonalClientAgreementAccepted = personalClientAgreementAccepted
    }

    override fun isClientTypeCached(): Boolean = isClientTypeCached
    override fun setClientTypeCached(clientTypeCached: Boolean) {
        isClientTypeCached = clientTypeCached
    }

    override fun isCallMeBackRequested(): Boolean = isCallMeBackRequested
    override fun setCallMeBackRequested(callMeBackRequested: Boolean) {
        isCallMeBackRequested = callMeBackRequested
    }

    override fun isInternationalPaymentsAllowed(): Boolean = isInternationalPaymentsAllowed
    override fun setInternationalPaymentsAllowed(internationalPaymentsAllowed: Boolean) {
        isInternationalPaymentsAllowed = internationalPaymentsAllowed
    }

    override fun clear() {
        isPersonalClientAgreementAccepted = false
        isClientTypeCached = false
        isCallMeBackRequested = false
        isInternationalPaymentsAllowed = false
    }
}