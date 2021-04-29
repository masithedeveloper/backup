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

package com.barclays.absa.banking.funeralCover

import com.barclays.absa.banking.boundary.model.funeralCover.FamilyMemberCoverDetails
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails
import com.barclays.absa.banking.boundary.model.funeralCover.RolePlayerDetails
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteInteractor
import com.barclays.absa.banking.funeralCover.ui.AdditionalFamilyMemberPresenter
import com.barclays.absa.banking.funeralCover.ui.AdditionalFamilyMemberView
import com.barclays.absa.banking.funeralCover.ui.responseListeners.AdditionalFamilyMemberExtendedResponseListener
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import java.lang.ref.WeakReference

class AdditionalFamilyMemberPresenterTest {

    private lateinit var interactorMock: FuneralCoverQuoteInteractor
    private lateinit var presenter: AdditionalFamilyMemberPresenter
    private lateinit var viewWeakReferenceMock: WeakReference<AdditionalFamilyMemberView>
    private var rolePlayerDetails: RolePlayerDetails? = RolePlayerDetails()
    private lateinit var familyMemberCoverDetails: FamilyMemberCoverDetails
    private lateinit var funeralCoverDetails: FuneralCoverDetails
    private val responseListenerArgumentCaptor = argumentCaptor<AdditionalFamilyMemberExtendedResponseListener>()

    @Before
    fun setUp() {
        interactorMock = mock()
        viewWeakReferenceMock = WeakReference(mock())
        presenter = AdditionalFamilyMemberPresenter(viewWeakReferenceMock, interactorMock)

        familyMemberCoverDetails = FamilyMemberCoverDetails()
        familyMemberCoverDetails.relationship = "SON"
        familyMemberCoverDetails.relationshipCode = "SON"
        familyMemberCoverDetails.benefitCode = "FC1"
        familyMemberCoverDetails.gender = "M"
        familyMemberCoverDetails.surname = "Moabelo"
        familyMemberCoverDetails.coverAmount = "10000.00"
        familyMemberCoverDetails.premiumAmount = "8.50"
        familyMemberCoverDetails.initials = "KM"

        rolePlayerDetails = RolePlayerDetails()
        rolePlayerDetails?.listFamilyMemberCoverDetails = listOf(familyMemberCoverDetails)
        rolePlayerDetails?.sumAssured = "sumAssured"
        rolePlayerDetails?.initials = "initials"
        rolePlayerDetails?.surname = "surname"
        rolePlayerDetails?.gender = "M"
        rolePlayerDetails?.dateOfBirth = "01/09/2018"
        rolePlayerDetails?.relationship = "relationship"
        rolePlayerDetails?.category = "category"
        rolePlayerDetails?.premiumAmount = "1000"
        rolePlayerDetails?.coverAmount = "1000"
        rolePlayerDetails?.relationshipCode = "1000"
        rolePlayerDetails?.benefitCode = "1000"

        funeralCoverDetails = FuneralCoverDetails()

    }

    @Test
    fun shouldFetchFamilyMemberQuoteWhenSubmitButtonIsClicked() {
        presenter.onAddFamilyMemberButtonClicked(funeralCoverDetails, familyMemberCoverDetails)
        verify(viewWeakReferenceMock.get())?.showProgressDialog()
        verify(interactorMock).pullCoverAmountForRelationApplyFuneralPlan(responseListenerArgumentCaptor.capture(), eq(funeralCoverDetails), eq(familyMemberCoverDetails))
        verifyNoMoreInteractions(viewWeakReferenceMock.get(), interactorMock)
    }

    @Test
    fun shouldDisplayFamilyMemberQuoteWhenFamilyMemberCoverDetailsListIsNotNullAndIsNotEmpty() {
        presenter.onFamilyMemberCoverDetailsReceived(rolePlayerDetails)
        verify(viewWeakReferenceMock.get())?.displayFamilyMemberQuote(rolePlayerDetails?.listFamilyMemberCoverDetails)
        verify(viewWeakReferenceMock.get())?.dismissProgressDialog()
        verifyNoMoreInteractions(viewWeakReferenceMock.get(), interactorMock)
    }

    @Test
    fun shouldShowSomethingWentWrongScreenWhenFamilyMemberCoverDetailsListIsNull() {
        rolePlayerDetails?.listFamilyMemberCoverDetails = null
        presenter.onFamilyMemberCoverDetailsReceived(rolePlayerDetails)
        verify(viewWeakReferenceMock.get())?.showSomethingWentWrongScreen()
        verify(viewWeakReferenceMock.get())?.dismissProgressDialog()
        verifyNoMoreInteractions(viewWeakReferenceMock.get(), interactorMock)
    }

    @Test
    fun shouldShowSomethingWentWrongScreenWhenFamilyMemberCoverDetailsListIsEmpty() {
        rolePlayerDetails?.listFamilyMemberCoverDetails = emptyList()
        presenter.onFamilyMemberCoverDetailsReceived(rolePlayerDetails)
        verify(viewWeakReferenceMock.get())?.showSomethingWentWrongScreen()
        verify(viewWeakReferenceMock.get())?.dismissProgressDialog()
        verifyNoMoreInteractions(viewWeakReferenceMock.get(), interactorMock)
    }
}
