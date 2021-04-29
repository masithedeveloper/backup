/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.funeralCover.ui

import com.barclays.absa.banking.boundary.model.funeralCover.FamilyMemberCoverDetails
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails
import com.barclays.absa.banking.boundary.model.funeralCover.RolePlayerDetails
import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteInteractor
import com.barclays.absa.banking.funeralCover.ui.responseListeners.AdditionalFamilyMemberExtendedResponseListener
import org.jetbrains.annotations.TestOnly
import java.lang.ref.WeakReference

class AdditionalFamilyMemberPresenter internal constructor(viewWeakReference: WeakReference<AdditionalFamilyMemberView>) : AbstractPresenter(viewWeakReference) {
    private var funeralCoverQuoteInteractor = FuneralCoverQuoteInteractor()
    private val additionalFamilyMemberExtendedResponseListener: AdditionalFamilyMemberExtendedResponseListener by lazy { AdditionalFamilyMemberExtendedResponseListener(this) }

    @TestOnly
    internal constructor(viewWeakReference: WeakReference<AdditionalFamilyMemberView>, funeralCoverQuoteInteractor: FuneralCoverQuoteInteractor) : this(viewWeakReference) {
        this.funeralCoverQuoteInteractor = funeralCoverQuoteInteractor
    }

    fun onAddFamilyMemberButtonClicked(funeralCoverDetails: FuneralCoverDetails, familyMemberCoverDetails: FamilyMemberCoverDetails) {
        showProgressIndicator()
        funeralCoverQuoteInteractor.pullCoverAmountForRelationApplyFuneralPlan(additionalFamilyMemberExtendedResponseListener, funeralCoverDetails, familyMemberCoverDetails)
    }

    fun onFamilyMemberCoverDetailsReceived(rolePlayerDetails: RolePlayerDetails?) {
        val familyMemberView = viewWeakReference.get() as AdditionalFamilyMemberView?
        val familyMemberCoverDetailsList = rolePlayerDetails?.listFamilyMemberCoverDetails
        familyMemberView?.let {
            if (familyMemberCoverDetailsList != null && !familyMemberCoverDetailsList.isEmpty()) {
                it.displayFamilyMemberQuote(familyMemberCoverDetailsList)
            } else {
                it.showSomethingWentWrongScreen()
            }
        }
        dismissProgressIndicator()
    }
}
