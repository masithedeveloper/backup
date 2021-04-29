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
package com.barclays.absa.banking.card.ui.creditCard.vcl;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardInformation;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.databinding.CreditCardVclBaseActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.KeyboardUtils;
import com.barclays.absa.utils.TextFormatUtils;

import org.jetbrains.annotations.NotNull;

import static com.barclays.absa.banking.card.ui.creditCard.vcl.BaseVCLFragment.CREDIT_CARD_VCL_FEATURE_NAME;

public class CreditCardVCLBaseActivity extends BaseActivity implements CreditCardVCLView {
    public static final String SAVE_STATE_VCL_MODEL = "saveStateVclModel";
    public static final String VCL_DATA = "vcl_data";
    public static final String IS_FROM_CREDIT_CARD_HUB = "IS_FROM_CARD_HUB";
    public static final String IS_FROM_EXPLORE = "is_from_explore";
    public static final double CREDIT_LIMIT_INCREASE_AMOUNT = 500;
    public static final int STEPS = 3;
    private boolean isFromCreditCardHub;
    private boolean isFromExplore;
    private boolean showCancelToolbar;
    private CreditCardVclBaseActivityBinding binding;
    private VCLParcelableModel vclDataModel;
    protected BaseBackPressedListener onBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.credit_card_vcl_base_activity, null, false);
        setContentView(binding.getRoot());
        if (savedInstanceState != null) {
            vclDataModel = savedInstanceState.getParcelable(SAVE_STATE_VCL_MODEL);
        } else {
            updateVCLDataModel(getIntent().getParcelableExtra(VCL_DATA));
        }

        if (vclDataModel != null) {
            if (vclDataModel.getCreditCardVCLGadget() != null && vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses() != null) {
                vclDataModel.setTotalFixedAmount(vclDataModel.getCreditCardVCLGadget().getIncomeAndExpenses().getTotalMonhtlyLivingExpenses());
            }
            vclDataModel.populateInstalmentAmounts();
        }
        isFromCreditCardHub = getIntent().getBooleanExtra(IS_FROM_CREDIT_CARD_HUB, false);
        isFromExplore = getIntent().getBooleanExtra(IS_FROM_EXPLORE, false);
        updateVCLDataModel(vclDataModel);
        if (savedInstanceState == null) {
            navigateToNextFragment(CreditCardVCLTermsAndConditionsFragment.newInstance());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVE_STATE_VCL_MODEL, vclDataModel);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setToolbarText(String toolbarText, boolean showCancelToolbar) {
        this.showCancelToolbar = showCancelToolbar;
        binding.toolbar.toolbar.setVisibility(View.VISIBLE);
        setToolBarBack(toolbarText);
    }

    @Override
    public void navigateToNextFragment(Fragment fragment) {
        hideKeyBoard();
        startFragment(fragment, R.id.fragmentContainerFrameLayout, true, AnimationType.FADE, true, fragment.getClass().getName());
    }

    @Override
    public void setCurrentProgress(int stepNumber) {
        binding.progressIndicator.setVisibility(View.VISIBLE);
        binding.progressIndicator.setSteps(STEPS);
        binding.progressIndicator.setNextStep(stepNumber);
    }

    @Override
    public void hideKeyBoard() {
        KeyboardUtils.hideSoftKeyboard(binding.fragmentContainerFrameLayout);
    }

    public void hideProgressIndicator() {
        binding.progressIndicator.setVisibility(View.GONE);
    }

    @Override
    public void updateVCLDataModel(VCLParcelableModel vclDataModel) {
        this.vclDataModel = vclDataModel;
    }

    @Override
    public VCLParcelableModel getVCLDataModel() {
        return vclDataModel;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener.doBack();
        }
        navigateToPreviousScreen();
    }

    public void setOnBackPressedListener(BaseBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void navigateToFailureScreen() {
        AnalyticsUtil.INSTANCE.trackAction(CREDIT_CARD_VCL_FEATURE_NAME, "Credit Card VCL failure");
        startActivity(IntentFactory.getVCLIncreaseFailedResultScreen(this));
    }

    @Override
    public void navigateToInvalidAccountScreen() {
        AnalyticsUtil.INSTANCE.trackAction(CREDIT_CARD_VCL_FEATURE_NAME, "Credit Card VCL Invalid Account");
        startActivity(IntentFactory.getVCLIncreaseInvalidNumberResultScreen(this));
    }

    @Override
    public void navigateToPreviousScreen() {
        hideKeyBoard();
        if (getSupportFragmentManager().findFragmentById(R.id.fragmentContainerFrameLayout) instanceof CreditCardVCLIncreaseConfirmationFragment) {
            showToolBar();
        }
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        } else {
            removeFragments(1);
        }
    }

    @Override
    public void hideToolbar() {
        binding.toolbar.toolbar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showCancelToolbar) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.combined_cancel_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (showCancelToolbar) {
            if (item.getItemId() == R.id.cancel) {
                BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                        .message(getString(R.string.vcl_cancel_text))
                        .positiveDismissListener((dialog, which) -> navigateBackToCreditCardHub()));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateBackToCreditCardHub() {
        CreditCardInformation creditCard = getAppCacheService().getCreditCardInformation();
        if (creditCard != null && creditCard.getAccount() != null && creditCard.getAccount().getAccountNo() != null) {
            Intent intent = new Intent(this, CreditCardHubActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(CreditCardHubActivity.ACCOUNT_NUMBER, creditCard.getAccount().getAccountNo());
            startActivity(intent);
        } else {
            loadAccountsAndGoHome();
        }
    }

    @Override
    public void creditLimitIncreaseRejected() {
        AnalyticsUtil.INSTANCE.trackAction(CREDIT_CARD_VCL_FEATURE_NAME, "Credit Card VCL Rejected");
        startActivity(IntentFactory.getVCLRejectedResultScreen(this, isFromCreditCardHub, isFromExplore, vclDataModel.getCreditCardNumber()));
    }

    @Override
    public void navigateToVCLAcceptedScreen() {
        AnalyticsUtil.INSTANCE.trackAction(CREDIT_CARD_VCL_FEATURE_NAME, "Credit Card VCL Approved");
        startActivity(IntentFactory.getVCLIncreaseAcceptedResultScreen(this, TextFormatUtils.formatBasicAmount(vclDataModel.getCreditLimitIncreaseAmount())));
    }

    @Override
    public void navigateToVCLPendingScreen() {
        AnalyticsUtil.INSTANCE.trackAction(CREDIT_CARD_VCL_FEATURE_NAME, "Credit Card VCL Pending");
        startActivity(IntentFactory.getVCLIncreasePendingResultScreen(this, TextFormatUtils.formatBasicAmount(vclDataModel.getCreditLimitIncreaseAmount())));
    }

    @Override
    public void navigateToVCLDeclineScreen() {
        AnalyticsUtil.INSTANCE.trackAction(CREDIT_CARD_VCL_FEATURE_NAME, "Credit Card VCL Declined");
        startActivity(IntentFactory.getVCLIncreaseDeclinedResultScreen(this));
    }
}
