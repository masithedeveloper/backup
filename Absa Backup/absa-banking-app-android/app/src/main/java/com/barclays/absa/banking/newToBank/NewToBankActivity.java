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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerDocument;
import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerGetCaseByIdResponse;
import com.barclays.absa.banking.boundary.docHandler.dto.RequiredDocuments;
import com.barclays.absa.banking.businessBanking.ui.BusinessBankActivity;
import com.barclays.absa.banking.databinding.NewToBankActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo;
import com.barclays.absa.banking.newToBank.dto.InBranchInfo;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveCardPackageResponse;
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector;
import com.barclays.absa.banking.newToBank.services.dto.CreateCombiDetails;
import com.barclays.absa.banking.newToBank.services.dto.CustomerDetails;
import com.barclays.absa.banking.newToBank.services.dto.PerformAddressLookup;
import com.barclays.absa.banking.newToBank.services.dto.PerformPhotoMatchAndMobileLookupDTO;
import com.barclays.absa.banking.newToBank.services.dto.ProofOfResidenceResponse;
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations;
import com.barclays.absa.banking.newToBank.services.dto.SiteFilteredDetailsVO;
import com.barclays.absa.banking.newToBankSilverStudent.NewToBankWhatYouDoFragment;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.relationshipBanking.services.dto.FilteredListObject;
import com.barclays.absa.banking.relationshipBanking.ui.AboutYourBusinessFragment;
import com.barclays.absa.banking.relationshipBanking.ui.BusinessAddressFragment;
import com.barclays.absa.banking.relationshipBanking.ui.BusinessBankingApplicationSummaryFragment;
import com.barclays.absa.banking.relationshipBanking.ui.BusinessBankingFilteredListFragment;
import com.barclays.absa.banking.relationshipBanking.ui.BusinessBankingFinancialDetailsFragment;
import com.barclays.absa.banking.relationshipBanking.ui.BusinessBankingGetCardBranchFragment;
import com.barclays.absa.banking.relationshipBanking.ui.BusinessChooseYourProductFragment;
import com.barclays.absa.banking.relationshipBanking.ui.ChooseYourAccountFragment;
import com.barclays.absa.banking.relationshipBanking.ui.ConfirmBusinessAddressFragment;
import com.barclays.absa.banking.relationshipBanking.ui.NewToBankStartApplicationFragment;
import com.barclays.absa.banking.relationshipBanking.ui.SelectBusinessBankingBranchFragment;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.PdfUtil;
import com.barclays.absa.utils.ViewAnimation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import styleguide.screens.GenericResultScreenFragment;
import styleguide.screens.GenericResultScreenProperties;
import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.framework.utils.AppConstants.NEW_TO_BANK_FLEXI_ABSA_URL;
import static com.barclays.absa.banking.framework.utils.AppConstants.NEW_TO_BANK_GOLD_ABSA_URL;
import static com.barclays.absa.banking.framework.utils.AppConstants.NEW_TO_BANK_PREMIUM_ABSA_URL;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.SOLE_PROPRIETOR_ANALYTICS_CHANNEL;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.STUDENT_ACCOUNT_ANALYTICS_CHANNEL;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.TIM_BACK;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.TIM_FRONT;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.TIM_ID_NUMBER;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.TIM_IMAGE;
import static com.barclays.absa.utils.ImageUtils.convertToByteArrayForNewToBank;

public class NewToBankActivity extends BaseActivity implements NewToBankView {

    private static final String SELFIE_UPLOAD_DEFINITION_ID = "7c513d7b-9357-4e3c-a4bb-8817c8b784c3";
    private static final String UPLOADED_PROOF_OF_ADDRESS_DEFINITION_ID = "795ca61e-f9e3-443d-ba58-82a94957276d";
    private static final String GREEN_BAR_CODED_ID_DEFINITION_ID = "ac0969b1-6d70-490e-af5a-0169826dc77f";
    private static final String SOUTH_AFRICA_SMART_ID_CARD_FRONT_IMAGE_UPLOAD_DEFINITION_ID = "2b6adc4f-3abf-4ac8-a313-c7667643a481";
    private static final String SOUTH_AFRICA_SMART_ID_CARD_REAR_IMAGE_UPLOAD_DEFINITION_ID = "2d81b4b2-1eaf-4281-b0d1-931e8299170d";

    private static final String SEARCH_PROOF_OF_ADDRESS = "Proof of address";
    private static final String SEARCH_SELFIE = "Selfie";
    private static final String SEARCH_PROOF_OF_IDENTITY = "Proof of Identity";

    public static final String NEW_TO_BANK_TEMP_DATA_EXTRAS = "NewToBankTempData";

    private static final int ID_BOOK_REQUEST_CODE = 00004;
    private static final int ID_CARD_REQUEST_CODE = 00005;
    private static final int ANIMATION_DURATION = 500;
    private static final int PLAY_SERVICES_REQUEST = 19720;
    private static final int PERSONAL_ACCOUNT_REQUEST = 600;

    private NewToBankActivityBinding newToBankBinding;
    private NewToBankPresenter newToBankPresenter;

    private NewToBankTempData newToBankTempData;
    private ViewAnimation toolbarAnimation;

    private NewToBankApplicationProcessingFragment newToBankApplicationProcessingFragment;
    private NewToBankSurecheckFragment newToBankSurecheckFragment;
    private NewToBankAccountOfferFullFragment accountOfferFullFragment;

    private boolean hasUserInteracted;
    private static CountDownTimer sessionCountDownTimer;
    private boolean isResultFragmentBackEnabled;
    private boolean isBusinessFlow;
    private boolean isStudentFlow;
    private boolean isFromBusinessAccountActivity;
    private FilteredListObject filteredListObject;

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        hasUserInteracted = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newToBankBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.new_to_bank_activity, null, false);
        setContentView(newToBankBinding.getRoot());
        Toolbar toolbar = (Toolbar) newToBankBinding.toolbar.toolbar;
        toolbarAnimation = new ViewAnimation(toolbar);

        startSessionTimer();
        setSupportActionBar(toolbar);

        newToBankTempData = new NewToBankTempData();
        newToBankPresenter = new NewToBankPresenter(this);
        newToBankPresenter.performGetAllConfigsForApplication();

        final Intent intent = getIntent();
        if (intent != null) {
            isFromBusinessAccountActivity = BusinessBankActivity.class.getSimpleName().equals(intent.getStringExtra(BusinessBankActivity.CALLING_ACTIVITY));
            BusinessEvolveCardPackageResponse cardPackage = (BusinessEvolveCardPackageResponse) intent.getSerializableExtra(BusinessBankActivity.SELECTED_PACKAGE);
            String productType = intent.getStringExtra(BusinessBankActivity.PRODUCT_TYPE);
            if (isFromBusinessAccountActivity && cardPackage != null && productType != null) {
                newToBankTempData.setSelectedBusinessEvolvePackage(cardPackage);
                newToBankTempData.setProductType(productType);
                isBusinessFlow = true;

                if (BMBApplication.getInstance().getUserLoggedInStatus()) {
                    navigateToChooseYourProductFragment();
                } else if (intent.getExtras() != null && intent.hasExtra(NEW_TO_BANK_TEMP_DATA_EXTRAS)) {
                    newToBankTempData = new Gson().fromJson(intent.getStringExtra(NEW_TO_BANK_TEMP_DATA_EXTRAS), NewToBankTempData.class);
                    navigateToStartApplicationFragment();
                }
            } else {
                navigateToStartBankingFragment();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGooglePlayServicesVersion();
    }

    private void checkGooglePlayServicesVersion() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(result)) {
                googleApiAvailability.getErrorDialog(this, result, PLAY_SERVICES_REQUEST, dialog -> finish()).show();
            }
        }
    }

    private void sessionCheck() {
        if (hasUserInteracted) {
            newToBankPresenter.performKeepAlive();
            hasUserInteracted = false;
        } else {
            finish();
        }
    }

    @Override
    public void resetKeepAliveTimer() {
        sessionCountDownTimer.start();
    }

    private void startSessionTimer() {
        if (sessionCountDownTimer != null) {
            sessionCountDownTimer.cancel();
        }

        sessionCountDownTimer = new CountDownTimer(TimeUnit.MINUTES.toMillis(getResources().getInteger(R.integer.timer_duration_in_mins)), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // BMBLogger.d("KEEP ALIVE", String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                sessionCheck();
            }
        }.start();
    }

    boolean isBackVisible() {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            return (actionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0;
        }
        return true;
    }

    public void showApplicationCancellationDialog() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.business_banking_cancel_dialog_title))
                .message(getString(R.string.business_banking_cancel_dialog_message))
                .positiveDismissListener((dialog, which) -> {
                    goToLaunchScreen(this);
                }));
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() instanceof NewToBankStartApplicationFragment && isFromBusinessAccountActivity && !BMBApplication.getInstance().getUserLoggedInStatus()) {
            showApplicationCancellationDialog();
            return;
        }

        if (getCurrentFragment() instanceof NewToBankAccountOfferFullFragment || getCurrentFragment() instanceof NewToBankAccountOffersFragment) {
            super.onBackPressed();
            setToolbarBackTitle(getString(R.string.new_to_bank_choose_account));
            showToolbar();
        } else if (getCurrentFragment() instanceof NewToBankSelfieFragment || getCurrentFragment() instanceof BusinessAddressFragment) {
            super.onBackPressed();
            return;
        } else if (getCurrentFragment() instanceof GenericResultScreenFragment) {
            if (isResultFragmentBackEnabled) {
                isResultFragmentBackEnabled = false;
                super.onBackPressed();
            } else {
                showEndSessionDialogPrompt();
            }
            return;
        } else if (!isBackVisible() && getCurrentFragment() instanceof NewToBankApplicationProcessingFragment || getCurrentFragment() instanceof NewToBankWelcomeToAbsaFragment || getCurrentFragment() instanceof BusinessBankingApplicationSummaryFragment) {
            showEndSessionDialogPrompt();
            return;
        } else if (getCurrentFragment() instanceof SelectBusinessBankingBranchFragment) {
            newToBankBinding.progressIndicatorView.setNextStep(7);
            navigateToConfirmBusinessAddressFragment();
        } else if (getCurrentFragment() instanceof BusinessBankingFilteredListFragment) {
            Intent intent = new Intent();
            filteredListObject.getCallingFragment().onActivityResult(filteredListObject.getNormalInputViewRequestCode(), RESULT_CANCELED, intent);
            super.onBackPressed();
            return;
        } else {
            super.onBackPressed();
        }

        int FIFTH_SCREEN = 5;
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            finish();
        } else if (getSupportFragmentManager().getBackStackEntryCount() < FIFTH_SCREEN) {
            newToBankBinding.progressIndicatorView.setVisibility(View.GONE);
        } else {
            newToBankBinding.progressIndicatorView.goBackStep();
        }
    }

    @Override
    public void navigateToWelcomeActivity() {
        Intent intent = new Intent(NewToBankActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void navigateToStartBankingFragment() {
        NewToBankStartBankingFragment startBankingFragment = NewToBankStartBankingFragment.newInstance();
        startFragment(startBankingFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToNewToBankWelcomeToAbsaFragment(boolean showDetailsDescription) {
        if (newToBankApplicationProcessingFragment != null) {
            newToBankApplicationProcessingFragment.showFireWorks();
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            NewToBankWelcomeToAbsaFragment newToBankWelcomeToAbsaFragment = NewToBankWelcomeToAbsaFragment.newInstance(showDetailsDescription);
            startFragment(newToBankWelcomeToAbsaFragment, true, AnimationType.FADE);
        }, 4000);
    }

    @Override
    public void navigateToCreatePasswordFragment() {
        NewToBankCreatePasswordFragment newToBankCreatePasswordFragment = NewToBankCreatePasswordFragment.newInstance();
        startFragment(newToBankCreatePasswordFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToSetPinFragment() {
        NewToBankSetPinFragment newToBankSetPinFragment = NewToBankSetPinFragment.newInstance();
        startFragment(newToBankSetPinFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToGetBankCardFragment() {
        NewToBankGetBankCardFragment newToBankGetBankCardFragment = NewToBankGetBankCardFragment.newInstance();
        startFragment(newToBankGetBankCardFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToSelectPreferredBranchFragment() {
        if (newToBankTempData.getListOfBranches() == null) {
            newToBankPresenter.performGetFilteredSiteDetails("");
        } else {
            NewToBankSelectPreferredBranchFragment newToBankSelectPreferredBrachFragment = NewToBankSelectPreferredBranchFragment.newInstance();
            startFragment(newToBankSelectPreferredBrachFragment, true, AnimationType.FADE);
        }
    }

    @Override
    public void navigateToChooseBankCardFragment() {
        NewToBankChooseBankCardFragment newToBankChooseBankCardFragment = NewToBankChooseBankCardFragment.newInstance();
        startFragment(newToBankChooseBankCardFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToClientIncomeFragment() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        NewToBankClientIncomeFragment newToBankClientIncomeFragment = NewToBankClientIncomeFragment.Companion.newInstance();
        startFragment(newToBankClientIncomeFragment, false, AnimationType.FADE);
    }

    @Override
    public void navigateToProofOfAddressFragment(ProofOfResidenceResponse successResponse) {
        newToBankTempData.setProofOfResidenceInfo(successResponse);
        NewToBankProofOfAddressFragment newToBankProofOfAddressFragment = NewToBankProofOfAddressFragment.newInstance();
        startFragment(newToBankProofOfAddressFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToIncomeAndExpensesFragment() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        NewToBankIncomeAndExpensesFragment newToBankIncomeAndExpensesFragment = NewToBankIncomeAndExpensesFragment.newInstance();
        startFragment(newToBankIncomeAndExpensesFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToVerifyIdentityFragment() {
        newToBankBinding.progressIndicatorView.setVisibility(View.VISIBLE);
        newToBankBinding.progressIndicatorView.setNextStepNoAnimation(1);
        NewToBankVerifyIdentityFragment newToBankVerifyIdentityFragment = NewToBankVerifyIdentityFragment.newInstance();
        startFragment(newToBankVerifyIdentityFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToConfirmIdentityFragment(PerformPhotoMatchAndMobileLookupDTO performPhotoMatchAndMobileLookupDTO) {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        this.newToBankTempData.setPhotoMatchAndMobileLookupResponse(performPhotoMatchAndMobileLookupDTO);
        NewToBankConfirmIdentityFragment newToBankConfirmIdentityFragment = NewToBankConfirmIdentityFragment.newInstance();
        startFragment(newToBankConfirmIdentityFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToPDFViewerFragment(String url, String toolbarTitle) {
        PdfUtil.INSTANCE.showPDFInApp(this, url);
    }

    @Override
    public void navigateToGenericFragment(GenericResultScreenProperties genericResultScreenProperties, View.OnClickListener onClickListener) {
        GenericResultScreenFragment genericResultsScreenFragment = GenericResultScreenFragment.newInstance(genericResultScreenProperties, false, onClickListener, onClickListener);
        startFragment(genericResultsScreenFragment, true, AnimationType.FADE);
        hideToolbar();
    }

    @Override
    public void navigateToGenericFragment(GenericResultScreenProperties genericResultScreenProperties, View.OnClickListener onPrimaryClickListener, View.OnClickListener onSecondaryClickListener) {
        GenericResultScreenFragment genericResultsScreenFragment = GenericResultScreenFragment.newInstance(genericResultScreenProperties, false, onPrimaryClickListener, onSecondaryClickListener);
        startFragment(genericResultsScreenFragment, true, AnimationType.FADE);
        hideToolbar();
    }

    @Override
    public void navigateToThankYouForYourApplicationScreen() {
        if (newToBankApplicationProcessingFragment != null) {
            newToBankApplicationProcessingFragment.showFireWorks();
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                    .setResultScreenAnimation(ResultAnimations.generalSuccess)
                    .setTitle(getString(R.string.new_to_bank_thank_you_for_your_application))
                    .setDescription(getString(R.string.new_to_bank_we_will_contact_you_within_3_hours))
                    .setNoteText(getString(R.string.new_to_bank_business_hours_only))
                    .setPrimaryButtonLabel(getString(R.string.done))
                    .setSecondaryButtonLabel(getString(R.string.new_to_bank_contact_us))
                    .build(true);
            trackCurrentFragment(NewToBankConstants.SIGNUP_RESULT_REFERRAL);
            navigateToGenericFragment(resultScreenProperties, v -> finish(), v -> TelephoneUtil.supportApplyOverdraftDivision(this));
        }, 4000);
    }

    @Override
    public void navigateToCardOrderedSuccessScreen() {
        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.new_to_bank_card_has_been_ordered))
                .setDescription(getString(R.string.new_to_bank_you_will_receive_sms))
                .setSecondaryButtonLabel(getString(R.string.new_to_bank_set_up_my_digital_profile))
                .build(true);

        GenericResultScreenFragment genericResultsScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, false, null, v -> navigateToSetPinFragment());
        trackCurrentFragment(NewToBankConstants.BANKING_CARD_ORDER_SUCCESS);
        startFragment(genericResultsScreenFragment, true, AnimationType.FADE);
        hideToolbar();
    }

    @Override
    public void navigateToCardOrderedSuccessFailure() {
        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.new_to_bank_unable_to_order_your_card))
                .setDescription(getString(R.string.new_to_bank_unable_to_order_your_card_description))
                .setContactViewContactName(getString(R.string.new_to_bank_contact_centre))
                .setContactViewContactNumber(getString(R.string.new_to_bank_contact_centre_number))
                .setSecondaryButtonLabel(getString(R.string.close))
                .build(true);

        View.OnClickListener onClickListener = v -> navigateToWelcomeActivity();

        GenericResultScreenFragment genericResultsScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, false, null, onClickListener);
        trackCurrentFragment(NewToBankConstants.BANKING_CARD_ORDER_FAIL);
        startFragment(genericResultsScreenFragment, true, AnimationType.FADE);
        hideToolbar();
    }

    @Override
    public void navigateToScoringFailure(boolean inBranch) {
        GenericResultScreenProperties resultScreenProperties;
        GenericResultScreenProperties.PropertiesBuilder propertiesBuilder = new GenericResultScreenProperties.PropertiesBuilder();
        if (inBranch) {
            propertiesBuilder
                    .setDescription(getString(R.string.new_to_bank_application_was_unsuccessful_in_branch));
        } else {
            propertiesBuilder
                    .setDescription(getString(R.string.new_to_bank_application_was_unsuccessful));
        }

        resultScreenProperties = propertiesBuilder
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.new_to_bank_unsuccessful))
                .setContactViewContactName(getString(R.string.new_to_bank_contact_centre))
                .setContactViewContactNumber(getString(R.string.new_to_bank_contact_centre_number))
                .setSecondaryButtonLabel(getString(R.string.done))
                .build(true);

        GenericResultScreenFragment genericResultsScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, false, null, v -> finish());
        trackCurrentFragment(NewToBankConstants.SIGNUP_RESULT_UNSUCCESSFUL);
        startFragment(genericResultsScreenFragment, true, AnimationType.FADE);
        hideToolbar();
    }

    @Override
    public void showSureCheckResend() {
        if (newToBankSurecheckFragment != null && newToBankSurecheckFragment.isAdded()) {
            newToBankSurecheckFragment.stopTimer();
            newToBankSurecheckFragment.displayResendOption();
        }
    }

    @Override
    public void showExistingCustomerError() {
        String inBranch = newToBankTempData.getInBranchInfo().getInBranchIndicator() ? getString(R.string.new_to_bank_speak_to_branch_consultant_generic) : getString(R.string.new_to_bank_visit_nearest_branch_generic);
        if (isBusinessFlow) {
            trackSoleProprietorCurrentFragment("SoleProprietor_AlreadyAnAbsaCustomerErrorScreen_ScreenDisplayed");
        } else if (isStudentFlow) {
            trackStudentAccount("StudentAccount_AlreadyAnAbsaCustomerErrorScreen_ScreenDisplayed");
        } else {
            trackCurrentFragment(NewToBankConstants.IDENTITY_SCREEN_EXISTING_CUSTOMER_ERROR);
        }
        String buttonText = newToBankTempData.getInBranchInfo().getInBranchIndicator() ? getString(R.string.close) : getString(R.string.new_to_bank_open_online_application);

        View.OnClickListener onClickListener;
        if (newToBankTempData.getInBranchInfo().getInBranchIndicator()) {
            onClickListener = v -> navigateToWelcomeActivity();
        } else {
            onClickListener = v -> {
                String url = NEW_TO_BANK_FLEXI_ABSA_URL;
                if (newToBankTempData.getSelectedPackage() != null) {
                    final String PREMIUM_BANKING = "Premium Banking";
                    final String GOLD_BUNDLE = "Gold Value Bundle";

                    if (PREMIUM_BANKING.equalsIgnoreCase(newToBankTempData.getSelectedPackage().getPackageName())) {
                        url = NEW_TO_BANK_PREMIUM_ABSA_URL;
                    } else if (GOLD_BUNDLE.equalsIgnoreCase(newToBankTempData.getSelectedPackage().getPackageName())) {
                        url = NEW_TO_BANK_GOLD_ABSA_URL;
                    }
                }

                finish();
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
                startActivityIfAvailable(intent);
            };
        }

        navigateToGenericResultFragment(getString(R.string.new_to_bank_cannot_continue_error, inBranch),
                ResultAnimations.generalFailure, getString(R.string.new_to_bank_already_absa_customer),
                false, buttonText, onClickListener, false);
    }

    @Override
    public void navigateToTakeMyPhotosFragment() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        NewToBankTakeMyPhotosFragment newToBankTakeMyPhotosFragment = NewToBankTakeMyPhotosFragment.Companion.newInstance();
        startFragment(newToBankTakeMyPhotosFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToSAIDBookOrCardFragment() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        NewToBankSAIDBookOrCardFragment newToBankSaIdBookOrCardFragment = NewToBankSAIDBookOrCardFragment.newInstance();
        startFragment(newToBankSaIdBookOrCardFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToConfirmAddressFragment(PerformAddressLookup addressLookup) {
        newToBankTempData.setAddressLookupDetails(addressLookup);
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        NewToBankConfirmAddressFragment newToBankConfirmAddressFragment = NewToBankConfirmAddressFragment.newInstance();
        startFragment(newToBankConfirmAddressFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToScanIdBookFragment() {
        Intent intent = new Intent(this, NewToBankScanIDBookActivity.class);
        startActivityForResult(intent, ID_BOOK_REQUEST_CODE);
    }

    @Override
    public void navigateToScanIdCardActivity() {
        Intent intent = new Intent(this, NewToBankScanIDCardActivity.class);
        startActivityForResult(intent, ID_CARD_REQUEST_CODE);
    }

    @Override
    public void navigateToNumberConfirmationFragment() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        NewToBankNumberConfirmationFragment numberConfirmationFragment = NewToBankNumberConfirmationFragment.newInstance();
        startFragment(numberConfirmationFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToNewToBankSurecheckFragment() {
        newToBankSurecheckFragment = NewToBankSurecheckFragment.newInstance();
        startFragment(newToBankSurecheckFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToNewToBankTVNFragment() {
        NewToBankTVNFragment newToBankTVNFragment = NewToBankTVNFragment.newInstance();
        startFragment(newToBankTVNFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToNewToBankRegisteredForOnlineBankingFragment() {
        NewToBankRegisteredForOnlineBankingFragment newToBankRegisteredForOnlineBankingFragment = NewToBankRegisteredForOnlineBankingFragment.newInstance();
        startFragment(newToBankRegisteredForOnlineBankingFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToNewToBankApplicationProcessingFragment() {
        newToBankApplicationProcessingFragment = NewToBankApplicationProcessingFragment.newInstance();
        startFragment(newToBankApplicationProcessingFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateBack() {
        onBackPressed();
    }

    @Override
    public void popFragmentByTag(String tag) {
        getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void navigateSelectCurrentLocationFragment() {
        NewToBankSelectCurrentLocationFragment selectCurrentLocationFragment = NewToBankSelectCurrentLocationFragment.newInstance();
        startFragment(selectCurrentLocationFragment, true, AnimationType.FADE);
    }

    @Override
    public void performSecurityNotification(String cellphoneNumber) {
        newToBankPresenter.requestSecurityNotification(cellphoneNumber);
    }

    @Override
    public void resendSecurityNotification(String cellphoneNumber) {
        newToBankPresenter.resendSecurityNotification(cellphoneNumber);
    }

    @Override
    public void navigateToChooseAccountFragment() {
        NewToBankChooseAccountFragment chooseAccountFragment = NewToBankChooseAccountFragment.newInstance();
        startFragment(chooseAccountFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToAccountOffersFragment() {
        NewToBankAccountOffersFragment accountOffersFragment = NewToBankAccountOffersFragment.newInstance();
        startFragment(accountOffersFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToAccountOfferFullFragment() {
        showProgressDialog();
        accountOfferFullFragment = NewToBankAccountOfferFullFragment.newInstance();
        startFragment(accountOfferFullFragment, false, AnimationType.FADE);
    }

    @Override
    public void navigateToAbsaRewardsFragment() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        NewToBankAbsaRewardsFragment absaRewardsFragment = NewToBankAbsaRewardsFragment.newInstance();
        startFragment(absaRewardsFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToScanAddressFragment() {
        NewToBankScanAddressFragment scanAddressFragment = NewToBankScanAddressFragment.newInstance();
        startFragment(scanAddressFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToProcessingFragment() {
        NewToBankProcessingFragment processingFragment = NewToBankProcessingFragment.newInstance();
        startFragment(processingFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToSelfieFragment() {
        if (isBusinessFlow || isStudentFlow) {
            newToBankPresenter.fetchSourceOfIncomeList();
        }
        NewToBankSelfieFragment selfieFragment = NewToBankSelfieFragment.newInstance();
        startFragment(selfieFragment, true, AnimationType.FADE);
    }

    @Override
    public void uploadSelfiePhoto(Bitmap neutralPhoto) {
        hideToolbar();
        DocHandlerDocument docHandlerSelfieDocument = createDocHandlerDocument(SEARCH_SELFIE, SELFIE_UPLOAD_DEFINITION_ID, neutralPhoto);
        newToBankPresenter.uploadSelfieToDocHandler(docHandlerSelfieDocument);
    }

    @Override
    public void uploadIdBookPhoto(Bitmap photo, String idNumber) {
        DocHandlerDocument docHandlerIdBookDocument = createDocHandlerDocument(SEARCH_PROOF_OF_IDENTITY, GREEN_BAR_CODED_ID_DEFINITION_ID, photo);
        newToBankPresenter.uploadIdBookToDocHandler(docHandlerIdBookDocument, idNumber);
    }

    @Override
    public void uploadIdCardPhotos(Bitmap frontImage, Bitmap backImage, String idNumber) {
        DocHandlerDocument docHandlerIdCardDocumentFront = createDocHandlerDocument(SEARCH_PROOF_OF_IDENTITY, SOUTH_AFRICA_SMART_ID_CARD_FRONT_IMAGE_UPLOAD_DEFINITION_ID, frontImage);
        DocHandlerDocument docHandlerIdCardDocumentBack = createDocHandlerDocument(SEARCH_PROOF_OF_IDENTITY, SOUTH_AFRICA_SMART_ID_CARD_REAR_IMAGE_UPLOAD_DEFINITION_ID, backImage);
        newToBankPresenter.uploadIdCardToDocHandler(docHandlerIdCardDocumentFront, docHandlerIdCardDocumentBack, idNumber);
    }

    @Override
    public void fetchCifCodes() {
        newToBankPresenter.fetchCifCodes();
    }

    @Override
    public void saveCustomerDetails(CustomerDetails customerDetails) {
        newToBankTempData.setCustomerDetails(customerDetails);
        newToBankPresenter.performValidateCustomerAndCreateCase(customerDetails);
    }

    @Override
    public void setDocHandlerResponse(DocHandlerGetCaseByIdResponse docHandlerGetCaseByIdResponse) {
        newToBankTempData.setDocHandlerGetCaseByIdResponse(docHandlerGetCaseByIdResponse);
    }

    @Override
    public void saveMonthlyIncome(CodesLookupDetailsSelector monthlyIncomeSelector) {
        newToBankTempData.setSelectedMonthlyIncomeCode(monthlyIncomeSelector.getItemCode());
        newToBankPresenter.fetchCardBundles();
    }

    @Override
    public void setRewardsAmount(String rewardsAmount) {
        newToBankTempData.setRewardsAmount(rewardsAmount);
    }

    @Override
    public void setRewardsDateDeadline(String dateDeadline) {
        try {
            if (DateUtils.getDate(dateDeadline, new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)).after(new Date())) {
                newToBankTempData.setRewardsDateValid(true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        newToBankTempData.setRewardsDateDeadline(dateDeadline);
    }

    @Override
    public void saveRewardsData(String debitDay, boolean agreeAbsaRewards) {
        newToBankTempData.setDebitDay(debitDay);
        newToBankTempData.setAgreeAbsaRewards(agreeAbsaRewards);
        newToBankPresenter.onRewardsNextButtonClick();
    }

    @Override
    public void createCombiCardAccount(CreateCombiDetails createCombiDetails) {
        createCombiDetails.setInBranchSite(newToBankTempData.getInBranchInfo().getInBranchCode());
        createCombiDetails.setInBranchIndicator(newToBankTempData.getInBranchInfo().getInBranchIndicator());
        createCombiDetails.setInBranchName(newToBankTempData.getInBranchInfo().getInBranchName());
        final BMBApplication app = BMBApplication.getInstance();
        try {
            createCombiDetails.setTokenNumber(app.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        newToBankPresenter.performCreateCombiCardAccount(createCombiDetails);
    }

    @Override
    public void setToolbarTitle(String title) {
        setToolBar(StringExtensions.toTitleCase(title), view -> onBackPressed());
    }

    @Override
    public void setToolbarBackTitle(String title) {
        setToolBarBack(StringExtensions.toTitleCase(title), view -> onBackPressed());
    }

    @Override
    public String getToolbarTitle() {
        if (getSupportActionBar() != null && getSupportActionBar().getTitle() != null) {
            return getSupportActionBar().getTitle().toString();
        }
        return "";
    }

    @Override
    public void hideToolbar() {
        if (newToBankBinding.toolbar.toolbar.getVisibility() != View.GONE) {
            toolbarAnimation.collapseView(ANIMATION_DURATION);
        }
        newToBankBinding.progressIndicatorView.setVisibility(View.GONE);
    }

    @Override
    public void showToolbar() {
        if (newToBankBinding.toolbar.toolbar.getVisibility() != View.VISIBLE) {
            toolbarAnimation.expandView(ANIMATION_DURATION);
        }
    }

    @Override
    public void showProgressIndicator() {
        newToBankBinding.progressIndicatorView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel_menu_item:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void navigateToAbsaWebsite() {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.absa.co.za"));
        startActivityIfAvailable(intent);
    }

    private DocHandlerDocument createDocHandlerDocument(String search, String subTypeId, Bitmap bitmap) {
        RequiredDocuments[] requiredDocuments = newToBankTempData.getDocHandlerGetCaseByIdResponse().getRequiredDocuments();
        for (RequiredDocuments doc : requiredDocuments) {
            if (doc.getDescription().equalsIgnoreCase(search.toLowerCase())) {
                final DocHandlerDocument docHandlerDocument = new DocHandlerDocument();
                docHandlerDocument.setCaseId(newToBankTempData.getDocHandlerGetCaseByIdResponse().getCaseId());
                docHandlerDocument.setDocId(doc.getDocumentTypeId());
                docHandlerDocument.setDocSubType(subTypeId);
                docHandlerDocument.setDescription(search);
                docHandlerDocument.setFileName(search + ".jpg");

                if (isBusinessFlow && search.equals(SEARCH_PROOF_OF_ADDRESS)) {
                    docHandlerDocument.setUploadDocument(convertToByteArrayForNewToBank(bitmap));
                } else {
                    docHandlerDocument.setUploadImage(bitmap);
                }

                return docHandlerDocument;
            }
        }
        return null;
    }

    @Override
    public NewToBankTempData getNewToBankTempData() {
        return newToBankTempData;
    }

    @Override
    public void hideProgressIndicator() {
        newToBankBinding.progressIndicatorView.setVisibility(View.GONE);
    }

    @Override
    public void setProgressStep(int step) {
        newToBankBinding.progressIndicatorView.setVisibility(View.VISIBLE);
        newToBankBinding.progressIndicatorView.setNextStep(step);
    }

    @Override
    public void submitMyApplication(CustomerPortfolioInfo customerPortfolioInfo) {
        newToBankPresenter.createCustomerPortfolio(customerPortfolioInfo);
        navigateToNewToBankApplicationProcessingFragment();
    }

    @Override
    public void requestCasaRiskStatus(CustomerPortfolioInfo customerPortfolioInfo) {
        newToBankPresenter.performCasaRiskProfiling(customerPortfolioInfo);
    }

    @Override
    public void getAllBranches() {
        if (newToBankTempData.getListOfBranches() == null || newToBankTempData.getListOfBranches().size() == 0) {
            newToBankPresenter.performGetFilteredSiteDetails("");
        }
    }

    @Override
    public void setListOfBranches(List<SiteFilteredDetailsVO> listOfBranches) {
        newToBankTempData.setListOfBranches(listOfBranches);
    }

    @Override
    public void setBranchVisitedInfo(InBranchInfo inBranchInfo) {
        if (newToBankTempData != null && inBranchInfo != null) {
            newToBankTempData.setInBranchInfo(inBranchInfo);
        }
    }

    @Override
    public void uploadProofOfAddress(Bitmap photo) {
        showProgressDialog();
        DocHandlerDocument docHandlerIdBookDocument = createDocHandlerDocument(SEARCH_PROOF_OF_ADDRESS, UPLOADED_PROOF_OF_ADDRESS_DEFINITION_ID, photo);
        newToBankPresenter.uploadProofOfResidenceToDocHandler(docHandlerIdBookDocument);
    }

    @Override
    public void navigateToGenericResultFragment(boolean retainState, boolean isConnectionError, String description, String animation) {
        if (isConnectionError) {
            navigateToGenericResultFragment(retainState, isConnectionError, description, animation, getString(R.string.new_to_bank_landing_connection_error_header));
        } else {
            navigateToGenericResultFragment(retainState, isConnectionError, description, animation, getString(R.string.new_to_bank_generic_header_unable_to_continue));
        }
    }

    @Override
    public void navigateToGenericResultFragment(boolean retainState, boolean isConnectionError, String description, String animation, String title) {
        View.OnClickListener onClickListener;
        onClickListener = retainState ? v -> super.onBackPressed() : v -> navigateToWelcomeActivity();
        navigateToGenericResultFragment(description, animation, title, true, getString(R.string.close), onClickListener, retainState);
    }

    @Override
    public void navigateToGenericResultFragment(String description, String animation, String title, boolean showBranchMessage, String buttonText, View.OnClickListener onClickListener, boolean isBackEnabled) {
        hideToolbar();
        hideProgressIndicator();
        this.isResultFragmentBackEnabled = isBackEnabled;

        if (showBranchMessage) {
            String inBranch = newToBankTempData.getInBranchInfo().getInBranchIndicator() ? getString(R.string.new_to_bank_speak_to_branch_consultant_generic) : getString(R.string.new_to_bank_visit_nearest_branch_generic);
            description = description.endsWith(".") ? String.format("%s %s", description, inBranch) : String.format("%s. %s", description, inBranch);
        }

        GenericResultScreenProperties.PropertiesBuilder builder = new GenericResultScreenProperties.PropertiesBuilder();
        builder.setDescription(description);
        builder.setPrimaryButtonLabel(buttonText);
        builder.setResultScreenAnimation(animation);
        builder.setContactViewContactName(getString(R.string.new_to_bank_contact_centre));
        builder.setContactViewContactNumber(getString(R.string.new_to_bank_contact_centre_number));
        builder.setTitle(title);

        GenericResultScreenFragment screenFragment = GenericResultScreenFragment.newInstance(builder.build(showBranchMessage), false, onClickListener, null);
        startFragment(screenFragment, true, AnimationType.FADE);
    }

    @Override
    public void setOnlineBankingPIN(String PIN) {
        String clientTypeGroup = isBusinessFlow ? "SOLE_TRADER" : "INDIVIDUALS";
        newToBankTempData.getRegistrationDetails().setPIN(PIN);
        newToBankPresenter.setOnlineBankingPIN(newToBankTempData.getCustomerDetails().getIdNumber(), PIN, clientTypeGroup);
    }

    @Override
    public void setOnlineBankingPassword(String password) {
        newToBankTempData.getRegistrationDetails().setPassword(password);
        newToBankPresenter.setOnlineBankingPassword(password);
    }

    @Override
    public void setPINSuccessful() {
        hideToolbar();
        navigateToGenericResultFragment("", ResultAnimations.generalSuccess,
                getString(R.string.new_to_bank_digital_pin_set), false,
                getString(R.string.new_to_bank_create_password), v -> navigateToCreatePasswordFragment(), false);
    }

    @Override
    public void setPasswordSuccessful() {
        hideToolbar();
        navigateToGenericResultFragment("", ResultAnimations.generalSuccess,
                getString(R.string.new_to_bank_digital_password_set), false,
                getString(R.string.next), v -> navigateToNewToBankRegisteredForOnlineBankingFragment(), false);
    }

    @Override
    public void fetchProofOfResidenceInfo() {
        newToBankPresenter.fetchProofOfResidenceInfo();
    }

    @Override
    public void fetchAbsaRewards() {
        newToBankPresenter.fetchAbsaRewards(newToBankTempData.getRewardsDateValid());
    }

    @Override
    public void forceBack() {
        super.onBackPressed();
    }

    @Override
    public void sendTVNCode(String TVN) {
        showProgressDialog();
        newToBankPresenter.validateSecurityNotification(getNewToBankTempData().getCustomerDetails().getCellphoneNumber(), TVN);
    }

    @Override
    public void checkDeviceState() {
        navigateToGenericResultFragment(false, true, getString(R.string.data_or_connection_description), ResultAnimations.generalAlert);
    }

    @Override
    public void showEndSessionDialogPrompt() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.new_to_bank_are_you_sure))
                .message(getString(R.string.new_to_bank_if_you_go_back))
                .positiveDismissListener((dialog, which) -> finish()));
    }

    @Override
    public void trackCurrentFragment(String fragmentName) {
        AnalyticsUtils.getInstance().trackCustomScreen(fragmentName, NewToBankConstants.ANALYTICS_CHANNEL, BMBConstants.TRUE_CONST);
    }

    @Override
    public void trackFragmentAction(String fragmentName, String actionName) {
        AnalyticsUtils.getInstance().trackAppActionStart(fragmentName, actionName, BMBConstants.TRUE_CONST);
    }

    @Override
    public void showStandardCardScreen() {
        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.new_to_bank_thank_you))
                .setDescription(getString(R.string.new_to_bank_please_speak_to_consultant))
                .setSecondaryButtonLabel(getString(R.string.close))
                .build(true);

        trackCurrentFragment(NewToBankConstants.BANKING_STANDARD_CARD_SELECTION);
        navigateToGenericFragment(resultScreenProperties, v -> finish());
    }

    @Override
    public void navigateToApplicationSummary() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        NewToBankSubmitApplicationSummaryFragment applicationSummaryFragment = NewToBankSubmitApplicationSummaryFragment.newInstance();
        startFragment(applicationSummaryFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToCasaFailureScreen(Boolean isClientInBranch) {
        GenericResultScreenProperties resultScreenProperties;
        GenericResultScreenProperties.PropertiesBuilder propertiesBuilder = new GenericResultScreenProperties.PropertiesBuilder();
        if (isClientInBranch) {
            propertiesBuilder
                    .setDescription(getString(R.string.new_to_bank_unable_to_process_in_branch));
        } else {
            propertiesBuilder
                    .setDescription(getString(R.string.new_to_bank_unable_to_process_not_in_branch));
        }

        resultScreenProperties = propertiesBuilder
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.new_to_bank_unable_to_process_application))
                .setSecondaryButtonLabel(getString(R.string.close))
                .setContactViewContactName(getString(R.string.new_to_bank_contact_centre))
                .setContactViewContactNumber(getString(R.string.new_to_bank_contact_centre_number))
                .build(true);

        trackCurrentFragment(NewToBankConstants.BANKING_STANDARD_CARD_SELECTION);
        navigateToGenericFragment(resultScreenProperties, v -> finish());
    }

    @Override
    public void nextButtonClicked(int selectedIndex) {
        final FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        isBusinessFlow = false;
        isStudentFlow = false;

        switch (selectedIndex) {
            case 0:
                if (featureSwitchingToggles.getNtbRegistration() == FeatureSwitchingStates.ACTIVE.getKey()) {
                    navigateSelectCurrentLocationFragment();
                } else {
                    capabilityUnavailable(R.string.feature_unavailable_message_new_to_bank);
                }
                break;
            case 1:
                boolean soleProprietorRegistrationAvailable = featureSwitchingToggles.getSoleProprietorRegistration() == FeatureSwitchingStates.ACTIVE.getKey();
                boolean businessEvolveProductsAvailable = featureSwitchingToggles.getBusinessEvolveIslamicRegistration() == FeatureSwitchingStates.ACTIVE.getKey() || featureSwitchingToggles.getBusinessEvolveStandardRegistration() == FeatureSwitchingStates.ACTIVE.getKey();
                if (soleProprietorRegistrationAvailable && businessEvolveProductsAvailable) {
                    isBusinessFlow = true;
                    if (featureSwitchingToggles.getBusinessEvolveIslamicRegistration() == FeatureSwitchingStates.ACTIVE.getKey()) {
                        newToBankPresenter.fetchBusinessEvolveIslamicAccount();
                    }

                    if (featureSwitchingToggles.getBusinessEvolveStandardRegistration() == FeatureSwitchingStates.ACTIVE.getKey()) {
                        newToBankPresenter.fetchBusinessEvolveStandardAccount();
                    }
                } else {
                    capabilityUnavailable(R.string.relationship_banking_feature_unavailable);
                }
                break;
            case 2:
                if (featureSwitchingToggles.getStudentAccountRegistration() == FeatureSwitchingStates.ACTIVE.getKey()) {
                    isStudentFlow = true;
                    NewToBankSelectCurrentLocationFragment selectCurrentLocationFragment = NewToBankSelectCurrentLocationFragment.newInstance();
                    startFragment(selectCurrentLocationFragment, true, AnimationType.FADE);
                } else {
                    capabilityUnavailable(R.string.student_account_banking_feature_unavailable);
                }
        }
    }

    @Override
    public void navigateToChooseYourProductFragment() {
        if (isBusinessFlow) {
            startFragment(BusinessChooseYourProductFragment.newInstance(), true, AnimationType.FADE);
        } else {
            startFragment(NewToBankChooseYourProductFragment.newInstance(), true, AnimationType.FADE);
        }
    }

    @Override
    public void navigateToBusinessWebsite() {
        startActivityIfAvailable(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.absa.co.za/business/")));
    }

    @Override
    public void navigateToStudentAccountBenefitsTerms() {
        Intent intent = IntentFactory.getStudentSilverAccountTermsAndConditionActivity(this);
        startActivityIfAvailable(intent);
    }

    @Override
    public void navigateToStudentBenefits() {
        startActivityIfAvailable(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.studentbenefits.co.za/")));
    }

    @Override
    public void navigateToBusinessBankingApplicationFees() {
        startActivityIfAvailable(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.absa.co.za/rates-and-fees/business-banking/")));
    }

    @Override
    public boolean isBusinessFlow() {
        return isBusinessFlow;
    }

    @Override
    public boolean isStudentFlow() {
        return isStudentFlow;
    }

    @Override
    public boolean isFromBusinessAccountActivity() {
        return isFromBusinessAccountActivity;
    }

    @Override
    public void navigateToAboutYourBusinessFragment() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        AboutYourBusinessFragment aboutYourBusinessFragment = new AboutYourBusinessFragment();
        startFragment(aboutYourBusinessFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToConfirmBusinessAddressFragment() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        ConfirmBusinessAddressFragment confirmBusinessAddressFragment = new ConfirmBusinessAddressFragment();
        startFragment(confirmBusinessAddressFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToAddBusinessAddressFragment() {
        BusinessAddressFragment businessAddressFragment = new BusinessAddressFragment();
        startFragment(businessAddressFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToSelectPreferredBusinessBankBranchFragment() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        SelectBusinessBankingBranchFragment selectBusinessBankingBranchFragment = new SelectBusinessBankingBranchFragment();
        startFragment(selectBusinessBankingBranchFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToFilteredList(FilteredListObject filteredListObject) {
        this.filteredListObject = filteredListObject;
        BusinessBankingFilteredListFragment businessBankingFilteredListFragment = new BusinessBankingFilteredListFragment(filteredListObject);
        businessBankingFilteredListFragment.setTargetFragment(filteredListObject.getCallingFragment(), filteredListObject.getNormalInputViewRequestCode());
        startFragment(businessBankingFilteredListFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToFinancialDetailsFragment() {
        newToBankBinding.progressIndicatorView.setNextStepWithIncrement();
        BusinessBankingFinancialDetailsFragment businessBankingFinancialDetailsFragment = new BusinessBankingFinancialDetailsFragment();
        startFragment(businessBankingFinancialDetailsFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToBusinessBankingSummaryFragment() {
        newToBankTempData.getBusinessCustomerPortfolio().setPhysicalAddressCountry("SO003");
        BusinessBankingApplicationSummaryFragment businessBankingApplicationSummaryFragment = new BusinessBankingApplicationSummaryFragment();
        startFragment(businessBankingApplicationSummaryFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToDocumentsUploadSuccessfully() {
        if (isStudentFlow()) {
            trackStudentAccount("StudentAccount_DocumentsUploadedSuccessfullyScreen_ScreenDisplayed");
        } else {
            trackSoleProprietorCurrentFragment("SoleProprietor_DocumentsUploadedSuccessfullyScreen_ScreenDisplayed");
        }

        navigateToGenericResultFragment("Your account will only become active once your new address has been verified. Please continue with your application", ResultAnimations.generalSuccess,
                "Documents uploaded successfully", false,
                getString(R.string.continue_button), v -> navigateToSelectPreferredBusinessBankBranchFragment(), false);
    }

    @Override
    public void navigateToGetBusinessBankingCardFragment() {
        BusinessBankingGetCardBranchFragment businessBankingGetCardBranchFragment = new BusinessBankingGetCardBranchFragment();
        startFragment(businessBankingGetCardBranchFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToSureCheckRejectScreen() {
        navigateToGenericResultFragment(false, false, getString(R.string.relationship_banking_surecheck_rejected_message), ResultAnimations.generalFailure);
    }

    @Override
    public void navigateToSilverStudentWhatYouDoFragment() {
        NewToBankWhatYouDoFragment newToBankWhatYouDoFragment = new NewToBankWhatYouDoFragment();
        startFragment(newToBankWhatYouDoFragment, true, AnimationType.FADE);
    }

    @Override
    public void trackSoleProprietorCurrentFragment(String fragment) {
        AnalyticsUtil.INSTANCE.trackAction(SOLE_PROPRIETOR_ANALYTICS_CHANNEL, fragment);
    }

    @Override
    public void trackStudentAccount(String tag) {
        AnalyticsUtil.INSTANCE.trackAction(STUDENT_ACCOUNT_ANALYTICS_CHANNEL, tag);
    }

    @Override
    public void navigateToChooseAccountScreen() {
        ChooseYourAccountFragment startChooseYourAccountFragment = new ChooseYourAccountFragment();
        startFragment(startChooseYourAccountFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToStartApplicationFragment() {
        showToolbar();
        NewToBankStartApplicationFragment newToBankStartApplicationFragment = new NewToBankStartApplicationFragment(this);
        startFragment(newToBankStartApplicationFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToOptionalAccountExtrasFragment() {
        showToolbar();
        NewToBankOptionalAccountExtrasFragment newToBankOptionalAccountExtrasFragment = new NewToBankOptionalAccountExtrasFragment();
        startFragment(newToBankOptionalAccountExtrasFragment, true, AnimationType.FADE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ID_BOOK_REQUEST_CODE) {
                byte[] bytes = data.getByteArrayExtra(TIM_IMAGE);
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                String idNumber = data.getStringExtra(TIM_ID_NUMBER);
                uploadIdBookPhoto(image, idNumber);
            } else if (requestCode == ID_CARD_REQUEST_CODE) {
                byte[] bytes = data.getByteArrayExtra(TIM_FRONT);
                Bitmap front = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                byte[] bytes2 = data.getByteArrayExtra(TIM_BACK);
                Bitmap back = BitmapFactory.decodeByteArray(bytes2, 0, bytes2.length);
                String idNumber = data.getStringExtra(TIM_ID_NUMBER);
                uploadIdCardPhotos(front, back, idNumber);
            } else if (requestCode == PERSONAL_ACCOUNT_REQUEST) {
                navigateSelectCurrentLocationFragment();
            }
        } else if (resultCode == Activity.RESULT_FIRST_USER) {
            trackCustomAction(NewToBankConstants.ID_FAILED_ERROR);
            navigateToGenericResultFragment(false, false, getString(R.string.new_to_bank_scan_failure), ResultAnimations.generalError, getString(R.string.something_went_wrong));
        }
    }

    private void capabilityUnavailable(@StringRes int subMessage) {
        Intent intent = new Intent(this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IS_ERROR, true);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.feature_unavailable);
        intent.putExtra(GenericResultActivity.SUB_MESSAGE, subMessage);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_TYPE_SECONDARY, true);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.close);
        GenericResultActivity.bottomOnClickListener = v -> navigateToWelcomePage();
        startActivity(intent);
    }

    private void navigateToWelcomePage() {
        Intent landingPageIntent = new Intent(this, WelcomeActivity.class);
        landingPageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(landingPageIntent);
    }
}