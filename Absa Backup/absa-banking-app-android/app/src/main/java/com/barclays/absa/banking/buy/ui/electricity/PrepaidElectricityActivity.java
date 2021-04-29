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
package com.barclays.absa.banking.buy.ui.electricity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.MeterNumberObject;
import com.barclays.absa.banking.boundary.model.PurchasePrepaidElectricityResultObject;
import com.barclays.absa.banking.boundary.model.TransactionObject;
import com.barclays.absa.banking.boundary.model.prepaidElectricity.PrepaidElectricity;
import com.barclays.absa.banking.databinding.PrepaidElectricityActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.KeyboardUtils;
import com.barclays.absa.utils.ViewAnimation;

import org.jetbrains.annotations.NotNull;

import styleguide.screens.GenericResultScreenFragment;
import styleguide.screens.GenericResultScreenProperties;

public class PrepaidElectricityActivity extends BaseActivity implements PrepaidElectricityView {
    private static final int ANIMATION_DURATION = 200;

    static final String BUY_ELECTRICITY = "Buy Electricity";

    private static final String PURCHASE_PREPAID_ELECTRICITY_RESULT_OBJECT = "purchasePrepaidElectricityResultObject";
    private static final String BENEFICIARY_DETAIL_OBJECT = "beneficiaryDetailObject";
    private static final String PREPAID_ELECTRICITY_RESPONSE = "prepaidElectricityResponse";
    private static final String SELECTED_HISTORY_TRANSACTION_OBJECT = "selectedHistoryTransactionObject";
    private static final String ELECTRICITY_BENEFICIARY_DETAILS = "prepaidElectricityBeneficiaryDetails";
    public static final String ADD_PREPAID_ELECTRICITY_BENEFICIARY = "addPrepaidElectricityBeneficiaryManageBeneficiaryFlow";
    private static final String VIEW_PREPAID_ELECTRICITY_BENEFICIARY_DETAILS = "viewPrepaidElectricityBeneficiaryDetails";

    private PrepaidElectricityPresenter buyElectricityPresenter;
    private PrepaidElectricityActivityBinding binding;

    private PrepaidElectricityRecipientMeterNumberFragment prepaidElectricityRecipientMeterNumberFragment;

    private PurchasePrepaidElectricityResultObject purchasePrepaidElectricityResultObject;
    private BeneficiaryListObject beneficiaryListObject;
    private BeneficiaryObject selectedBeneficiaryObject;
    private BeneficiaryDetailObject beneficiaryDetailObject;
    private PrepaidElectricity prepaidElectricityResponse;
    private TransactionObject selectedHistoryTransactionObject;
    private MeterNumberObject meterNumberObject;
    private ViewAnimation toolbarAnimation;
    private final BeneficiariesObservable beneficiariesObservable = new BeneficiariesObservable();
    private boolean isFromManageBeneficiaryHub;
    private final IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.prepaid_electricity_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(R.string.title_prepaid_electricity);
        toolbarAnimation = new ViewAnimation(binding.toolbar.toolbar);

        mScreenName = BUY_PREPAID_ELECTRICITY_CONST;
        mSiteSection = BUY_PREPAID_ELECTRICITY_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BUY_PREPAID_ELECTRICITY_CONST, BUY_PREPAID_ELECTRICITY_CONST, TRUE_CONST);
        buyElectricityPresenter = new PrepaidElectricityPresenter(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null && (extras.get(ELECTRICITY_BENEFICIARY_DETAILS) != null || extras.getBoolean(ADD_PREPAID_ELECTRICITY_BENEFICIARY))) {
            isFromManageBeneficiaryHub = extras.getBoolean(ADD_PREPAID_ELECTRICITY_BENEFICIARY);
            if (isFromManageBeneficiaryHub) {
                navigateToRecipientMeterNumberFragment();
            } else if (extras.get(ELECTRICITY_BENEFICIARY_DETAILS) != null) {
                isFromManageBeneficiaryHub = extras.getBoolean(VIEW_PREPAID_ELECTRICITY_BENEFICIARY_DETAILS);
                meterNumberObject = (MeterNumberObject) extras.get(ELECTRICITY_BENEFICIARY_DETAILS);
                beneficiaryDetailObject = (BeneficiaryDetailObject) extras.get(BENEFICIARY_DETAIL_OBJECT);
                navigateToPurchaseDetailsFragment();
            }
        } else if (savedInstanceState == null) {
            startFragment(PrepaidElectricityBeneficiariesFragment.newInstance(), true, AnimationType.NONE);
            if (AbsaCacheManager.getInstance().isBeneficiariesCached(BMBConstants.PASS_PREPAID_ELECTRICITY)) {
                setBeneficiariesFragment(AbsaCacheManager.getInstance().getCachedBeneficiaryListObject());
            } else {
                requestBeneficiaries();
            }
        }
        getDeviceProfilingInteractor().notifyAddBeneficiary();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getCurrentFragment();

        if (currentFragment instanceof PrepaidElectricityPurchaseReceiptFragment) {
            setToolbarIcon(R.drawable.ic_arrow_back_dark);
            super.onBackPressed();
        } else if (currentFragment == null || binding.toolbar.toolbar.getVisibility() != View.VISIBLE || currentFragment instanceof PrepaidElectricityBeneficiariesFragment || currentFragment instanceof GenericResultScreenFragment) {
            finish();
        } else if (currentFragment instanceof PrepaidElectricityPurchaseDetailsFragment && isFromManageBeneficiaryHub) {
            finish();
        } else if (currentFragment instanceof PrepaidElectricityPurchaseDetailsFragment) {
            restartActivity();
        } else {
            setToolbarIcon(R.drawable.ic_arrow_back_dark);
            super.onBackPressed();
        }
    }

    @Override
    public void doneClicked() {
        loadAccountsAndGoHome();
    }

    private void restartActivity() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.electricity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void requestBeneficiaries() {
        buyElectricityPresenter.requestBeneficiaryList();
    }

    @Override
    public void navigateToProblemWithMeterNumberActivity() {
        View.OnClickListener primaryButtonClick = v -> finish();
        hideToolbar();
        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setDescription(getString(R.string.try_again_later))
                .setTitle(getString(R.string.problem_with_meter))
                .setPrimaryButtonLabel(getString(R.string.home))
                .setResultScreenAnimation("general_failure.json")
                .build(false);

        startFragment(GenericResultScreenFragment.newInstance(resultScreenProperties, true, primaryButtonClick, null), true, AnimationType.FADE);
    }

    @Override
    public void navigateToRecipientMeterNumberFragment() {
        selectedBeneficiaryObject = null;
        prepaidElectricityRecipientMeterNumberFragment = new PrepaidElectricityRecipientMeterNumberFragment();
        startFragment(prepaidElectricityRecipientMeterNumberFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToSomeoneNewBeneficiaryDetailsFragment() {
        PrepaidElectricitySomeoneNewMeterDetailsFragment someoneNewBeneficiaryDetailsFragment = new PrepaidElectricitySomeoneNewMeterDetailsFragment();
        startFragment(someoneNewBeneficiaryDetailsFragment, true, AnimationType.FADE);
    }

    @Override
    public void navigateToPurchaseDetailsFragment() {
        startFragment(PrepaidElectricityPurchaseDetailsFragment.newInstance(getSelectedBeneficiaryObject(), getBeneficiaryDetailObject(), getMeterNumberObject()), true, AnimationType.FADE);
    }

    @Override
    public void navigateToBeneficiaryAddedResultScreen() {
        View.OnClickListener primaryButtonClick = v -> finish();

        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_success.json")
                .setTitle(getString(R.string.beneficiary_added_successfully))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true);

        GenericResultScreenFragment genericResultScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, true, primaryButtonClick, null);
        startFragment(genericResultScreenFragment, true, AnimationType.FADE);
    }

    @Override
    public void setBeneficiariesFragment(BeneficiaryListObject beneficiaryListObject) {
        beneficiariesObservable.onBeneficiariesLoaded(this.beneficiaryListObject = beneficiaryListObject);
    }

    @Override
    public void navigateToConfirmPurchaseFragment(PurchasePrepaidElectricityResultObject purchasePrepaidElectricityResultObject) {
        this.purchasePrepaidElectricityResultObject = purchasePrepaidElectricityResultObject;
        KeyboardUtils.hideKeyboard(this);
        startFragment(PrepaidElectricityConfirmPurchaseFragment.newInstance(purchasePrepaidElectricityResultObject), true, AnimationType.FADE);
    }

    @Override
    public void navigateToImportantInformationFragment() {
        startFragment(PrepaidElectricityImportantInformationFragment.newInstance(), true, AnimationType.FADE);
    }

    @Override
    public void navigateToPurchaseReceiptFragment() {
        startFragment(PrepaidElectricityPurchaseReceiptFragment.newInstance(
                getBeneficiaryDetailObject(),
                getPurchasePrepaidElectricityResultObject(),
                getPrepaidElectricityResponse(),
                getSelectedHistoryTransaction()
        ), true, AnimationType.FADE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BENEFICIARY_DETAIL_OBJECT, beneficiaryDetailObject);
        outState.putSerializable(PURCHASE_PREPAID_ELECTRICITY_RESULT_OBJECT, purchasePrepaidElectricityResultObject);
        outState.putSerializable(PREPAID_ELECTRICITY_RESPONSE, prepaidElectricityResponse);
        outState.putSerializable(SELECTED_HISTORY_TRANSACTION_OBJECT, selectedHistoryTransactionObject);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle inState) {
        super.onRestoreInstanceState(inState);
        beneficiaryDetailObject = (BeneficiaryDetailObject) inState.getSerializable(BENEFICIARY_DETAIL_OBJECT);
        purchasePrepaidElectricityResultObject = (PurchasePrepaidElectricityResultObject) inState.getSerializable(PURCHASE_PREPAID_ELECTRICITY_RESULT_OBJECT);
        prepaidElectricityResponse = (PrepaidElectricity) inState.getSerializable(PREPAID_ELECTRICITY_RESPONSE);
        selectedHistoryTransactionObject = (TransactionObject) inState.getSerializable(SELECTED_HISTORY_TRANSACTION_OBJECT);
    }

    @Override
    public void navigateToPurchaseHistoryFragment() {
        startFragment(PrepaidElectricityPurchaseHistoryFragment.newInstance(getBeneficiaryListObject()), true, AnimationType.FADE);
    }

    @Override
    public void navigateToPurchaseHistorySelectedFragment() {
        startFragment(PrepaidElectricityPurchaseHistorySelectedFragment.newInstance(getBeneficiaryDetailObject()), true, AnimationType.FADE);
    }

    @Override
    public void navigateToNeedHelpFragment() {
        startFragment(new PrepaidElectricityNeedHelpFragment(), true, AnimationType.FADE);
    }

    @Override
    public void saveMeterNumberObject(MeterNumberObject meterNumberObject) {
        this.meterNumberObject = meterNumberObject;
    }

    @Override
    public MeterNumberObject getMeterNumberObject() {
        return meterNumberObject;
    }

    @Override
    public void setToolbarTitle(String toolbarTitle) {
        setToolBarBack(toolbarTitle);
    }

    @Override
    public void setToolbarTitle(String title, View.OnClickListener onClickListener) {
        setToolBarBack(title, onClickListener);
    }

    @Override
    public void setToolbarIcon(int icon) {
        getToolBar().setHomeAsUpIndicator(icon);
    }

    @Override
    public void getMeterHistory(BeneficiaryObject beneficiaryObject) {
        this.selectedBeneficiaryObject = beneficiaryObject;
        buyElectricityPresenter.fetchHistoryForMeterNumber(beneficiaryObject.getBeneficiaryAccountNumber());
    }

    @Override
    public void showRecipientMeterError(String errorMessage) {
        if (prepaidElectricityRecipientMeterNumberFragment != null && errorMessage != null) {
            prepaidElectricityRecipientMeterNumberFragment.showMeterError(errorMessage);
        } else {
            showGenericErrorMessage();
        }
    }

    @Override
    public void validateRecipientMeterNumber(String meterNumber) {
        buyElectricityPresenter.validateRecipientMeterNumber(meterNumber, isFromManageBeneficiaryHub);
    }

    @Override
    public void validateExistingMeterNumber(String meterNumber, BeneficiaryObject
            beneficiaryObject) {
        this.selectedBeneficiaryObject = beneficiaryObject;
        buyElectricityPresenter.validateMeterNumber(meterNumber);
    }

    @Override
    public void saveBeneficiaryDetailsObject(BeneficiaryDetailObject beneficiaryDetailObject) {
        this.beneficiaryDetailObject = beneficiaryDetailObject;
    }

    @Override
    public void addPrepaidElectricityBeneficiary(String selectedValue, String meterNumber, String utility) {
        buyElectricityPresenter.addPrepaidElectricityBeneficiary(selectedValue, meterNumber, utility);
    }

    @Override
    public void showBeneficiaryExistDialog() {
        showMessage(getString(R.string.ben_exist_dialog_title), getString(R.string.ben_exist_dialog_msg), (dialog, which) -> {
            beneficiaryCacheService.setTabPositionToReturnTo("PPE");
            finish();
        });
    }

    @Override
    public void setupSureCheckDelegate(@NotNull SureCheckDelegate buyElectricitySureCheckDelegate) {
        buyElectricityPresenter.setUpSureCheckDelegate(buyElectricitySureCheckDelegate);
    }

    @Override
    public void confirmElectricityPurchase() {
        buyElectricityPresenter.confirmPurchase(purchasePrepaidElectricityResultObject);
    }

    @Override
    public BeneficiaryListObject getBeneficiaryListObject() {
        return beneficiaryListObject;
    }

    @Override
    public BeneficiaryObject getSelectedBeneficiaryObject() {
        return selectedBeneficiaryObject;
    }

    @Override
    public PurchasePrepaidElectricityResultObject getPurchasePrepaidElectricityResultObject() {
        return purchasePrepaidElectricityResultObject;
    }

    @Override
    public BeneficiaryDetailObject getBeneficiaryDetailObject() {
        return beneficiaryDetailObject;
    }

    @Override
    public void navigateToPurchaseSuccessfulFragment(PrepaidElectricity prepaidElectricityResponse) {
        this.prepaidElectricityResponse = prepaidElectricityResponse;
        startFragment(new PrepaidElectricityPurchaseSuccessFragment(), true, AnimationType.FADE);
    }

    @Override
    public void navigateToPurchaseUnsuccessfulFragment(PrepaidElectricity prepaidElectricityResponse) {
        AnalyticsUtil.INSTANCE.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_PurchaseFailureScreen_ScreenDisplayed");

        View.OnClickListener primaryButtonClick = v -> loadAccountsAndGoHome();
        GenericResultScreenProperties resultScreenProperties = new GenericResultScreenProperties.PropertiesBuilder()
                .setDescription(getString(R.string.prepaid_electricity_unsuccessful_description))
                .setTitle(getString(R.string.prepaid_electricity_unsuccessful_purchase))
                .setPrimaryButtonLabel(getString(R.string.home))
                .setResultScreenAnimation("general_failure.json")
                .build(false);

        startFragment(GenericResultScreenFragment.newInstance(resultScreenProperties, true, primaryButtonClick, null), true, AnimationType.FADE);
    }

    @Override
    public void hideToolbar() {
        toolbarAnimation.collapseView(ANIMATION_DURATION);
    }

    @Override
    public void showToolbar() {
        toolbarAnimation.expandView(ANIMATION_DURATION);
    }

    @Override
    public PrepaidElectricity getPrepaidElectricityResponse() {
        return prepaidElectricityResponse;
    }

    @Override
    public void setSelectedHistoryTransaction(TransactionObject transactionObject) {
        this.selectedHistoryTransactionObject = transactionObject;
    }

    @Override
    public TransactionObject getSelectedHistoryTransaction() {
        return selectedHistoryTransactionObject;
    }

    public BeneficiariesObservable getBeneficiariesObservable() {
        return beneficiariesObservable;
    }

    @Override
    public void showDuplicatePurchaseErrorMessage() {
        Intent intent = IntentFactoryGenericResult.getAlertResultBuilder(this)
                .setGenericResultHeaderMessage(R.string.prepaid_electricity_duplicate_error_title)
                .setGenericResultSubMessage(R.string.prepaid_electricity_duplicate_error_message)
                .setGenericResultTopButton(R.string.buy_again, v -> {
                    preventDoubleClick(v);
                    ((BaseActivity) BMBApplication.getInstance().getTopMostActivity()).showProgressDialog();
                    buyElectricityPresenter.fetchBeneficiaryDetails(getSelectedBeneficiaryObject().getBeneficiaryID(), true);
                })
                .setGenericResultBottomButton(R.string.home, v -> loadAccountsAndGoHome())
                .build();
        startActivityIfAvailable(intent);
    }

    @Override
    public void closeResultScreen() {
        Activity topMostActivity = BMBApplication.getInstance().getTopMostActivity();
        if (topMostActivity != null && topMostActivity.getClass().getSimpleName().equals(GenericResultActivity.class.getSimpleName())) {
            topMostActivity.finish();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this::navigateToPurchaseDetailsFragment, 50);
        }
    }

    @Override
    public void showTandemTimeoutErrorMessage() {
        Intent intent = IntentFactoryGenericResult.getAlertResultBuilder(this)
                .setGenericResultHeaderMessage(R.string.temporary_error)
                .setGenericResultSubMessage(R.string.temporary_error_message)
                .setGenericResultDoneButton(this, (v) -> loadAccountsAndGoHome())
                .build();
        startActivityIfAvailable(intent);
    }
}