/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.newToBank;

import android.graphics.Bitmap;
import android.view.View;

import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerGetCaseByIdResponse;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo;
import com.barclays.absa.banking.newToBank.dto.InBranchInfo;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector;
import com.barclays.absa.banking.newToBank.services.dto.CreateCombiDetails;
import com.barclays.absa.banking.newToBank.services.dto.CustomerDetails;
import com.barclays.absa.banking.newToBank.services.dto.PerformAddressLookup;
import com.barclays.absa.banking.newToBank.services.dto.PerformPhotoMatchAndMobileLookupDTO;
import com.barclays.absa.banking.newToBank.services.dto.ProofOfResidenceResponse;
import com.barclays.absa.banking.newToBank.services.dto.SiteFilteredDetailsVO;
import com.barclays.absa.banking.relationshipBanking.services.dto.FilteredListObject;

import java.util.List;

import styleguide.screens.GenericResultScreenProperties;

public interface NewToBankView extends BaseView {
    void resetKeepAliveTimer();

    void navigateToWelcomeActivity();

    void navigateToStartBankingFragment();

    void popFragmentByTag(String tag);

    void navigateToChooseAccountFragment();

    void navigateToAccountOffersFragment();

    void navigateToAccountOfferFullFragment();

    void navigateToAbsaRewardsFragment();

    void navigateToProcessingFragment();

    void navigateToSelfieFragment();

    void navigateToVerifyIdentityFragment();

    void navigateToPDFViewerFragment(String bankingMoreInfoUrl, String toolbarTitle);

    void navigateToGenericFragment(GenericResultScreenProperties genericResultScreenProperties, View.OnClickListener onClickListener);

    void navigateToGenericFragment(GenericResultScreenProperties genericResultScreenProperties, View.OnClickListener onPrimaryClickListener, View.OnClickListener onSecondaryClickListener);

    void navigateToTakeMyPhotosFragment();

    void navigateToSAIDBookOrCardFragment();

    void navigateToConfirmIdentityFragment(PerformPhotoMatchAndMobileLookupDTO performPhotoMatchAndMobileLookupDTO);

    void navigateToConfirmAddressFragment(PerformAddressLookup addressLookup);

    void navigateToIncomeAndExpensesFragment();

    void navigateToClientIncomeFragment();

    void navigateToGetBankCardFragment();

    void navigateToScanIdBookFragment();

    void navigateToScanIdCardActivity();

    void navigateToNumberConfirmationFragment();

    void navigateToNewToBankSurecheckFragment();

    void navigateToNewToBankTVNFragment();

    void navigateToNewToBankRegisteredForOnlineBankingFragment();

    void navigateToNewToBankApplicationProcessingFragment();

    void navigateBack();

    String getToolbarTitle();

    void navigateToCreatePasswordFragment();

    void navigateToSetPinFragment();

    void setToolbarTitle(String title);

    void hideToolbar();

    void showToolbar();

    void uploadSelfiePhoto(Bitmap neutralPhoto);

    void fetchCifCodes();

    void uploadIdBookPhoto(Bitmap photo, String idNumber);

    void uploadIdCardPhotos(Bitmap frontImage, Bitmap backImage, String idNumber);

    void saveCustomerDetails(CustomerDetails customerDetails);

    void setDocHandlerResponse(DocHandlerGetCaseByIdResponse docHandlerGetCaseByIdResponse);

    void saveMonthlyIncome(CodesLookupDetailsSelector monthlyIncomeRange);

    void saveRewardsData(String debitDay, boolean agreeAbsaRewards);

    void setRewardsAmount(String rewardsAmount);

    void setRewardsDateDeadline(String dateDeadline);

    void showProgressIndicator();

    NewToBankTempData getNewToBankTempData();

    void hideProgressIndicator();

    void setProgressStep(int step);

    void navigateSelectCurrentLocationFragment();

    void performSecurityNotification(String cellphoneNumber);

    void resendSecurityNotification(String cellphoneNumber);

    void setToolbarBackTitle(String title);

    void submitMyApplication(CustomerPortfolioInfo customerPortfolioInfo);

    void requestCasaRiskStatus(CustomerPortfolioInfo customerPortfolioInfo);

    void navigateToProofOfAddressFragment(ProofOfResidenceResponse successResponse);

    void getAllBranches();

    void navigateToNewToBankWelcomeToAbsaFragment(boolean showDetailsDescription);

    void navigateToSelectPreferredBranchFragment();

    void navigateToChooseBankCardFragment();

    void setListOfBranches(List<SiteFilteredDetailsVO> listOfBranches);

    void setBranchVisitedInfo(InBranchInfo inBranchInfo);

    void createCombiCardAccount(CreateCombiDetails createCombiDetails);

    void navigateToCardOrderedSuccessScreen();

    void navigateToCardOrderedSuccessFailure();

    void uploadProofOfAddress(Bitmap photo);

    void navigateToScanAddressFragment();

    void navigateToGenericResultFragment(boolean retainState, boolean isConnectionError, String description, String animation);

    void navigateToGenericResultFragment(boolean retainState, boolean isConnectionError, String description, String animation, String title);

    void navigateToGenericResultFragment(String description, String animation, String title, boolean showBranchMessage, String buttonText, View.OnClickListener onClickListener, boolean isBackEnabled);

    void setOnlineBankingPIN(String PIN);

    void setOnlineBankingPassword(String password);

    void setPINSuccessful();

    void setPasswordSuccessful();

    void fetchProofOfResidenceInfo();

    void fetchAbsaRewards();

    void forceBack();

    void sendTVNCode(String selectedValueUnmasked);

    void navigateToAbsaWebsite();

    void navigateToScoringFailure(boolean inBranch);

    void navigateToThankYouForYourApplicationScreen();

    void showSureCheckResend();

    void showExistingCustomerError();

    void showEndSessionDialogPrompt();

    void trackCurrentFragment(String fragmentName);

    void trackFragmentAction(String screenName, String actionName);

    void showStandardCardScreen();

    void navigateToApplicationSummary();

    void navigateToCasaFailureScreen(Boolean isClientInBranch);

    void nextButtonClicked(int selectedIndex);

    void navigateToChooseAccountScreen();

    void navigateToStartApplicationFragment();

    void navigateToOptionalAccountExtrasFragment();

    void navigateToChooseYourProductFragment();

    void navigateToBusinessWebsite();

    void navigateToStudentAccountBenefitsTerms();

    void navigateToStudentBenefits();

    void navigateToBusinessBankingApplicationFees();

    boolean isBusinessFlow();

    boolean isStudentFlow();

    boolean isFromBusinessAccountActivity();

    void navigateToAboutYourBusinessFragment();

    void navigateToConfirmBusinessAddressFragment();

    void navigateToSelectPreferredBusinessBankBranchFragment();

    void navigateToFilteredList(FilteredListObject filteredListObject);

    void navigateToFinancialDetailsFragment();

    void navigateToDocumentsUploadSuccessfully();

    void navigateToAddBusinessAddressFragment();

    void navigateToBusinessBankingSummaryFragment();

    void navigateToGetBusinessBankingCardFragment();

    void navigateToSureCheckRejectScreen();

    void navigateToSilverStudentWhatYouDoFragment();

    void trackSoleProprietorCurrentFragment(String fragment);

    void trackStudentAccount(String tag);
}