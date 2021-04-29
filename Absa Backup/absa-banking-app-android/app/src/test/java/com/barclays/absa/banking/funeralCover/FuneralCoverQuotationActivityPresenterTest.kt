package com.barclays.absa.banking.funeralCover

import com.barclays.absa.banking.boundary.model.funeralCover.FamilyMemberCoverDetails
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteInteractor
import com.barclays.absa.banking.funeralCover.ui.FuneralCoverQuotationActivityPresenter
import com.barclays.absa.banking.funeralCover.ui.FuneralCoverQuotationActivityView
import com.barclays.absa.banking.funeralCover.ui.responseListeners.FuneralCoverQuotationExtendedResponseListener
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import java.lang.ref.WeakReference

class FuneralCoverQuotationActivityPresenterTest {

    private lateinit var viewWeakReferenceMock: WeakReference<FuneralCoverQuotationActivityView>
    private lateinit var funeralCoverQuoteInteractor: FuneralCoverQuoteInteractor
    private lateinit var presenter: FuneralCoverQuotationActivityPresenter
    private lateinit var funeralCoverDetails: FuneralCoverDetails
    private lateinit var familyMemberCoverDetails: FamilyMemberCoverDetails
    private val responseListenerArgumentCaptor = argumentCaptor<FuneralCoverQuotationExtendedResponseListener>()

    @Before
    fun setUp() {
        funeralCoverQuoteInteractor = mock()
        viewWeakReferenceMock = WeakReference(mock())
        presenter = FuneralCoverQuotationActivityPresenter(funeralCoverQuoteInteractor, viewWeakReferenceMock)
        setUpTestData()
    }

    private fun setUpTestData() {
        familyMemberCoverDetails = FamilyMemberCoverDetails()
        familyMemberCoverDetails.relationship = "SON"
        familyMemberCoverDetails.relationshipCode = "SON"
        familyMemberCoverDetails.benefitCode = "FC1"
        familyMemberCoverDetails.gender = "M"
        familyMemberCoverDetails.surname = "Moabelo"
        familyMemberCoverDetails.coverAmount = "10000.00"
        familyMemberCoverDetails.premiumAmount = "8.50"
        familyMemberCoverDetails.initials = "KM"

        funeralCoverDetails = FuneralCoverDetails()
        funeralCoverDetails.txnStatus = "success"
        funeralCoverDetails.policyNumber = "123"
        funeralCoverDetails.debitDate = "1"
        funeralCoverDetails.policyStartDate = "01/09/2018"
        funeralCoverDetails.accountToDebit = "9051014363"
        funeralCoverDetails.accountNumber = "9051014364"
        funeralCoverDetails.accountDescription = "savingsAccount"
        funeralCoverDetails.accountType = "savingsAccount"
        funeralCoverDetails.sourceOfFunds = "20-SALARY/WAGES"
        funeralCoverDetails.yearlyIncrease = "True"
        funeralCoverDetails.mainMemberCover = "200000.00"
        funeralCoverDetails.spouseCover = "20000.00"
        funeralCoverDetails.mainMemberPremium = "149.50"
        funeralCoverDetails.spousePremium = "35.50"
        funeralCoverDetails.totalMonthlyPremium = "149.93"
        funeralCoverDetails.familySelected = "true"
        funeralCoverDetails.planCode = "11"
        funeralCoverDetails.spousePlanCode = "Y"
        funeralCoverDetails.totalCoverAmount = "50000.00"
        funeralCoverDetails.familyInitials = "KM"
        funeralCoverDetails.familySurname = "Moabelo"
        funeralCoverDetails.familyGender = "M"
        funeralCoverDetails.familyDateOfBirth = "978-06-07"
        funeralCoverDetails.familyCoverAmount = "10000.00"
        funeralCoverDetails.familyPremium = "8.50"
        funeralCoverDetails.familyRelationship = "SON"
        funeralCoverDetails.familyRelationshipCode = "FC1"
        funeralCoverDetails.familyBenefitCode = "FC1"
        funeralCoverDetails.familyCategory = "Spouse"
        funeralCoverDetails.familyMemberList = mutableListOf(familyMemberCoverDetails)
    }

    @Test
    fun shouldPullFuneralApplicationPlanOnAcceptButtonQuoteClicked() {
        presenter.onAcceptQuoteButtonClicked(funeralCoverDetails)
        verify(funeralCoverQuoteInteractor).pullFuneralPlanApplication(responseListenerArgumentCaptor.capture(), eq(funeralCoverDetails))
        verify(viewWeakReferenceMock.get())?.showProgressDialog()
        verifyNoMoreInteractions(viewWeakReferenceMock.get(), funeralCoverQuoteInteractor)
    }

    @Test
    fun shouldShowFuneralApplicationSuccessResponseScreenWhenApplicationSuccessfullySubmitted() {
        presenter.onFuneralCoverApplicationSubmitted(funeralCoverDetails)
        verify(viewWeakReferenceMock.get())?.showFuneralApplicationSuccessResponse(funeralCoverDetails)
        verify(viewWeakReferenceMock.get())?.dismissProgressDialog()
        verifyNoMoreInteractions(viewWeakReferenceMock.get())
    }

    @Test
    fun shouldShowSomethingWentWrongScreenWhenTheServiceFailed() {
        presenter.onServiceFailed()
        verify(viewWeakReferenceMock.get())?.showSomethingWentWrongScreen()
        verify(viewWeakReferenceMock.get())?.dismissProgressDialog()
        verifyNoMoreInteractions(viewWeakReferenceMock.get())
    }

/*    @Test
    fun shouldShowOnFailedToSubmitFuneralCoverApplicationScreenWhenAnApplicationFailureResponseIsReceived() {
        presenter.onFailedToSubmitFuneralCoverApplication(funeralCoverDetails)
        verify(viewWeakReferenceMock.get())?.showFuneralApplicationFailureResponse(funeralCoverDetails)
        verify(viewWeakReferenceMock.get())?.dismissProgressDialog()
        verifyNoMoreInteractions(viewWeakReferenceMock.get())
    }*/
}