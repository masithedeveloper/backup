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
package com.barclays.absa.banking.overdraft.ui

import com.barclays.absa.banking.boundary.model.overdraft.*
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicatorResponse
import com.barclays.absa.banking.framework.BaseView
import com.barclays.absa.banking.model.FicaCheckResponse
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse
import org.jetbrains.annotations.TestOnly
import styleguide.forms.SelectorList

interface OverdraftContracts {

    interface OverdraftIntroView : BaseView {
        fun navigateToApplyOverdraftStep1()
        fun navigateToAbsaWebsite()
    }

    interface OverdraftIntroPresenter {
        fun applyNowButtonClicked()
        fun onAbsaWebsiteClicked()
    }

    interface TellUsAboutYourSelfView : BaseView {
        fun navigateToNextScreen()
        fun openCalendarWhenUnderDebtReview()
        fun showInsolventAndDebtReviewReasons()
        fun showInsolventAndDebtReviewInThePastOptions()
        fun showInsolventDateSelector()
        fun hideInsolventAndDebtReviewInThePastOptions()
        fun hideDebtReviewReasonsOptions()
        fun hideInsolventDateSelector()
    }

    interface TellUsAboutYourSelfPresenter {
        fun onNextButtonClicked()
        fun onDebtCounsellingOrPendingDebtReviewOrInsolventNoSelected()
        fun onDebtCounsellingOrPendingDebtReviewOrInsolventYesSelected()
        fun onInsolventOrUnderDebtReviewYesOptionSelected()
        fun onInsolventOrUnderDebtReviewNoOptionSelected()
    }

    interface OverdraftSetupPresenter {
        fun loadCurrentAccountsFromCache()
        fun fetchMarketingConsentMethods()
        fun onFailureResponse()
    }

    interface OverdraftSetupView : BaseView {
        fun populateAccountList(accounts: SelectorList<OverdraftAccountObject>)
        fun setChequeAccount(accountNumberAndDescriptions: String, accountNumber: String, availableBalance: String)
        fun validateInputOnView()
        fun setMarketingResponse(marketingMethodResponse: PersonalInformationResponse)
    }

    interface MarketingConsentView : BaseView {
        fun navigateToOverdraftSetupConfirmationScreen()
        fun showMarketingConsentChannelOptions()
        fun hideMarketingConsentChannelOptions()
    }

    interface MarketingConsentPresenter {
        fun onNextButtonClicked()
        fun marketingConsentChecked()
        fun marketingConsentNotChecked()
    }

    interface DeclarationView : BaseView {
        fun navigateToNextScreen()
        fun navigateToFailureScreen(message: String)
    }

    interface OverdraftDeclarationPresenter {
        fun onNextButtonClicked()
        fun displayNextScreen(successResponse: FicaCheckResponse?)
    }

    interface IncomeAndExpenseView : BaseView {
        fun navigateToQuoteSummaryScreen(overdraftResponse: OverdraftResponse, overdraftQuoteSummaryResponse: OverdraftQuoteSummary)
        fun navigateToFailureScreen()
        fun navigateToPolicyDeclineFailureScreen()
        fun navigateToReferralScreen()
        fun navigateToQuoteWaitingScreen()
    }

    interface IncomeAndExpensePresenter {
        fun onIncomeAndExpenseDataReceived(response: OverdraftIncomeAndExpensesConfirmationResponse?)
        fun viewQuoteSummary(quoteSummary: OverdraftQuoteSummary?)
        fun onNextButtonClicked(detailsObject: OverdraftQuoteDetailsObject)
        fun failureToRetrieveQuote()
    }

    interface OverdraftSetupConfirmationPresenter {
        fun onNextButtonClicked(overdraftSetup: OverdraftSetup?)
        fun onIncomeAndExpenseDataReceived(overdraftResponse: OverdraftResponse?)
        fun failureResponse()
        @TestOnly
        fun setOverdraftSetup(overdraftSetup: OverdraftSetup)
    }

    interface OverdraftSetupConfirmationView : BaseView {
        fun navigateToIncomeAndExpenseScreen()
        fun navigateToFailureScreen()
        fun navigateToFailureDueScoringScreen()
    }

    interface OverdraftSummaryConfirmationPresenter {
        fun acceptOverdraftQuoteButtonClicked()
        fun rejectOverdraftQuoteButtonClicked()
        fun decideLaterButtonClicked()
        fun quoteRejectedResponse(overdraftResponse: OverdraftResponse?)
        fun quoteAcceptedResponse(overdraftResponse: OverdraftResponse?)
    }

    interface OverdraftConfirmationView : BaseView {
        fun navigateToOfferAcceptedScreen()
        fun navigateToOfferRejectedScreen()
        fun navigateToOfferPostponedScreen()
        fun navigateToFailureScreen()
        fun navigateToDirectMarketingScreen(successResponse: MarketingIndicatorResponse)
    }
}